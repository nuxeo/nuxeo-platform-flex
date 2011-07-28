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

package org.nuxeo.ecm.platform.ui.flex.tests;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.impl.DocumentModelImpl;
import org.nuxeo.ecm.core.repository.jcr.testing.RepositoryOSGITestCase;
import org.nuxeo.ecm.flex.javadto.FlexDocumentModel;
import org.nuxeo.ecm.platform.ui.flex.mapping.DocumentModelTranslator;
import org.nuxeo.ecm.platform.ui.flex.services.AttachHelper;

public class FlexDocumentModelGenerationTests extends RepositoryOSGITestCase {

    private DocumentModel doc;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        deployBundle("org.nuxeo.ecm.platform.content.template");
        deployBundle("org.nuxeo.ecm.platform.types.api");
        deployBundle("org.nuxeo.ecm.platform.types.core");
        openRepository();
        createDocuments();
    }

    private void createDocuments() throws Exception {
        DocumentModel wsRoot = coreSession.getDocument(new PathRef(
                "default-domain/workspaces"));

        DocumentModel ws = coreSession.createDocumentModel(
                wsRoot.getPathAsString(), "ws1", "Workspace");
        ws.setProperty("dublincore", "title", "test WS");
        ws = coreSession.createDocument(ws);

        doc = coreSession.createDocumentModel(ws.getPathAsString(), "file",
                "File");
        doc.setProperty("dublincore", "title", "MyDoc");
        doc.setProperty("dublincore", "coverage", "MyDocCoverage");
        doc = coreSession.createDocument(doc);
    }

    public void testGenFlexDocumentModel() throws Exception {
        createDocuments();

        assertEquals("File", doc.getType());
        FlexDocumentModel fdm = DocumentModelTranslator.toFlexType(doc);

        String title = (String) fdm.getProperty("dublincore", "title");
        assertEquals("MyDoc", title);

        String coverage = (String) fdm.getProperty("dublincore", "coverage");
        assertEquals("MyDocCoverage", coverage);
    }

    public void testGenFlexDocumentModelFromPrefetch() throws Exception {
        FlexDocumentModel fdm = DocumentModelTranslator.toFlexTypeFromPrefetch(doc);

        String title = (String) fdm.getProperty("dublincore", "title");
        assertEquals("MyDoc", title);

        String coverage = (String) fdm.getProperty("dublincore", "coverage");
        assertNull(coverage);
    }

    public void testAttach() throws Exception {

        DocumentModel doc = session.getRootDocument();
        System.out.println("SID=" + doc.getSessionId());

        ((DocumentModelImpl)doc).detach(true);
        System.out.println("SID=" + doc.getSessionId());

        AttachHelper.attach((DocumentModelImpl)doc, "777");
        System.out.println("SID=" + doc.getSessionId());

        AttachHelper.attach((DocumentModelImpl)doc, "999");
        System.out.println("SID=" + doc.getSessionId());

    }


}
