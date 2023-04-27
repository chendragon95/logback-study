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
 */
package com.chenlongji.logbackstudy.srccode;

import ch.qos.logback.core.Appender;
import ch.qos.logback.core.joran.GenericConfigurator;
import ch.qos.logback.core.joran.action.*;
import ch.qos.logback.core.joran.spi.ElementSelector;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.joran.spi.Interpreter;
import ch.qos.logback.core.joran.spi.RuleStore;

import java.util.HashMap;
import java.util.Map;

// Based on 310985 revision 310985 as attested by http://tinyurl.com/8njps
// see also http://tinyurl.com/c2rp5

/**
 * A JoranConfiguratorBase lays most of the groundwork for concrete
 * configurators derived from it. Concrete configurators only need to implement
 * the {@link #addInstanceRules} method.
 * <p>
 * A JoranConfiguratorBase instance should not be used more than once to
 * configure a Context.
 *
 * @author Ceki G&uuml;lc&uuml;
 */
abstract public class JoranConfiguratorBase1<E> extends GenericConfigurator {

    @Override
    protected void addInstanceRules(RuleStore rs) {
        rs.addRule(new ElementSelector("configuration/variable"), new PropertyAction());
        rs.addRule(new ElementSelector("configuration/property"), new PropertyAction());

        rs.addRule(new ElementSelector("configuration/substitutionProperty"), new PropertyAction());

        rs.addRule(new ElementSelector("configuration/timestamp"), new TimestampAction());
        rs.addRule(new ElementSelector("configuration/shutdownHook"), new ShutdownHookAction());
        rs.addRule(new ElementSelector("configuration/define"), new DefinePropertyAction());

        rs.addRule(new ElementSelector("configuration/contextProperty"), new ContextPropertyAction());

        rs.addRule(new ElementSelector("configuration/conversionRule"), new ConversionRuleAction());

        rs.addRule(new ElementSelector("configuration/statusListener"), new StatusListenerAction());

        // 添加标签<configuration>内的<appender>的解析规则AppenderAction
        rs.addRule(new ElementSelector("configuration/appender"), new AppenderAction<E>());
        rs.addRule(new ElementSelector("configuration/appender/appender-ref"), new AppenderRefAction<E>());
        rs.addRule(new ElementSelector("configuration/newRule"), new NewRuleAction());
        rs.addRule(new ElementSelector("*/param"), new ParamAction(getBeanDescriptionCache()));
    }

    @Override
    protected void addImplicitRules(Interpreter interpreter) {
        // The following line adds the capability to parse nested components
        NestedComplexPropertyIA nestedComplexPropertyIA = new NestedComplexPropertyIA(getBeanDescriptionCache());
        nestedComplexPropertyIA.setContext(context);
        interpreter.addImplicitAction(nestedComplexPropertyIA);

        NestedBasicPropertyIA nestedBasicIA = new NestedBasicPropertyIA(getBeanDescriptionCache());
        nestedBasicIA.setContext(context);
        interpreter.addImplicitAction(nestedBasicIA);
    }

    @Override
    protected void buildInterpreter() {
        super.buildInterpreter();
        Map<String, Object> omap = interpreter.getInterpretationContext().getObjectMap();
        omap.put(ActionConst.APPENDER_BAG, new HashMap<String, Appender<?>>());
        //omap.put(ActionConst.FILTER_CHAIN_BAG, new HashMap());
    }

    public InterpretationContext getInterpretationContext() {
        return interpreter.getInterpretationContext();
    }
}
