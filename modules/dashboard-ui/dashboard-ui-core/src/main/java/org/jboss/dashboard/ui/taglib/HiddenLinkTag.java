/**
 * Copyright (C) 2012 Red Hat, Inc. and/or its affiliates.
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
package org.jboss.dashboard.ui.taglib;

import org.jboss.dashboard.ui.components.HandlerMarkupGenerator;
import org.jboss.dashboard.ui.controller.RequestContext;
import org.jboss.dashboard.workspace.Panel;

import javax.servlet.jsp.JspTagException;

/**
 * Custom Tag which is used to provide URLs to invoke panels actions
 */
public class HiddenLinkTag extends BaseTag {

    private String action = null;
    private String params = null;
    private String panel = null;

    /**
     * @see javax.servlet.jsp.tagext.TagSupport
     */
    public int doEndTag() throws JspTagException {
        HandlerMarkupGenerator markupGenerator = HandlerMarkupGenerator.lookup();
        Panel thePanel = RequestContext.lookup().getActivePanel();
        if (getPanel() != null) thePanel = thePanel.getSection().getPanel(getPanel());
        try {
            String textToWrite = markupGenerator.getMarkupToPanelAction(thePanel, action);
            pageContext.getOut().print(textToWrite);
        } catch (java.io.IOException e) {
            handleError(e);
        }
        return EVAL_PAGE;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getPanel() {
        return panel;
    }

    public void setPanel(String panel) {
        this.panel = panel;
    }
}