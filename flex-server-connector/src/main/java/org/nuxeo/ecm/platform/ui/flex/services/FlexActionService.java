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

package org.nuxeo.ecm.platform.ui.flex.services;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.remoting.WebRemote;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.platform.actions.Action;
import org.nuxeo.ecm.platform.actions.ActionContext;
import org.nuxeo.ecm.platform.actions.ejb.ActionManager;
import org.nuxeo.ecm.platform.ui.web.util.SeamContextHelper;

@Name("flexActionService")
@Scope(ScopeType.STATELESS)
public class FlexActionService {

    @In(create = true)
    private ActionManager actionManager;

    @In(create=true)
    private CoreSession flexDocumentManager;

    @In(create=true)
    private FlexNavigationContext flexNavigationContext;

    @In(create=false,required=false)
    NuxeoPrincipal flexUser;

    @WebRemote
    public List<Action> getActionsListInContext(List<String> categories) throws ClientException {
        List<Action> list = new ArrayList<Action>();
        ActionContext context = createActionContext(null);

        for (String category : categories) {
            List<Action> actions = actionManager.getActions(category, context);
            if (actions != null) {
                list.addAll(actions);
            }
        }
        return list;
    }

    @WebRemote
    public List<Action> getActionsList(List<String> categories, String currentDocumentId) throws ClientException {
        List<Action> list = new ArrayList<Action>();
        ActionContext context = createActionContext(currentDocumentId);
        for (String category : categories) {
            List<Action> actions = actionManager.getActions(category, context);
            if (actions != null) {
                list.addAll(actions);
            }
        }
        return list;
    }

    private ActionContext createActionContext(String currentDocumentId) throws ClientException {
        ActionContext ctx = new ActionContext();

        if (currentDocumentId != null) {
            DocumentModel currentDoc = flexDocumentManager.getDocument(new IdRef(currentDocumentId));
            flexNavigationContext.setCurrentDocument(currentDoc);
        }

        ctx.setCurrentDocument(flexNavigationContext.getCurrentDocument());
        ctx.setDocumentManager(flexDocumentManager);
        ctx.put("SeamContext", new SeamContextHelper());
        ctx.setCurrentPrincipal(flexUser);
        return ctx;
    }

}
