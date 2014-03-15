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
package org.jboss.dashboard.export;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;
import java.util.Map;

import org.jboss.dashboard.displayer.DataDisplayer;
import org.jboss.dashboard.kpi.KPI;
import org.jboss.dashboard.provider.*;
import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

/**
 * BAM export manager.
 */
@ApplicationScoped
public class ExportManagerImpl implements ExportManager {

    private transient static Logger log = LoggerFactory.getLogger(ExportManagerImpl.class);

    public ExportOptions createExportOptions() {
        return new ExportOptionsImpl();
    }

    public String format(ExportOptions options) throws Exception {
        StringWriter sw = new StringWriter();
        PrintWriter out = new PrintWriter(sw);
        out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        format(options, out, 0);
        return sw.toString();
    }

    public void format(ExportOptions options, PrintWriter out, int indent) throws Exception {
        // Start
        printIndent(out, indent++);
        out.println("<kpis>");

        if (!options.ignoreDataProviders()) formatDataProviders(options, out, indent);
        if (!options.ignoreKPIs()) formatKPIs(options, out, indent);

        // End
        printIndent(out, --indent);
        out.println("</kpis>");
    }

    public void formatKPIs(ExportOptions options, PrintWriter out, int indent) throws Exception {
        for (KPI kpi : options.getKPIs()) {
            DataProvider provider = kpi.getDataProvider();
            DataDisplayer displayer = kpi.getDataDisplayer();
            DataDisplayerXMLFormat displayerXMLFormat = displayer.getDataDisplayerType().getXmlFormat();

            // Start KPI
            printIndent(out, indent++);
            out.println("<kpi code=\"" + StringEscapeUtils.escapeXml(kpi.getCode()) + "\">");

            // Description
            Map<String,String> descriptions = kpi.getDescriptionI18nMap();
            for (String key : descriptions.keySet()) {
                printIndent(out, indent);
                out.print("<description language");
                out.print("=\"" + StringEscapeUtils.escapeXml(key) + "\">");
                out.print(StringEscapeUtils.escapeXml(descriptions.get(key)));
                out.println("</description>");
            }

            // Provider
            String providerCode = provider.getCode();
            if (providerCode != null) {
                printIndent(out, indent);
                out.println("<provider code=\"" + StringEscapeUtils.escapeXml(providerCode) + "\" />");
            }

            // Displayer
            displayerXMLFormat.format(displayer, out, indent);

            // End KPI
            printIndent(out, --indent);
            out.println("</kpi>");
        }
    }

    public void formatDataProviders(ExportOptions options, PrintWriter out, int indent) throws Exception {
        for (DataProvider dataProvider : options.getDataProviders()) {
            DataLoader dataLoader = dataProvider.getDataLoader();
            DataProviderType providerType = dataLoader.getDataProviderType();

            printIndent(out, indent++);
            out.println("<dataprovider code=\"" + StringEscapeUtils.escapeXml(dataProvider.getCode()) + "\" type=\"" + StringEscapeUtils.escapeXml(providerType.getUid()) + "\">");

            Map<Locale,String> descriptions = dataProvider.getDescriptionI18nMap();
            for (Locale key : descriptions.keySet()) {
                printIndent(out, indent);
                out.print("<description language");
                out.print("=\"" + StringEscapeUtils.escapeXml(key.toString()) + "\">");
                out.print(StringEscapeUtils.escapeXml(descriptions.get(key)));
                out.println("</description>");
            }           

            if (!dataProvider.isCanEdit()) {
                printIndent(out, indent);
                out.println("<canEdit>false</canEdit>");
            }

            if (!dataProvider.isCanEditProperties()) {
                printIndent(out, indent);
                out.println("<canEditProperties>false</canEditProperties>");
            }

            if (!dataProvider.isCanDelete()) {
                printIndent(out, indent);
                out.println("<canDelete>false</canDelete>");
            }

            // The loader
            DataLoaderXMLFormat providerXMLFormat = providerType.getXmlFormat();
            providerXMLFormat.format(dataLoader, out, indent);

            // Provider properties
            dataProvider.getDataSet().formatXMLProperties(out, indent);

            // End
            printIndent(out, --indent);
            out.println("</dataprovider>");
        }
    }

    protected  void printIndent(PrintWriter out, int indent) {
        for (int i = 0; i < indent; i++) {
            out.print("  ");
        }
    }
}
