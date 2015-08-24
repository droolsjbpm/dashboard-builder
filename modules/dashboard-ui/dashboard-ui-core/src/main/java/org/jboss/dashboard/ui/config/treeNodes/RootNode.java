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
package org.jboss.dashboard.ui.config.treeNodes;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.jboss.dashboard.ui.config.AbstractNode;
import org.jboss.dashboard.ui.config.TreeNode;

public class RootNode extends AbstractNode {

    @Inject
    private WorkspacesNode workspacesNode;

    @Inject
    private ResourcesNode resourcesNode;

    @Inject
    private GlobalPermissionsNode globalPermissionsNode;

    @PostConstruct
    protected void init() {
        super.setSubnodes(new TreeNode[] {workspacesNode, resourcesNode, globalPermissionsNode});
    }

    public String getId() {
        return "root";
    }
}
