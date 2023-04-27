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
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.rolling.RollingPolicy;
import ch.qos.logback.core.rolling.RollingPolicyBase;
import ch.qos.logback.core.rolling.RolloverFailure;
import ch.qos.logback.core.rolling.TriggeringPolicy;
import ch.qos.logback.core.rolling.helper.CompressionMode;
import ch.qos.logback.core.rolling.helper.FileNamePattern;
import ch.qos.logback.core.util.ContextUtil;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import static ch.qos.logback.core.CoreConstants.CODES_URL;
import static ch.qos.logback.core.CoreConstants.MORE_INFO_PREFIX;

*/
/**
 * <code>RollingFileAppender</code> extends {@link FileAppender} to backup the
 * log files depending on {@link RollingPolicy} and {@link TriggeringPolicy}.
 * <p/>
 * <p/>
 * For more information about this appender, please refer to the online manual
 * at http://logback.qos.ch/manual/appenders.html#RollingFileAppender
 *
 * @author Heinz Richter
 * @author Ceki G&uuml;lc&uuml;
 *//*

public class RollingFileAppender1<E> extends FileAppender<E> {
    File currentlyActiveFile;
    TriggeringPolicy<E> triggeringPolicy;
    RollingPolicy rollingPolicy;

    static private String RFA_NO_TP_URL = CODES_URL + "#rfa_no_tp";
    static private String RFA_NO_RP_URL = CODES_URL + "#rfa_no_rp";
    static private String COLLISION_URL = CODES_URL + "#rfa_collision";
    static private String RFA_LATE_FILE_URL = CODES_URL + "#rfa_file_after";

    public void start() {
        if (triggeringPolicy == null) {
            addWarn("No TriggeringPolicy was set for the RollingFileAppender named " + getName());
            addWarn(MORE_INFO_PREFIX + RFA_NO_TP_URL);
            return;
        }
        if (!triggeringPolicy.isStarted()) {
            addWarn("TriggeringPolicy has not started. RollingFileAppender will not start");
            return;
        }

        if (checkForCollisionsInPreviousRollingFileAppenders()) {
            addError("Collisions detected with FileAppender/RollingAppender instances defined earlier. Aborting.");
            addError(MORE_INFO_PREFIX + COLLISION_WITH_EARLIER_APPENDER_URL);
            return;
        }

        // we don't want to void existing log files
        if (!append) {
            addWarn("Append mode is mandatory for RollingFileAppender. Defaulting to append=true.");
            append = true;
        }

        if (rollingPolicy == null) {
            addError("No RollingPolicy was set for the RollingFileAppender named " + getName());
            addError(MORE_INFO_PREFIX + RFA_NO_RP_URL);
            return;
        }

        // sanity check for http://jira.qos.ch/browse/LOGBACK-796
        if (checkForFileAndPatternCollisions()) {
            addError("File property collides with fileNamePattern. Aborting.");
            addError(MORE_INFO_PREFIX + COLLISION_URL);
            return;
        }

        if (isPrudent()) {
            if (rawFileProperty() != null) {
                addWarn("Setting \"File\" property to null on account of prudent mode");
                setFile(null);
            }
            if (rollingPolicy.getCompressionMode() != CompressionMode.NONE) {
                addError("Compression is not supported in prudent mode. Aborting");
                return;
            }
        }

        currentlyActiveFile = new File(getFile());
        addInfo("Active log file name: " + getFile());
        super.start();
    }

    private boolean checkForFileAndPatternCollisions() {
        if (triggeringPolicy instanceof RollingPolicyBase) {
            final RollingPolicyBase base = (RollingPolicyBase) triggeringPolicy;
            final FileNamePattern fileNamePattern = base.fileNamePattern;
            // no use checking if either fileName or fileNamePattern are null
            if (fileNamePattern != null && fileName != null) {
                String regex = fileNamePattern.toRegex();
                return fileName.matches(regex);
            }
        }
        return false;
    }

    private boolean checkForCollisionsInPreviousRollingFileAppenders() {
        boolean collisionResult = false;
        if (triggeringPolicy instanceof RollingPolicyBase) {
            final RollingPolicyBase base = (RollingPolicyBase) triggeringPolicy;
            final FileNamePattern fileNamePattern = base.fileNamePattern;
            boolean collisionsDetected = innerCheckForFileNamePatternCollisionInPreviousRFA(fileNamePattern);
            if (collisionsDetected)
                collisionResult = true;
        }
        return collisionResult;
    }

    private boolean innerCheckForFileNamePatternCollisionInPreviousRFA(FileNamePattern fileNamePattern) {
        boolean collisionsDetected = false;
        @SuppressWarnings("unchecked")
        Map<String, FileNamePattern> map = (Map<String, FileNamePattern>) context.getObject(CoreConstants.RFA_FILENAME_PATTERN_COLLISION_MAP);
        if (map == null) {
            return collisionsDetected;
        }
        for (Entry<String, FileNamePattern> entry : map.entrySet()) {
            if (fileNamePattern.equals(entry.getValue())) {
                addErrorForCollision("FileNamePattern", entry.getValue().toString(), entry.getKey());
                collisionsDetected = true;
            }
        }
        if (name != null) {
            map.put(getName(), fileNamePattern);
        }
        return collisionsDetected;
    }

    @Override
    public void stop() {
        super.stop();
        
        if (rollingPolicy != null)
            rollingPolicy.stop();
        if (triggeringPolicy != null)
            triggeringPolicy.stop();

        Map<String, FileNamePattern> map = ContextUtil.getFilenamePatternCollisionMap(context);
        if (map != null && getName() != null)
            map.remove(getName());

    }

    @Override
    public void setFile(String file) {
        // http://jira.qos.ch/browse/LBCORE-94
        // allow setting the file name to null if mandated by prudent mode
        if (file != null && ((triggeringPolicy != null) || (rollingPolicy != null))) {
            addError("File property must be set before any triggeringPolicy or rollingPolicy properties");
            addError(MORE_INFO_PREFIX + RFA_LATE_FILE_URL);
        }
        super.setFile(file);
    }

    @Override
    public String getFile() {
        return rollingPolicy.getActiveFileName();
    }

    */
