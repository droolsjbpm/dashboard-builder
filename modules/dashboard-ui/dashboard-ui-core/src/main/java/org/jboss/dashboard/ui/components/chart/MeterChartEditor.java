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
package org.jboss.dashboard.ui.components.chart;

import java.text.NumberFormat;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.dashboard.displayer.chart.MeterChartDisplayer;
import org.jboss.dashboard.ui.annotation.panel.PanelScoped;
import org.jboss.dashboard.ui.components.AbstractChartDisplayerEditor;
import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.ui.controller.CommandRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jboss.dashboard.ui.controller.CommandResponse;

/**
 * Meter chart editor displayer configurator component.
 */
@PanelScoped
@Named("meterchart_editor")
public class MeterChartEditor extends AbstractChartDisplayerEditor {

    /** Logger */ @Inject
    private transient Logger log;

    // i18n
    public static final String I18N_METER = "meterChartDisplayer.";
    public static final String METER_SAVE_BUTTON_PRESSED = "updateMeterDetails";

    public String getBeanJSP() {
        return "/components/bam/displayer/chart/meterchart_editor.jsp";
    }

    public CommandResponse actionSubmit(CommandRequest request) throws Exception {
        MeterChartDisplayer meterDisplayer = (MeterChartDisplayer) getDataDisplayer();
        if (!meterDisplayer.getDataProvider().isReady()) return null;

        super.actionSubmit(request);
        try {
            Locale locale = LocaleManager.currentLocale();
            NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);

            // Parse the position.
            String positionType = request.getRequestObject().getParameter("positionType");
            if (positionType != null && !"".equals(positionType)) meterDisplayer.setPositionType(positionType);

            // Meter
            if (meterDisplayer.getType().equals("meter")) {
                String minValueParam = request.getRequestObject().getParameter("minValue");
                String maxValueParam = request.getRequestObject().getParameter("maxValue");
                String maxMeterTicksParam = request.getRequestObject().getParameter("maxMeterTicks");
                String warningThresholdParam = request.getRequestObject().getParameter("meterWarningThreshold");
                String criticalThresholdParam = request.getRequestObject().getParameter("meterCriticalThreshold");
                if (minValueParam == null || "".equals(minValueParam.trim())) return null;
                if (maxValueParam == null || "".equals(maxValueParam.trim())) return null;
                if (warningThresholdParam == null || "".equals(warningThresholdParam.trim())) return null;
                if (criticalThresholdParam == null || "".equals(criticalThresholdParam.trim())) return null;
                if (maxMeterTicksParam == null || "".equals(maxMeterTicksParam.trim())) return null;

                double minValue = numberFormat.parse(minValueParam).doubleValue();
                double maxValue = numberFormat.parse(maxValueParam).doubleValue();
                double warningThreshold = numberFormat.parse(warningThresholdParam).doubleValue();
                double criticalThreshold = numberFormat.parse(criticalThresholdParam).doubleValue();
                int maxMeterTicks = numberFormat.parse(maxMeterTicksParam).intValue();
                if (minValue > maxValue) return null;
                if (warningThreshold < minValue || warningThreshold > maxValue) return null;
                if (criticalThreshold < minValue || criticalThreshold > maxValue) return null;
                if (warningThreshold > criticalThreshold) return null;
                if (maxMeterTicks < 0) return null;

                meterDisplayer.setMaxMeterTicks(maxMeterTicks);
                meterDisplayer.setMinValue(minValue);
                meterDisplayer.setWarningThreshold(warningThreshold);
                meterDisplayer.setCriticalThreshold(criticalThreshold);
                meterDisplayer.setMaxValue(maxValue);

                /* Intervals descriptions. Hide them until the global legend will be available.
                    String descripNormalInterval = request.getRequestObject().getParameter("descripNormalInterval");
                    String descripWarningInterval = request.getRequestObject().getParameter("descripWarningInterval");
                    String descripCriticalInterval = request.getRequestObject().getParameter("descripCriticalInterval");
                    if (descripCriticalInterval != null && !"".equals(descripCriticalInterval.trim())) meterDisplayer.setDescripCriticalInterval(descripCriticalInterval, locale);
                    if (descripWarningInterval != null && !"".equals(descripWarningInterval.trim())) meterDisplayer.setDescripWarningInterval(descripWarningInterval, locale);
                    if (descripNormalInterval != null && !"".equals(descripNormalInterval.trim())) meterDisplayer.setDescripNormalInterval(descripNormalInterval, locale);
                */
            }
            // Thermometer
            else if (meterDisplayer.getType().equals("thermometer")) {
                String thermoLowerBoundParam = request.getRequestObject().getParameter("thermoLowerBound");
                String thermoUpperBoundParam = request.getRequestObject().getParameter("thermoUpperBound");
                String thermoWarningThresholdParam = request.getRequestObject().getParameter("thermoWarningThreshold");
                String thermoCriticalThresholdParam = request.getRequestObject().getParameter("thermoCriticalThreshold");
                if (thermoLowerBoundParam == null || "".equals(thermoLowerBoundParam.trim())) return null;
                if (thermoUpperBoundParam == null || "".equals(thermoUpperBoundParam.trim())) return null;
                if (thermoWarningThresholdParam == null || "".equals(thermoWarningThresholdParam.trim())) return null;
                if (thermoCriticalThresholdParam == null || "".equals(thermoCriticalThresholdParam.trim())) return null;

                double thermoLowerBound = numberFormat.parse(thermoLowerBoundParam).doubleValue();
                double thermoUpperBound = numberFormat.parse(thermoUpperBoundParam).doubleValue();
                double thermoWarningThreshold = numberFormat.parse(thermoWarningThresholdParam).doubleValue();
                double thermoCriticalThreshold = numberFormat.parse(thermoCriticalThresholdParam).doubleValue();
                if (thermoLowerBound > thermoUpperBound) return null;
                if (thermoWarningThreshold < thermoLowerBound || thermoWarningThreshold > thermoUpperBound) return null;
                if (thermoCriticalThreshold < thermoLowerBound || thermoCriticalThreshold > thermoUpperBound) return null;
                if (thermoWarningThreshold > thermoCriticalThreshold) return null;

                meterDisplayer.setThermoLowerBound(thermoLowerBound);
                meterDisplayer.setWarningThermoThreshold(thermoWarningThreshold);
                meterDisplayer.setCriticalThermoThreshold(thermoCriticalThreshold);
                meterDisplayer.setThermoUpperBound(thermoUpperBound);
            }
            // Dial
            else if (meterDisplayer.getType().equals("dial")) {
                String pointerTypeParam = request.getRequestObject().getParameter("pointerType");
                String dialLowerBoundParam = request.getRequestObject().getParameter("dialLowerBound");
                String dialUpperBoundParam = request.getRequestObject().getParameter("dialUpperBound");
                String maxTicksParam = request.getRequestObject().getParameter("maxTicks");
                String minorTickCountParam = request.getRequestObject().getParameter("minorTickCount");
                if (pointerTypeParam == null || "".equals(pointerTypeParam.trim())) return null;
                if (dialLowerBoundParam == null || "".equals(dialLowerBoundParam.trim())) return null;
                if (dialUpperBoundParam == null || "".equals(dialUpperBoundParam.trim())) return null;
                if (maxTicksParam == null || "".equals(maxTicksParam.trim())) return null;
                if (minorTickCountParam == null || "".equals(minorTickCountParam.trim())) return null;

                double dialLowerBound = numberFormat.parse(dialLowerBoundParam).doubleValue();
                double dialUpperBound = numberFormat.parse(dialUpperBoundParam).doubleValue();
                int maxTicks = numberFormat.parse(maxTicksParam).intValue();
                int minorTickCount = numberFormat.parse(minorTickCountParam).intValue();
                if (dialLowerBound > dialUpperBound) return null;
                if (maxTicks < 0) return null;
                if (minorTickCount > 10) return null;

                meterDisplayer.setDialLowerBound(dialLowerBound);
                meterDisplayer.setDialUpperBound(dialUpperBound);
                meterDisplayer.setMaxTicks(numberFormat.parse(maxTicksParam).intValue());
                meterDisplayer.setMinorTickCount(minorTickCount);
            }
        } catch (Exception e) {
            log.warn("Cannot parse number meter specific properties.");
        }
        return null;
    }
}
