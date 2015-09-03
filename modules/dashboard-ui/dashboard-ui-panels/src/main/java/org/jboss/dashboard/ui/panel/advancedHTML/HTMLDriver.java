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
package org.jboss.dashboard.ui.panel.advancedHTML;

import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.database.hibernate.HibernateTxFragment;
import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.ui.controller.CommandResponse;
import org.jboss.dashboard.ui.controller.responses.ShowPanelPage;
import org.jboss.dashboard.ui.panel.PanelProvider;
import org.jboss.dashboard.workspace.Panel;
import org.jboss.dashboard.workspace.PanelInstance;
import org.jboss.dashboard.workspace.PanelSession;
import org.jboss.dashboard.workspace.export.Exportable;
import org.jboss.dashboard.ui.panel.PanelDriver;
import org.jboss.dashboard.ui.panel.parameters.BooleanParameter;
import org.jboss.dashboard.security.PanelPermission;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.FlushMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.*;

/**
 * Controller for the HTML panel
 */
public class HTMLDriver extends PanelDriver implements Exportable {

    public static final String PARAMETER_HTML = "html_code";
    public static final String PARAMETER_EDITING_LANG = "edit_lang";

    private final static String PAGE_SHOW = "show";
    private final static String PAGE_EDIT = "edit";

    public final static String PARAMETER_USE_DEFAULTS = "useDefaultLanguage";

    private static final String ATTR_TEXT = "text";
    private static final String ATTR_EDITING_LANGUAGE = "lang";

    @Inject
    Logger log;

    public void init(PanelProvider provider) throws Exception {
        super.init(provider);
        addParameter(new BooleanParameter(provider, PARAMETER_USE_DEFAULTS, false, true));
        String[] methodsForEditMode = new String[]{"actionChangeEditingLanguage", "actionSaveChanges"};
        for (String method : methodsForEditMode) {
            addMethodPermission(method, PanelPermission.class, PanelPermission.ACTION_EDIT);
        }
    }

    public void initPanelSession(PanelSession status, HttpSession session) {
        status.setCurrentPageId(PAGE_SHOW);
    }

    protected void beforePanelInstanceRemove(final PanelInstance instance) throws Exception {
        super.beforePanelInstanceRemove(instance);

        new HibernateTxFragment() {
        protected void txFragment(Session session) throws Exception {
            HTMLText htmlText = load(instance);
            if (htmlText != null) htmlText.delete();
        }}.execute();
    }

    /**
     * Returns if this driver defines support to activate edit mode.
     */
    public boolean supportsEditMode(Panel panel) {
        return true;
    }

    public int getEditWidth(Panel panel, CommandRequest request) {
        return 1000;
    }

    public int getEditHeight(Panel panel, CommandRequest request) {
        return 695;
    }

    /**
     * Defines the action to be taken when activating edit mode
     */
    public void activateEditMode(Panel panel, CommandRequest request) throws Exception {
        super.activateEditMode(panel, request);
        HTMLText text = load(panel.getInstance());
        panel.getPanelSession().setAttribute(ATTR_TEXT, toEditableObject(text));
    }

    protected Map<String, String> toEditableObject(HTMLText text) {
        Map<String, String> m = new HashMap<String, String>();
        for (String lang : text.getText().keySet()) {
            String val = text.getText(lang);
            m.put(lang, val);
        }
        return m;
    }

    /**
     * Defines the action to be taken when activating edit mode
     */
    public void activateNormalMode(Panel panel, CommandRequest request) throws Exception {
        super.activateNormalMode(panel, request);
        panel.getPanelSession().removeAttribute(ATTR_TEXT);
        panel.getPanelSession().removeAttribute(ATTR_EDITING_LANGUAGE);
    }

    /**
     * Returns if this driver is using default language
     *
     * @param panel
     * @return if this driver is using default language
     */
    public boolean isUsingDefaultLanguage(Panel panel) {
        return Boolean.parseBoolean(panel.getParameterValue(PARAMETER_USE_DEFAULTS));
    }

