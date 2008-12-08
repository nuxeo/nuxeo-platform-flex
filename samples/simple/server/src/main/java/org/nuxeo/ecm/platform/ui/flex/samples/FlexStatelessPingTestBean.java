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

package org.nuxeo.ecm.platform.ui.flex.samples;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.remoting.WebRemote;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.flex.javadto.FlexDocumentModel;
import org.nuxeo.ecm.platform.ui.flex.mapping.DocumentModelTranslator;

@Name("flexStatelessPingTestBean")
@Scope(ScopeType.STATELESS)
public class FlexStatelessPingTestBean implements Serializable {

    /**
     *
     */

    @In(create=false,required=false)
    NuxeoPrincipal flexUser;

    private static final long serialVersionUID = 1L;

    @In(create=true)
    CoreSession flexDocumentManager;

    @WebRemote
    public String ping() {
        return "Hello from stateless Nuxeo Seam Bean";
    }


    @WebRemote
    public String pingUser() {

        if (flexUser==null)
            return "Stateless Nuxeo Seam bean saye Hello to null user";
        else
            return "Stateless Nuxeo Seam bean saye Hello to " + flexUser.getName();
    }

    @WebRemote
    public DummyBean testSerialization() {

        DummyBean test = new DummyBean();
        test.setMyField("this field was set from Seam");

        return test;
    }


    @WebRemote
    public DocumentModel getTestDocumentModel() throws Exception
    {
        FlexDocumentModel doc = new FlexDocumentModel();

        Map<String, Serializable> schemadata = new HashMap<String, Serializable>();

        schemadata.put("title", "I am a test");
        List<String> list = new ArrayList<String>();
        list.add("1");
        list.add("2");
        schemadata.put("list", (Serializable)list);

        doc.feed("dublincore", schemadata);

        return DocumentModelTranslator.toDocumentModel(doc, flexDocumentManager);
    }

    @WebRemote
    public DocumentModel getSomeDocumentModel(String path) throws Exception
    {
        DocumentModel doc = flexDocumentManager.getDocument(new PathRef(path));
        //CoreInstance.getInstance().close(session);
        return doc;
    }

    @WebRemote
    public DocumentModel saveDocument(DocumentModel dm) throws Exception
    {
        dm=flexDocumentManager.saveDocument(dm);
        flexDocumentManager.save();
        return dm;
    }


}
