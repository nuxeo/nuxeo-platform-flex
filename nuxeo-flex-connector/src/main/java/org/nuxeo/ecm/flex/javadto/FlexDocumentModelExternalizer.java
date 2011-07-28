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

package org.nuxeo.ecm.flex.javadto;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.Map;

import org.granite.messaging.amf.io.util.externalizer.DefaultExternalizer;

/**
 * Manage AMF Marshaling of the {@link FlexDocumentModel}
 *
 * @author Tiry (tdelprat@nuxeo.com)
 *
 */
public class FlexDocumentModelExternalizer extends DefaultExternalizer {

    @Override
    public void readExternal(Object o, ObjectInput in) throws IOException,
            ClassNotFoundException, IllegalAccessException {
        if (o instanceof FlexDocumentModel) {
            FlexDocumentModel fdm = (FlexDocumentModel) o;
            //fdm.setSessionId(in.readUTF());
            fdm.setDocRef(in.readUTF());
            fdm.setName(in.readUTF());
            fdm.setPath(in.readUTF());
            fdm.setLifeCycleState(in.readUTF());
            fdm.setType(in.readUTF());
            fdm.setIcon(in.readUTF());
            fdm.setIconExpanded(in.readUTF());
            fdm.setIsFolder(in.readBoolean());
            //only ready dirty fields
            fdm.setDirtyFields((Map<String, Serializable>) in.readObject());
        }
    }

    @Override
    public void writeExternal(Object o, ObjectOutput out) throws IOException,
            IllegalAccessException {
        if (o instanceof FlexDocumentModel) {
            FlexDocumentModel fdm = (FlexDocumentModel) o;
            //out.writeUTF(fdm.getSessionId());
            out.writeUTF(fdm.getDocRef());
            if (fdm.getName() == null) {
                out.writeUTF("");
            } else {
                out.writeUTF(fdm.getName());
            }
            out.writeUTF(fdm.getPath());
            if (fdm.getLifeCycleState() == null) {
                out.writeUTF("");
            } else {
                out.writeUTF(fdm.getLifeCycleState());
            }
            out.writeUTF(fdm.getType());
            out.writeUTF(fdm.getIcon());
            out.writeUTF(fdm.getIconExpanded());
            out.writeBoolean(fdm.getIsFolder());
            // send data
            out.writeObject(fdm.getData());
            // send dirty fields too : useful for managing transient state on the server side
            out.writeObject(fdm.getDirtyFields());
            // send a Flag
            out.writeBoolean(fdm.getDirtyFields().size()>0);

        }
    }

}