    /**
     * Returns if this driver is using default language
     *
     * @param panel
     * @return if this driver is using default language
     */
    public boolean isUsingDefaultLanguage(PanelInstance panel) {
        return Boolean.parseBoolean(panel.getParameterValue(PARAMETER_USE_DEFAULTS));
    }

    /**
     * Determine the text being shown for given panel.
     *
     * @param panel
     * @return The text shown, i18n.
     */
    public Map<String, String> getHtmlCode(Panel panel) {
        PanelSession pSession = panel.getPanelSession();
        Map<String, String> m = (Map) pSession.getAttribute(ATTR_TEXT);
        if (m != null) return m;
        HTMLText text = load(panel.getInstance());
        if (text != null) return text.getText();
        try {
            HTMLText textToCreate = new HTMLText();
            textToCreate.setPanelInstance(panel.getInstance());
            String[] langs = LocaleManager.lookup().getPlatformAvailableLangs();
            for (int i = 0; i < langs.length; i++) {
                String lang = langs[i];
                ResourceBundle i18n = LocaleManager.lookup().getBundle("org.jboss.dashboard.ui.panel.advancedHTML.messages", new Locale(lang));
                textToCreate.setText(lang, i18n.getString("defaultContent"));
            }
            textToCreate.save();
        } catch (Exception e) {
            log.error("Error creating empty text for panel: ", e);
        }
        text = load(panel.getInstance());
        if (text != null) return text.getText();
        log.error("Current HTML code is null for panel " + panel);
        return null;
    }

    /**
     * Determine the editing language.
     *
     * @return The text shown, i18n.
     */
    public String getEditingLanguage(Panel panel) {
        String lang = (String) panel.getPanelSession().getAttribute(ATTR_EDITING_LANGUAGE);
        return lang == null ? LocaleManager.lookup().getDefaultLang() : lang;
    }

    public CommandResponse actionChangeEditingLanguage(Panel panel, CommandRequest request) throws Exception {
        String currentText = request.getRequestObject().getParameter(PARAMETER_HTML);
        Map<String, String> text = (Map) panel.getPanelSession().getAttribute(ATTR_TEXT);
        if (text == null) {
            text = toEditableObject(load(panel.getInstance()));
            panel.getPanelSession().setAttribute(ATTR_TEXT, text);
        }
        text.put(getEditingLanguage(panel), currentText);
        panel.getPanelSession().setAttribute(ATTR_TEXT, text);
        String paramLang = request.getRequestObject().getParameter(PARAMETER_EDITING_LANG);
        panel.getPanelSession().setAttribute(ATTR_EDITING_LANGUAGE, paramLang);
        return new ShowPanelPage();
    }

    public CommandResponse actionSaveChanges(Panel panel, CommandRequest request) throws Exception {
        String currentText = request.getRequestObject().getParameter(PARAMETER_HTML);
        Map<String, String> m = (Map) panel.getPanelSession().getAttribute(ATTR_TEXT);
        HTMLText text = load(panel.getInstance());
        for (String lang : m.keySet()) {
            String val = m.get(lang);
            text.setText(lang, val);
        }
        text.setText(getEditingLanguage(panel), currentText);
        text.save();
        activateNormalMode(panel, request);
        return new ShowPanelPage();
    }

    public HTMLText load(final PanelInstance instance) {
        final List<HTMLText> results = new ArrayList<HTMLText>();
        try {
            new HibernateTxFragment() {
            protected void txFragment(Session session) throws Exception {
                FlushMode oldFlushMode = session.getFlushMode();
                session.setFlushMode(FlushMode.NEVER);
                Query query = session.createQuery(" from " + HTMLText.class.getName() + " as text where text.panelInstance = :instance");
                query.setParameter("instance", instance);
                query.setCacheable(true);
                results.addAll(query.list());
                session.setFlushMode(oldFlushMode);
            }}.execute();
            HTMLText text = null;
            if (results.size() > 0) text = results.get(0);
            else log.debug("Does not exist a html_text for HTML panel");
            return text;
        } catch (Exception e) {
            log.error("Can't retrive a data for HTML panel ", e);
            return null;
        }
    }

