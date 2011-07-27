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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.repository.Repository;
import org.nuxeo.ecm.core.api.repository.RepositoryManager;
import org.nuxeo.ecm.platform.ui.granite.filter.NxAMFMessageFilter;
import org.nuxeo.runtime.api.Framework;

/**
 * Seam Factory component to manage the {@link CoreSession} in Seam Session
 * scope. The {@link CoreSession} is in fact managed by the
 * {@link NxAMFMessageFilter}, this Seam component is only used to make the
 * {@link CoreSession} visible and injectable as a Seam Component
 *
 * @author Tiry (tdelprat@nuxeo.com)
 *
 */
@Name("flexDocumentManager")
@Scope(ScopeType.STATELESS)
public class FlexDocumentManager {

    protected static final Log log = LogFactory.getLog(FlexDocumentManager.class);

    protected static ThreadLocal<CoreSession> session = new ThreadLocal<CoreSession>();

    public static CoreSession getRequestBoundCoreSession() throws Exception {
        if (session.get() == null) {
            RepositoryManager repositoryMgr = Framework.getService(RepositoryManager.class);
            Repository repository = repositoryMgr.getDefaultRepository();
            session.set(repository.open());
        }
        return session.get();
    }

    @Unwrap
    public CoreSession getFlexDocumentManager() throws Exception {
        return getRequestBoundCoreSession();
    }

    public static void release() {
        if (session.get() != null) {
            try {
                Repository.close(session.get());
            } finally {
                session.remove();
            }
        }
    }

}
