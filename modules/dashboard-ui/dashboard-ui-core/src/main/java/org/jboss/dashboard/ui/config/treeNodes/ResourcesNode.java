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

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.jboss.dashboard.ui.config.AbstractNode;
import org.jboss.dashboard.security.BackOfficePermission;
import org.jboss.dashboard.ui.config.TreeNode;
import org.jboss.dashboard.users.UserStatus;

public class ResourcesNode extends AbstractNode {

    @Inject
    private SkinsNode skinsNode;

    @Inject
    private LayoutsNode layoutsNode;

    @Inject
    private EnvelopesNode envelopesNode;

    @PostConstruct
    protected void init() {
        super.setSubnodes(new TreeNode[] {skinsNode, layoutsNode, envelopesNode});
    }

    public String getId() {
        return "resources";
    }

    public String getIconId() {
        return "22x22/ico-menu_resources.png";
    }

    public boolean isEditable() {
        return false;
    }

    public boolean isExpandible() {
        BackOfficePermission perm = BackOfficePermission.newInstance(null, BackOfficePermission.ACTION_USE_GRAPHIC_RESOURCES);
        return super.isExpandible() && UserStatus.lookup().hasPermission( perm);
    }
}
