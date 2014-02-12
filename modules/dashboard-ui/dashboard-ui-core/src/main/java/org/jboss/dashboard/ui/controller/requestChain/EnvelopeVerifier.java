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
package org.jboss.dashboard.ui.controller.requestChain;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.ui.taglib.EnvelopeFooterTag;
import org.jboss.dashboard.ui.taglib.EnvelopeHeadTag;
import org.slf4j.Logger;

@ApplicationScoped
public class EnvelopeVerifier implements RequestChainProcessor {

    @Inject
    private transient Logger log;

    public boolean processRequest(CommandRequest req) throws Exception {
        HttpServletRequest request = req.getRequestObject();
        Boolean headToken = (Boolean) request.getAttribute(EnvelopeHeadTag.ENVELOPE_TOKEN);
        if (headToken != null && Boolean.TRUE.equals(headToken)) {
            Boolean footerToken = (Boolean) request.getAttribute(EnvelopeFooterTag.ENVELOPE_TOKEN);
            if (footerToken == null || Boolean.FALSE.equals(footerToken)) {
                log.error("Invalid envelope: <panel:envelopeFooter/> tag MUST be included just before the </body> tag both in full.jsp AND shared.jsp.");
            }
        }
        return true;
    }
}
