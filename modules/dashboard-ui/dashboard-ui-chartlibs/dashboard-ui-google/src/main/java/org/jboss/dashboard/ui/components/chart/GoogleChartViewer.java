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
package org.jboss.dashboard.ui.components.chart;

import org.jboss.dashboard.ui.Dashboard;
import org.jboss.dashboard.dataset.DataSet;
import org.jboss.dashboard.displayer.chart.AbstractChartDisplayer;
import org.jboss.dashboard.domain.Interval;
import org.jboss.dashboard.ui.components.DataDisplayerViewer;
import org.jboss.dashboard.ui.components.DashboardHandler;
import org.jboss.dashboard.provider.DataProperty;
import org.jboss.dashboard.commons.filter.FilterByCriteria;
import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.ui.controller.CommandResponse;
import org.jboss.dashboard.ui.controller.responses.ShowCurrentScreenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class GoogleChartViewer extends DataDisplayerViewer {

    public static final String PARAM_ACTION = "applyLink";
    public static final String PARAM_NSERIE = "serie";

    public CommandResponse actionApplyLink(CommandRequest request) throws Exception {
        AbstractChartDisplayer abstractChartDisplayer = (AbstractChartDisplayer) getDataDisplayer();
        DataProperty property = abstractChartDisplayer.getDomainProperty();
        Integer series = Integer.decode(request.getRequestObject().getParameter(PARAM_NSERIE));
        DataSet dataSet = abstractChartDisplayer.buildXYDataSet();
        Interval interval = (Interval) dataSet.getValueAt(series, 0);
        Dashboard dashboard = DashboardHandler.lookup().getCurrentDashboard();
        if (dashboard.filter(property.getPropertyId(), interval, FilterByCriteria.ALLOW_ANY)) {
            return new ShowCurrentScreenResponse();
        }
        return null;
    }
}
