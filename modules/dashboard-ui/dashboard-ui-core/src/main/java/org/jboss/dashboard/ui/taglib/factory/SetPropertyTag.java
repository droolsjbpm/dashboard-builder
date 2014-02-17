/**
 * Copyright (C) 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.dashboard.ui.taglib.factory;

import org.jboss.dashboard.commons.cdi.CDIBeanLocator;
import org.jboss.dashboard.commons.misc.ReflectionUtils;

import javax.servlet.jsp.JspException;

public class SetPropertyTag extends GenericFactoryTag {
    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SetPropertyTag.class.getName());

    private String propValue;


    public String getPropValue() {
        return propValue;
    }

    public void setPropValue(String propValue) {
        this.propValue = propValue;
    }

    /**
     * @see javax.servlet.jsp.tagext.TagSupport
     */
    public int doEndTag() throws JspException {
        String beanName = getBean();
        Object bean = CDIBeanLocator.getBeanByNameOrType(beanName);
        if (bean == null) {
            log.error("Cannot write to component " + beanName + " as it doesn't exist.");
        } else {
            try {
                ReflectionUtils.parseAndSetFieldValue(bean, getProperty(), getPropValue());
            } catch (Exception e) {
                handleError(e);
            }
        }
        return EVAL_PAGE;
    }
}
