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

import org.jboss.seam.annotations.remoting.WebRemote;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Provide Session level storage for the {@link DocumentModel} used in the Seam
 * Controlers. The {@link CoreSession} exposed in Seam in Event Scoped, so you
 * can not simply store the {@link DocumentModel} in the Seam Context. This Seam
 * Component provide the logics to store DocumentModel within the Event Scope
 * and automatically refetch the document from the current {@link CoreSession}
 * when accessed outside the initial Event Scope.
 *
 * If in addition, you want to maintain some transient state in the
 * DocumentModel (i.e. you can not save it for now in the repository), and want
 * to access it in the same state later (for example because you use several
 * screens to handle the complete update), then you should use the
 * storeEditableDocument and getStoredEditableDocument methods.

 *
 * @author Tiry (tdelprat@nuxeo.com)
 *
 */
public interface FlexContextManager {

    /**
     * Set a {@link DocumentModel} inside the context.
     *
     * @param name
     * @param doc
     */
    @WebRemote
    public void setDocument(String name, DocumentModel doc);

    /**
     * Store a {@link DocumentModel} in a context and keeps tracks of the modifications that may have been done.
     *
     * @param name
     * @param doc
     */
    @WebRemote
    public void storeEditableDocument(String name, DocumentModel doc);

    @WebRemote
    public DocumentModel getDocument(String name);

    @WebRemote
    public DocumentModel getStoredEditableDocument(String name);

    @WebRemote
    public DocumentModel getCurrentDocument();

    @WebRemote
    public void setCurrentDocument(DocumentModel currentDocument);

}
