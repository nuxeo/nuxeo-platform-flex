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

import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.nuxeo.ecm.core.api.ClientRuntimeException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.impl.DocumentModelImpl;

/**
 *
 * @author Tiry (tdelprat@nuxeo.com)
 *
 */
@Name("flexNavigationContext")
@Scope(ScopeType.SESSION)
public class FlexNavigationContext implements FlexContextManager {

    @In(create = true)
    private CoreSession flexDocumentManager;

    public static final String CURRENT_DOCUMENT = "currentDocument";

    protected static final String CONTEXT_PREFIX = "org.nuxeo.flex.context.namedDocument.";

    protected Map<String, DocumentRef> docRefs = new HashMap<String, DocumentRef>();

    protected Map<String, DocumentModelImpl> docModels = new HashMap<String, DocumentModelImpl>();

    public void setDocument(String name, DocumentModel doc) {
        if (doc.getId()==null || doc.getId().equals("")) {
            return;
        }
        // store ref
        docRefs.put(name, doc.getRef());
        // store in Event scope cache
        Contexts.getEventContext().set(CONTEXT_PREFIX + name, doc);
    }

    public void storeEditableDocument(String name, DocumentModel doc) {

        // store the ref too
        setDocument(name, doc);

        DocumentModelImpl docModel = (DocumentModelImpl) doc;

        if (doc.getId()!=null && !doc.getId().equals("")) {
            // detach the Document from it's current session
            try {
                docModel.getCurrentLifeCycleState();
                docModel.detach(true);
            } catch (Exception e) {
                throw new ClientRuntimeException("Unable to detach DocumentModel",
                        e);
            }
        }
        docModels.put(name, docModel);
    }

    public DocumentModel getDocument(String name) {

        // lookup in Event scope cache
        if (Contexts.getEventContext().isSet(CONTEXT_PREFIX + name)) {
            return (DocumentModel) Contexts.getEventContext().get(
                    CONTEXT_PREFIX + name);
        }

        DocumentRef ref = docRefs.get(name);
        if (ref != null) {
            try {
                if (flexDocumentManager.exists(ref)) {
                    DocumentModel refreshedDoc = flexDocumentManager.getDocument(ref);

                    if (docModels.containsKey(name)) {
                        // get stalled cached Document
                        DocumentModelImpl doc = docModels.get(name);
                        // update newlyFetched Document
                        refreshedDoc.copyContent(doc);
                    }
                    return refreshedDoc;
                }
            } catch (Exception e) {
                throw new ClientRuntimeException(
                        "Unable to refetch DocumentModel", e);
            }
        }
        return null;
    }

    public DocumentModel getStoredEditableDocument(String name) {
        if (!docModels.containsKey(name)) {
            return null;
        }
        // get stalled cached Document
        DocumentModelImpl doc = docModels.get(name);
        // attach to current Session
        AttachHelper.attach(doc, flexDocumentManager.getSessionId());

        return doc;
    }

    public DocumentModel getCurrentDocument() {
        return getDocument(CURRENT_DOCUMENT);
    }

    public void setCurrentDocument(DocumentModel currentDocument) {
        setDocument(CURRENT_DOCUMENT, currentDocument);
    }

    public void clear(String name) {
        docRefs.remove(name);
        docModels.remove(name);
    }
}
