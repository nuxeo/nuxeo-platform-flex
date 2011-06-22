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
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.flex.javadto.FlexDocumentModel;

/**
 * Minimal Context management
 *
 * @author Tiry (tdelprat@nuxeo.com)
 *
 */
public interface FlexContextManager {

    @WebRemote
    FlexDocumentModel getCurrentFlexDocument() throws Exception;

    @WebRemote
    void setCurrentFlexDocument(FlexDocumentModel currentDocument)
            throws Exception;

    DocumentModel getCurrentDocument();

    void setCurrentDocument(DocumentModel currentDocument);

}
