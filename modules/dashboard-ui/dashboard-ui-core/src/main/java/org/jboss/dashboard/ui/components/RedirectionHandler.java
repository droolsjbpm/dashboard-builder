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
package org.jboss.dashboard.ui.components;

import javax.enterprise.context.ApplicationScoped;

import org.jboss.dashboard.workspace.Parameters;
import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.ui.controller.CommandResponse;
import org.jboss.dashboard.ui.controller.responses.DoNothingResponse;
import org.jboss.dashboard.ui.controller.responses.ShowScreenResponse;

@ApplicationScoped
public class RedirectionHandler extends BeanHandler {

    public static final String PARAM_PAGE_TO_REDIRECT = "RH_page_to_redirect";

    public CommandResponse actionRedirectToSection(CommandRequest request) throws Exception {
        final Object currentPanelIfAny = request.getRequestObject().getAttribute(Parameters.RENDER_PANEL);
        String page = request.getRequestObject().getParameter(PARAM_PAGE_TO_REDIRECT);
        if (page != null && !"".equals(page.trim())) {
            return new ShowScreenResponse(page) {
                public boolean execute(CommandRequest cmdReq) throws Exception {
                    cmdReq.getRequestObject().setAttribute(Parameters.RENDER_PANEL, currentPanelIfAny);
                    boolean b = super.execute(cmdReq);
                    cmdReq.getRequestObject().removeAttribute(Parameters.RENDER_PANEL);
                    return b;
                }
            };
        } else return new DoNothingResponse();
    }
}
