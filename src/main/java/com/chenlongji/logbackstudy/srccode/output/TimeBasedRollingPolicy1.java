/**
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 1999-2015, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 *//*

package com.chenlongji.logbackstudy.srccode.output;

import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.rolling.*;
import ch.qos.logback.core.rolling.helper.*;
import ch.qos.logback.core.util.FileSize;

import java.io.File;
import java.util.Date;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static ch.qos.logback.core.CoreConstants.UNBOUNDED_TOTAL_SIZE_CAP;
import static ch.qos.logback.core.CoreConstants.UNBOUND_HISTORY;

*/
/**
 * <code>TimeBasedRollingPolicy</code> is both easy to configure and quite
 * powerful. It allows the roll over to be made based on time. It is possible to
 * specify that the roll over occur once per day, per week or per month.
 * 
 * <p>For more information, please refer to the online manual at
 * http://logback.qos.ch/manual/appenders.html#TimeBasedRollingPolicy
 * 
 * @author Ceki G&uuml;lc&uuml;
 *//*

public class TimeBasedRollingPolicy1<E> extends RollingPolicyBase implements TriggeringPolicy<E> {
    static final String FNP_NOT_SET = "The FileNamePattern option must be set before using TimeBasedRollingPolicy. ";
    // WCS: without compression suffix
    FileNamePattern fileNamePatternWithoutCompSuffix;

    private Compressor compressor;
    private RenameUtil renameUtil = new RenameUtil();
    Future<?> compressionFuture;
    Future<?> cleanUpFuture;

    private int maxHistory = UNBOUND_HISTORY;
    protected FileSize totalSizeCap = new FileSize(UNBOUNDED_TOTAL_SIZE_CAP);

    private ArchiveRemover archiveRemover;

    TimeBasedFileNamingAndTriggeringPolicy<E> timeBasedFileNamingAndTriggeringPolicy;

    boolean cleanHistoryOnStart = false;

    public void start() {
        // set the LR for our utility object
        renameUtil.setContext(this.context);

        // find out period from the filename pattern
        if (fileNamePatternStr != null) {
            fileNamePattern = new FileNamePattern(fileNamePatternStr, this.context);
            determineCompressionMode();
        } else {
            addWarn(FNP_NOT_SET);
            addWarn(CoreConstants.SEE_FNP_NOT_SET);
            throw new IllegalStateException(FNP_NOT_SET + CoreConstants.SEE_FNP_NOT_SET);
        }

        compressor = new Compressor(compressionMode);
        compressor.setContext(context);

        // wcs : without compression suffix
        fileNamePatternWithoutCompSuffix = new FileNamePattern(Compressor.computeFileNameStrWithoutCompSuffix(fileNamePatternStr, compressionMode), this.context);

        addInfo("Will use the pattern " + fileNamePatternWithoutCompSuffix + " for the active file");

        if (compressionMode == CompressionMode.ZIP) {
            String zipEntryFileNamePatternStr = transformFileNamePattern2ZipEntry(fileNamePatternStr);
            zipEntryFileNamePattern = new FileNamePattern(zipEntryFileNamePatternStr, context);
        }

        if (timeBasedFileNamingAndTriggeringPolicy == null) {
            timeBasedFileNamingAndTriggeringPolicy = new DefaultTimeBasedFileNamingAndTriggeringPolicy<E>();
        }
        timeBasedFileNamingAndTriggeringPolicy.setContext(context);
        timeBasedFileNamingAndTriggeringPolicy.setTimeBasedRollingPolicy(this);
        timeBasedFileNamingAndTriggeringPolicy.start();

        if (!timeBasedFileNamingAndTriggeringPolicy.isStarted()) {
            addWarn("Subcomponent did not start. TimeBasedRollingPolicy will not start.");
            return;
        }

        // the maxHistory property is given to TimeBasedRollingPolicy instead of to
        // the TimeBasedFileNamingAndTriggeringPolicy. This makes it more convenient
        // for the user at the cost of inconsistency here.
        if (maxHistory != UNBOUND_HISTORY) {
            archiveRemover = timeBasedFileNamingAndTriggeringPolicy.getArchiveRemover();
            archiveRemover.setMaxHistory(maxHistory);
            archiveRemover.setTotalSizeCap(totalSizeCap.getSize());
            if (cleanHistoryOnStart) {
                addInfo("Cleaning on start up");
                Date now = new Date(timeBasedFileNamingAndTriggeringPolicy.getCurrentTime());
                cleanUpFuture = archiveRemover.cleanAsynchronously(now);
            }
        } else if (!isUnboundedTotalSizeCap()) {
            addWarn("'maxHistory' is not set, ignoring 'totalSizeCap' option with value ["+totalSizeCap+"]");
        }

        super.start();
    }

    protected boolean isUnboundedTotalSizeCap() {
        return totalSizeCap.getSize() == UNBOUNDED_TOTAL_SIZE_CAP;
    }

    @Override
    public void stop() {
        if (!isStarted())
            return;
        waitForAsynchronousJobToStop(compressionFuture, "compression");
        waitForAsynchronousJobToStop(cleanUpFuture, "clean-up");
        super.stop();
    }

    private void waitForAsynchronousJobToStop(Future<?> aFuture, String jobDescription) {
        if (aFuture != null) {
            try {
                aFuture.get(CoreConstants.SECONDS_TO_WAIT_FOR_COMPRESSION_JOBS, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                addError("Timeout while waiting for " + jobDescription + " job to finish", e);
            } catch (Exception e) {
                addError("Unexpected exception while waiting for " + jobDescription + " job to finish", e);
            }
        }
    }

    private String transformFileNamePattern2ZipEntry(String fileNamePatternStr) {
        String slashified = FileFilterUtil.slashify(fileNamePatternStr);
        return FileFilterUtil.afterLastSlash(slashified);
    }

    public void setTimeBasedFileNamingAndTriggeringPolicy(TimeBasedFileNamingAndTriggeringPolicy<E> timeBasedTriggering) {
        this.timeBasedFileNamingAndTriggeringPolicy = timeBasedTriggering;
    }

    public TimeBasedFileNamingAndTriggeringPolicy<E> getTimeBasedFileNamingAndTriggeringPolicy() {
        return timeBasedFileNamingAndTriggeringPolicy;
    }

    public void rollover() throws RolloverFailure {
        // 取出需要滚动文件的替换文件名, 在前面isTriggeringEvent方法已经得到该名字了
        String elapsedPeriodsFileName = timeBasedFileNamingAndTriggeringPolicy.getElapsedPeriodsFileName();
        // 获取elapsedPeriodsFileName中最后"/"后的文件名
        String elapsedPeriodStem = FileFilterUtil.afterLastSlash(elapsedPeriodsFileName);
        // 配置的滚动文件名没有gz. zip后缀, 则不需要压缩
        if (compressionMode == CompressionMode.NONE) {
            // getParentsRawFileProperty()获取的是<file>标签体内的文件名
            if (getParentsRawFileProperty() != null) {
                // 直接修改文件名. 注意: 系统禁止的文件名符号重命名会失败, 例如window系统下":"
                renameUtil.rename(getParentsRawFileProperty(), elapsedPeriodsFileName);
            }
        } else {
            if (getParentsRawFileProperty() == null) {
                // 使用压缩器进行文件压缩. 不展开
                //    这里使用日志上下文中的线程池异步进行文件压缩
                //    gz包使用java的GZIPOutputStream压缩, zip包使用java的ZipOutputStream压缩
                compressionFuture = compressor.asyncCompress(elapsedPeriodsFileName, elapsedPeriodsFileName, elapsedPeriodStem);
            } else {
                // 先将滚动文件改为临时文件名, 再进行压缩(执行compressor.asyncCompress), 不展开
                compressionFuture = renameRawAndAsyncCompress(elapsedPeriodsFileName, elapsedPeriodStem);
            }
        }

        if (archiveRemover != null) {
            Date now = new Date(timeBasedFileNamingAndTriggeringPolicy.getCurrentTime());
            // 使用归档删除器将过期的文件删除掉, future用于appender销毁时阻塞线程. 不展开
            //   这里会使用日志上下文中的线程池异步进行文件删除.
            //   删除过程会调用clean方法删除超出时间范围内的文件(文件保留数为maxHistory+1), capTotalCap会删除超过总文件大小的旧文件(需配置maxHistory和totalSizeCap才有效)
            this.cleanUpFuture = archiveRemover.cleanAsynchronously(now);
        }
    }

    Future<?> renameRawAndAsyncCompress(String nameOfCompressedFile, String innerEntryName) throws RolloverFailure {
        String parentsRawFile = getParentsRawFileProperty();
        String tmpTarget = nameOfCompressedFile + System.nanoTime() + ".tmp";
        renameUtil.rename(parentsRawFile, tmpTarget);
        return compressor.asyncCompress(tmpTarget, nameOfCompressedFile, innerEntryName);
    }

    */
