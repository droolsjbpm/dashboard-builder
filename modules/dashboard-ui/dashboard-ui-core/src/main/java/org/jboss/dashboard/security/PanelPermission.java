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
package org.jboss.dashboard.security;

import org.jboss.dashboard.SecurityServices;
import org.jboss.dashboard.workspace.Workspace;
import org.jboss.dashboard.workspace.Section;

import java.lang.reflect.Field;
import java.security.Permission;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * A permission over a panel.
 */
public class PanelPermission extends UIPermission {

    // Panel permissions
    //

    public static final String ACTION_VIEW = "view";
    public static final String ACTION_EDIT = "edit";
    public static final String ACTION_MAXIMIZE = "maximize";
    public static final String ACTION_MINIMIZE = "minimize";
    public static final String ACTION_EDIT_PERMISSIONS = "edit perm";

    /**
     * Actions supported by this permission.
     */
    public static final List<String> LIST_OF_ACTIONS = Collections.unmodifiableList(Arrays.asList(new String[]{
        ACTION_VIEW,
        ACTION_EDIT,
        //ACTION_MAXIMIZE,
        //ACTION_MINIMIZE,
        ACTION_EDIT_PERMISSIONS
    }));

    // Factory method(s)
    //

    public static String getResourceName(Object resource) {
        String resourceName = getPolicy().getResourceName(resource);
        if (resource != null) {

            // All workspace or section panels
            if (resource instanceof Workspace) resourceName += ".*";
            if (resource instanceof Section) resourceName += ".*";
        }
        return resourceName;
    }

    public static PanelPermission newInstance(Object resource, String actions) {
        return new PanelPermission(getResourceName(resource), actions);
    }

    public static PanelPermission getInstance(Principal prpal, Object resource) {
        Policy policy = SecurityServices.lookup().getSecurityPolicy();
        PanelPermission perm = (PanelPermission) policy.getPermission(prpal, PanelPermission.class, getResourceName(resource));
        if (perm == null) perm = PanelPermission.newInstance(resource, null);
        return perm;
    }

    // Constructor(s)
    //

    public PanelPermission(String panelPath, String actions) {
        super(panelPath, actions);
        //checkActions(actions);
    }

    /**
     * Check the integrity of the specified actions parameter.
     * Only allowed action identifiers defined into the <code>PanelPermission.LIST_OF_ACTIONS</code> constant
     * are supported. If this contraint is not satisfied then an exception will be thrown.
     *
     * @param actions List of action identifiers separated by comma.
     * @throws IllegalArgumentException If actions string is invalid.
     */
    private void checkActions(String actions) throws IllegalArgumentException {
        if (actions == null) return;

        List grantedList = super.toActionGrantedList(actions);
        List deniedList = super.toActionDeniedList(actions);
        List all = new ArrayList();
        all.addAll(grantedList);
        all.addAll(deniedList);
        Iterator it = all.iterator();
        while (it.hasNext()) {
            String action = (String) it.next();
            if (!LIST_OF_ACTIONS.contains(action)) {
                throw new IllegalArgumentException("Action list invalid (" + actions + ").");
            }
        }
    }

    // java.security.Permission interface
    //

    public boolean implies(Permission p) {
        // Check name
        if (!super.implies(p)) return false;

        // Check instances
        if (p == null || !(p instanceof PanelPermission)) return false;

        // All checks satisfied
        return true;
    }

    public void grantAllActions() {
        for (String action : LIST_OF_ACTIONS) {
            this.grantAction(action);
        }
    }

    public static void main(String[] args) throws Exception {
        Field f = PanelPermission.class.getField("LIST_OF_ACTIONS");
        List listOfActions = (List) f.get(PanelPermission.class);
        Iterator it = listOfActions.iterator();
        while (it.hasNext()) System.out.println(it.next());
    }

}
