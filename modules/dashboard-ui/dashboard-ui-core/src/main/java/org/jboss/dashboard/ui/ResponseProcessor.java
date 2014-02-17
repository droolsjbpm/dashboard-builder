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
package org.jboss.dashboard.ui;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.jboss.dashboard.commons.cdi.CDIBeanLocator;
import org.jboss.dashboard.ui.controller.RequestContext;
import org.jboss.dashboard.ui.controller.requestChain.BeanDispatcher;
import org.jboss.dashboard.ui.controller.requestChain.CSRFTokenProcessor;
import org.jboss.dashboard.ui.controller.requestChain.DashboardProcessor;
import org.jboss.dashboard.ui.controller.requestChain.EnvelopeVerifier;
import org.jboss.dashboard.ui.controller.requestChain.FreeMemoryProcessor;
import org.jboss.dashboard.ui.controller.requestChain.FriendlyUrlProcessor;
import org.jboss.dashboard.ui.controller.requestChain.HttpSSOProcessor;
import org.jboss.dashboard.ui.controller.requestChain.ModalDialogRenderer;
import org.jboss.dashboard.ui.controller.requestChain.ModalDialogStatusSaver;
import org.jboss.dashboard.ui.controller.requestChain.MultipartProcessor;
import org.jboss.dashboard.ui.controller.requestChain.NavigationCookieProcessor;
import org.jboss.dashboard.ui.controller.requestChain.RequestChainProcessor;
import org.jboss.dashboard.ui.controller.requestChain.RequestRenderer;
import org.jboss.dashboard.ui.controller.requestChain.ResponseHeadersProcessor;
import org.jboss.dashboard.ui.controller.requestChain.SessionInitializer;

@ApplicationScoped
public class ResponseProcessor {

    public static ResponseProcessor lookup() {
        return CDIBeanLocator.getBeanByType(ResponseProcessor.class);
    }

    protected List<RequestChainProcessor> processorChain = new ArrayList<RequestChainProcessor>();

    @PostConstruct
    protected void initChain() {
        processorChain.add(CDIBeanLocator.getBeanByType(ModalDialogRenderer.class));
        processorChain.add(CDIBeanLocator.getBeanByType(RequestRenderer.class));
        processorChain.add(CDIBeanLocator.getBeanByType(EnvelopeVerifier.class));
    }

    public void run() throws Exception {
        for (RequestChainProcessor processor : processorChain) {
            RequestContext reqCtx = RequestContext.getCurrentContext();
            if (processor.processRequest(reqCtx.getRequest()) == false) {
                // Stop in case the processor has explicitly stopped the chain's processing.
                return;
            }
        }
    }
}
