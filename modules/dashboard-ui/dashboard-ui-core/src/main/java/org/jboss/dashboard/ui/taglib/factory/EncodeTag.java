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

import javax.servlet.jsp.JspTagException;

import org.jboss.dashboard.ui.components.UIBeanHandler;
import org.jboss.dashboard.ui.formatters.FactoryUniqueIdEncoder;
import org.jboss.dashboard.ui.taglib.BaseTag;

public class EncodeTag extends BaseTag {

    /**
     * Text to encode
     */
    private String name = null;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @see javax.servlet.jsp.tagext.TagSupport
     */
    public int doEndTag() throws JspTagException {
        String encodedName = FactoryUniqueIdEncoder.lookup().encodeFromContext(pageContext, name);
        try {
            pageContext.getOut().print(encodedName);
        } catch (Exception e) {
            handleError(e);
        }
        return SKIP_BODY;
    }

   /**
     * Encode a name for a given panel context, appending it to a String depending on the panel.
     *
     * @param panel          Panel being rendered
     * @param factoryComponent factoryComponent
     * @param name             symbolic name to encode @return an encoded version for that name, so that different panels have different names.
     * @return a encoded name
     */
    public static String encode(Object panel, UIBeanHandler factoryComponent, String name) {
        return FactoryUniqueIdEncoder.lookup().encode(panel, factoryComponent, name);
    }

    /**
     * Encode a name for a given panel context, appending it to a String depending on the panel.
     *
     * @param panel Panel being rendered
     * @param name    symbolic name to encode
     * @return an encoded version for that name, so that different panels have different names.
     */
    public static String encode(Object panel, String name) {
        return encode(panel, null, name);
    }
}
