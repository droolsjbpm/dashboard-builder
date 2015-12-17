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
package org.jboss.dashboard.profiler;

/**
 * Core code block types of the platform.
 * @see org.jboss.dashboard.profiler.CodeBlockType
 */
public class CoreCodeBlockTypes {

    public static final CodeBlockType ERROR = CodeBlockHelper.newCodeBlockType("ERROR", "Error");
    public static final CodeBlockType THREAD = CodeBlockHelper.newCodeBlockType("THREAD", "Thread");
    public static final CodeBlockType TRANSACTION = CodeBlockHelper.newCodeBlockType("TRANSACTION", "Transaction");
    public static final CodeBlockType CONTROLLER_REQUEST = CodeBlockHelper.newCodeBlockType("CONTROLLER", "Controller Request");

    public static final CodeBlockType UI_COMPONENT = CodeBlockHelper.newCodeBlockType("UI COMPONENT", "UI Component");
    public static final CodeBlockType PANEL_ACTION = CodeBlockHelper.newCodeBlockType("PANEL ACTION", "Panel Action");
    public static final CodeBlockType JSP_PAGE = CodeBlockHelper.newCodeBlockType("JSP", "JSP Page");
    public static final CodeBlockType JSP_FORMATTER = CodeBlockHelper.newCodeBlockType("FORMATTER", "JSP Formatter");

    public static final CodeBlockType SQL = CodeBlockHelper.newCodeBlockType("SQL", "SQL Statement");
    public static final CodeBlockType CSV = CodeBlockHelper.newCodeBlockType("CSV", "CSV Read");
    public static final CodeBlockType SCALAR_FUNCTION = CodeBlockHelper.newCodeBlockType("SCALAR_FUNCTION", "Scalar Function");
    public static final CodeBlockType DATASET_LOAD = CodeBlockHelper.newCodeBlockType("DATASET_LOAD", "Data Set Load");
    public static final CodeBlockType DATASET_BUILD = CodeBlockHelper.newCodeBlockType("DATASET_BUILD", "Data Set Build");
    public static final CodeBlockType DATASET_FILTER = CodeBlockHelper.newCodeBlockType("DATASET_FILTER", "Data Set Filter");
}