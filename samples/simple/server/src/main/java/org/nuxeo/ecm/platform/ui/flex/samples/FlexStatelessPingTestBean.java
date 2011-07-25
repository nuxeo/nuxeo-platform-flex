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

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.remoting.WebRemote;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.api.PathRef;

/**
 * Simple Seam bean used to demo
 * simple calls from the flex client
 *
 * @author Tiry (tdelprat@nuxeo.com)
 *
 */
@Name("flexStatelessPingTestBean")
@Scope(ScopeType.STATELESS)
public class FlexStatelessPingTestBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @In(create=false,required=false)
    NuxeoPrincipal flexUser;

    @In(create=true)
    CoreSession flexDocumentManager;

    /**
     * Simple call
     * @return
     */
    @WebRemote
    public String ping() {
        return "Hello from a Nuxeo Seam Bean : " + Component.getComponentName(this.getClass());
    }

    /**
     * Simple call using the context User
     * @return
     */
    @WebRemote
    public String pingUser() {
        if (flexUser==null)
            return "Stateless Nuxeo Seam bean saye Hello to null user";
        else
            return "Stateless Nuxeo Seam bean saye Hello to " + flexUser.getName();
    }

    /**
     * Test bean serialization
     *
     * @return
     */
    @WebRemote
    public DummyBean getSimpleBean() {
        DummyBean test = new DummyBean();
        test.setMyField("This bean was initialized from the server side via a Seam bean");
        return test;
    }

    @WebRemote
    public String setSimpleBean(DummyBean beanFromFlex) {
        return beanFromFlex.myField;
    }

    @WebRemote
    public DocumentModel getSomeDocumentModel(String path) throws Exception
    {
        DocumentModel doc = flexDocumentManager.getDocument(new PathRef(path));
        return doc;
    }

    @WebRemote
    public String setSomeDocumentModel(DocumentModel doc) throws Exception
    {
        return doc.getTitle();
    }

}