/**
     * Implemented by delegating most of the rollover work to a rolling policy.
     *//*

    public void rollover() {
        // 上锁. 关流和打开文件必须在同一个代码块内, 不关流(打开的文件)无法完成重命名
        lock.lock();
        try {
            // 关闭输出流. 不展开
            this.closeOutputStream();
            // 核心代码: 尝试滚动文件
            attemptRollover();
            // 打开新文件, 设置输出流. 不展开
            attemptOpenFile();
        } finally {
            lock.unlock();
        }
    }

    private void attemptOpenFile() {
        try {
            // update the currentlyActiveFile LOGBACK-64
            currentlyActiveFile = new File(rollingPolicy.getActiveFileName());

            // This will also close the file. This is OK since multiple close operations are safe.
            this.openFile(rollingPolicy.getActiveFileName());
        } catch (IOException e) {
            addError("setFile(" + fileName + ", false) call failed.", e);
        }
    }

    private void attemptRollover() {
        try {
            // 滚动
            rollingPolicy.rollover();
        } catch (RolloverFailure rf) {
            addWarn("RolloverFailure occurred. Deferring roll-over.");
            this.append = true;
        }
    }

    */
/**
    * This method differentiates RollingFileAppender from its super class.
    *//*

    @Override
    protected void subAppend(E event) {
        // 同步代码块确保判断和滚动时线程安全
        synchronized (triggeringPolicy) {
            // 重要代码: 判断是否达到滚动时机
            if (triggeringPolicy.isTriggeringEvent(currentlyActiveFile, event)) {
                // 重要代码: 滚动文件
                rollover();
            }
        }
        // 核心代码: 执行父类代码进行日志输出
        super.subAppend(event);
    }

    public RollingPolicy getRollingPolicy() {
        return rollingPolicy;
    }

    public TriggeringPolicy<E> getTriggeringPolicy() {
        return triggeringPolicy;
    }

    */
/**
     * Sets the rolling policy. In case the 'policy' argument also implements
     * {@link TriggeringPolicy}, then the triggering policy for this appender is
     * automatically set to be the policy argument.
     *
     * @param policy
     *//*

    @SuppressWarnings("unchecked")
    public void setRollingPolicy(RollingPolicy policy) {
        rollingPolicy = policy;
        if (rollingPolicy instanceof TriggeringPolicy) {
            triggeringPolicy = (TriggeringPolicy<E>) policy;
        }

    }

    public void setTriggeringPolicy(TriggeringPolicy<E> policy) {
        triggeringPolicy = policy;
        if (policy instanceof RollingPolicy) {
            rollingPolicy = (RollingPolicy) policy;
        }
    }
}
*/
