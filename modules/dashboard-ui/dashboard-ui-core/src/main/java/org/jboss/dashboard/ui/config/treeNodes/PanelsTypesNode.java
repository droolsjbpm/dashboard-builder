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
package org.jboss.dashboard.ui.config.treeNodes;

import javax.inject.Inject;

import org.jboss.dashboard.ui.config.AbstractNode;
import org.jboss.dashboard.ui.config.components.panelstypes.PanelsTypesPropertiesHandler;
import org.jboss.dashboard.users.UserStatus;
import org.jboss.dashboard.workspace.Workspace;
import org.jboss.dashboard.security.WorkspacePermission;
import org.slf4j.Logger;

public class PanelsTypesNode extends AbstractNode {

    @Inject
    private transient Logger log;

    @Inject
    private PanelsTypesPropertiesHandler panelsTypesPropertiesHandler;

    public PanelsTypesPropertiesHandler getPanelsTypesPropertiesHandler() {
        return panelsTypesPropertiesHandler;
    }

    public void setPanelsTypesPropertiesHandler(PanelsTypesPropertiesHandler panelsTypesPropertiesHandler) {
        this.panelsTypesPropertiesHandler = panelsTypesPropertiesHandler;
    }

    public String getId() {
        return "panelTypes";
    }

    public String getIconId() {
        return "16x16/ico-menu_panel.png";
    }

    public boolean onEdit() {
        try {
            String workspaceId = ((WorkspaceNode) getParent()).getWorkspaceId();
            getPanelsTypesPropertiesHandler().clearFieldErrors();
            getPanelsTypesPropertiesHandler().setWorkspaceId(workspaceId);
        } catch (Exception e) {
            log.error("Error: ", e);
            return false;
        }
        return true;
    }

    public boolean isEditable() {
        WorkspaceNode parent = (WorkspaceNode) getParent();
        Workspace workspace;
        try {
            workspace = parent.getWorkspace();
            WorkspacePermission editPerm = WorkspacePermission.newInstance(workspace, WorkspacePermission.ACTION_ADMIN_PROVIDERS);
            return super.isEditable() && UserStatus.lookup().hasPermission( editPerm);
        } catch (Exception e) {
            log.error("Error: ", e);
        }
        return false;
    }
}
