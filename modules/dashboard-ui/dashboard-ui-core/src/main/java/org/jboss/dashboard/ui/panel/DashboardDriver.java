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
package org.jboss.dashboard.ui.panel;

import java.util.Set;

import org.jboss.dashboard.provider.DataProvider;
import org.jboss.dashboard.workspace.Panel;
import org.jboss.dashboard.workspace.Panel;

/**
 * Any panel driver (controller) who display dashboard related data should must implement this interface.
 */
public interface DashboardDriver {

    /**
     * Get all the properties this panel references to (directly or indirectly). A property is a reference
     * if any potential change on the property values has an impact on the data displayed by the referencing
     * panel. This typically occurs on filter/unfilter requests.
     */
    Set<String> getPropertiesReferenced(Panel panel) throws Exception;
}
