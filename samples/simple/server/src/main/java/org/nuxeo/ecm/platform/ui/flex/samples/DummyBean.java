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

public class DummyBean implements Serializable {

    protected String myField;
    /**
     *
     */
    private static final long serialVersionUID = 1L;


    public DummyBean()
    {
       myField="default dummy value";
    }

    public String getMyField() {
        return myField;
    }


    public void setMyField(String myField) {
        this.myField = myField;
    }

    public void doSomething()
    {
        // not impl on the server side
    }
}
