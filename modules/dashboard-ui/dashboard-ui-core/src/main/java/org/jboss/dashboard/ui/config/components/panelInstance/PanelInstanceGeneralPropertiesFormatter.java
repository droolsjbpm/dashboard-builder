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
package org.jboss.dashboard.ui.config.components.panelInstance;

import javax.inject.Inject;

import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.utils.forms.FormStatus;
import org.jboss.dashboard.workspace.*;

public class PanelInstanceGeneralPropertiesFormatter extends PanelInstancePropertiesFormatter {

    @Inject
    private PanelInstanceGeneralPropertiesHandler handler;

    public PanelInstanceGeneralPropertiesHandler getHandler() {
        return handler;
    }

    public void setHandler(PanelInstanceGeneralPropertiesHandler handler) {
        this.handler = handler;
    }

    public WorkspacesManager getWorkspacesManager() {
        return UIServices.lookup().getWorkspacesManager();
    }

    public PanelProviderParameter[] getPanelProviderParameters() throws Exception {
        return ((WorkspaceImpl) getWorkspacesManager().getWorkspace(handler.getWorkspaceId())).getPanelInstance(handler.getPanelInstanceId()).getSystemParameters();
    }

    public PanelInstance getPanelInstance() throws Exception {
        return ((WorkspaceImpl) getWorkspacesManager().getWorkspace(handler.getWorkspaceId())).getPanelInstance(handler.getPanelInstanceId());
    }

    public FormStatus getFormStatus() {
        return handler.getFormStatus();
    }
}