    /**
     * Replicates panel data.
     *
     * @param src  Source PanelInstance
     * @param dest Destinaton PanelInstance
     */
    public void replicateData(final PanelInstance src, PanelInstance dest) throws Exception {
        super.replicateData(src, dest);
        log.debug("HTMLDriver replicating Data from PanelInstance " + src.getDbid() + " to " + dest.getDbid() + ").");
        if (src.equals(dest)) {
            log.debug("Ignoring replication, panel instance is the same.");
            return;
        }

        final HTMLText[] textArray = new HTMLText[1];
        try {
            new HibernateTxFragment() {
            protected void txFragment(Session session) throws Exception {
                log.debug("Getting text to duplicate for instance " + src.getDbid());
                FlushMode oldMode = session.getFlushMode();
                session.setFlushMode(FlushMode.COMMIT);//Avoids flushing, as we know the text was not modified in this transaction.
                textArray[0] = load(src);
                session.setFlushMode(oldMode);
                log.debug("Got text to duplicate for instance " + src.getDbid());
            }}.execute();
        } catch (Exception e) {
            log.error("Error loading text for instance. ", e);
        }
        HTMLText text = textArray[0];
        if (text == null) {
            log.debug("Nothing to replicate from PanelInstance " + src.getDbid() + " to " + dest.getDbid());
            return;
        }
        Map<String, String> htmlSrc = text.getText();

        log.debug("htmlCode to replicate = " + htmlSrc);
        HTMLText htmlDest = new HTMLText();
        htmlDest.setPanelInstance(dest.getInstance());
        for (String lang : htmlSrc.keySet()) {
            String val = htmlSrc.get(lang);
            htmlDest.setText(lang, val);
        }
        try {
            log.debug("Updating HTMLText: IDText " + htmlDest.getDbid() + " Text " + htmlDest.getText());
            htmlDest.save();
        } catch (Exception e) {
            log.error("Replicating panel data", e);
        }
    }

    /**
     * Write instance content to given OutputStream, which must not be closed.
     */
    public void exportContent(PanelInstance instance, OutputStream os) throws Exception {
        HTMLText text = load(instance);
        if (text == null) {
            try {
                text = new HTMLText();
                text.setPanelInstance(instance);
                text.save();
            } catch (Exception e) {
                log.error("Error creating empty HTMLText object", e);
            }
        }
        ObjectOutputStream oos = new ObjectOutputStream(os);
        if (log.isDebugEnabled()) log.debug("Exporting content: " + text.getText());
        HashMap<String, String> h = new HashMap<String, String>(); // Avoids serializing a hibernate map
        h.putAll(text.getText());
        oos.writeObject(h);
    }

    /**
     * Read instance content from given InputStream, which must not be closed.
     */
    public void importContent(PanelInstance instance, InputStream is) throws Exception {
        HTMLText currentText = new HTMLText();
        currentText.setPanelInstance(instance);
        ObjectInputStream ois = new ObjectInputStream(is);
        Map<String, String> text = (Map) ois.readObject();
        if (log.isDebugEnabled()) log.debug("Importing content: " + text);
        for (String lang : text.keySet()) {
            String value = text.get(lang);
            currentText.setText(lang, value);
        }
        currentText.save();
    }

    protected String getPanelHTMLContent(PanelInstance instance, String lang) {
        HTMLText text = load(instance);
        if (text != null) {
            String val = text.getText(lang);
            if (StringUtils.isEmpty(val) && isUsingDefaultLanguage(instance)) {
                LocaleManager localeManager = LocaleManager.lookup();
                val = text.getText(localeManager.getDefaultLang());
            }
            return val;
        }
        return null;
    }
}
