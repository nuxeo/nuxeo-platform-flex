/*
 * (C) Copyright 2006-2008 Nuxeo SAS (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     Nuxeo - initial API and implementation
 *
 * $Id$
 */

package org.nuxeo.ecm.platform.ui.flex.seam;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.repository.Repository;
import org.nuxeo.ecm.core.api.repository.RepositoryManager;
import org.nuxeo.ecm.platform.ui.granite.filter.SessionConcurrencyManager;
import org.nuxeo.runtime.api.Framework;

/**
 * Seam Factory component to manage the {@link CoreSession} in Seam Session
 * scope
 *
 * @author Tiry (tdelprat@nuxeo.com)
 *
 */
@Name("flexDocumentManager")
@Scope(ScopeType.SESSION)
public class FlexDocumentManager {

    protected static final Log log = LogFactory.getLog(FlexDocumentManager.class);

    private CoreSession session;

    protected static final boolean USE_SYNC = true;

    @Unwrap
    public CoreSession getFlexDocumentManager() throws Exception {
        if (session == null) {
            RepositoryManager repositoryMgr = Framework.getService(RepositoryManager.class);
            Repository repository = repositoryMgr.getDefaultRepository();
            session = repository.open();
        } else {
            if (USE_SYNC) {
                SessionConcurrencyManager.getExclusiveAccess(session);
            }
        }
        return session;
    }

    @Destroy
    public void remove() {
        if (session != null) {
            LoginContext lc = null;
            try {
                try {
                    lc = Framework.login();
                } catch (LoginException le) {
                    log.error("Unable to login as System", le);
                    log.warn("...try to feed CoreSession(s) without system login ...");
                }

                Repository.close(session);
            } finally {
                if (lc != null) {
                    try {
                        lc.logout();
                    } catch (LoginException lo) {
                        log.error("Error when loggin out", lo);
                    }
                }
                session = null;
            }
        }

    }

}