/**
     * 
     * The active log file is determined by the value of the parent's filename
     * option. However, in case the file name is left blank, then, the active log
     * file equals the file name for the current period as computed by the
     * <b>FileNamePattern</b> option.
     * 
     * <p>The RollingPolicy must know whether it is responsible for changing the
     * name of the active file or not. If the active file name is set by the user
     * via the configuration file, then the RollingPolicy must let it like it is.
     * If the user does not specify an active file name, then the RollingPolicy
     * generates one.
     * 
     * <p> To be sure that the file name used by the parent class has been
     * generated by the RollingPolicy and not specified by the user, we keep track
     * of the last generated name object and compare its reference to the parent
     * file name. If they match, then the RollingPolicy knows it's responsible for
     * the change of the file name.
     * 
     *//*

    public String getActiveFileName() {
        String parentsRawFileProperty = getParentsRawFileProperty();
        if (parentsRawFileProperty != null) {
            return parentsRawFileProperty;
        } else {
            return timeBasedFileNamingAndTriggeringPolicy.getCurrentPeriodsFileNameWithoutCompressionSuffix();
        }
    }

    public boolean isTriggeringEvent(File activeFile, final E event) {
        return timeBasedFileNamingAndTriggeringPolicy.isTriggeringEvent(activeFile, event);
    }

    */
/**
     * Get the number of archive files to keep.
     * 
     * @return number of archive files to keep
     *//*

    public int getMaxHistory() {
        return maxHistory;
    }

    */
/**
     * Set the maximum number of archive files to keep.
     * 
     * @param maxHistory
     *                number of archive files to keep
     *//*

    public void setMaxHistory(int maxHistory) {
        this.maxHistory = maxHistory;
    }

    public boolean isCleanHistoryOnStart() {
        return cleanHistoryOnStart;
    }

    */
/**
     * Should archive removal be attempted on application start up? Default is false.
     * @since 1.0.1
     * @param cleanHistoryOnStart
     *//*

    public void setCleanHistoryOnStart(boolean cleanHistoryOnStart) {
        this.cleanHistoryOnStart = cleanHistoryOnStart;
    }

    @Override
    public String toString() {
        return "c.q.l.core.rolling.TimeBasedRollingPolicy@"+this.hashCode();
    }

    public void setTotalSizeCap(FileSize totalSizeCap) {
        addInfo("setting totalSizeCap to "+totalSizeCap.toString());
        this.totalSizeCap = totalSizeCap;
    }
}
*/
