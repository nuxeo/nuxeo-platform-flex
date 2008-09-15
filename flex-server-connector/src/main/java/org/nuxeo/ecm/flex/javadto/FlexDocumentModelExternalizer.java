package org.nuxeo.ecm.flex.javadto;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.Map;

import org.granite.messaging.amf.io.util.externalizer.DefaultExternalizer;

public class FlexDocumentModelExternalizer extends DefaultExternalizer {


    @Override
    public void readExternal(Object o, ObjectInput in) throws IOException,
            ClassNotFoundException, IllegalAccessException {
        if (o instanceof FlexDocumentModel){
            FlexDocumentModel fdm = (FlexDocumentModel)o;
            fdm.setSessionId((String) in.readUTF());
            fdm.setDocRef((String) in.readUTF());
            fdm.setName((String) in.readUTF());
            fdm.setPath((String) in.readUTF());
            fdm.setLifeCycleState((String) in.readUTF());
            fdm.setType((String) in.readUTF());
            fdm.setIsFolder((Boolean)in.readBoolean());
            //only ready dirty fields
            fdm.setDirtyFields((Map<String,Serializable>) in.readObject());
        }
    }

    @Override
    public void writeExternal(Object o, ObjectOutput out) throws IOException,
            IllegalAccessException {
        if (o instanceof FlexDocumentModel){
            FlexDocumentModel fdm = (FlexDocumentModel)o;
            out.writeUTF(fdm.getSessionId());
            out.writeUTF(fdm.getDocRef());
            if (fdm.getName()==null)
                out.writeUTF("");
            else
                out.writeUTF(fdm.getName());
            out.writeUTF(fdm.getPath());
            if (fdm.getLifeCycleState()==null)
                out.writeUTF("");
            else
                out.writeUTF(fdm.getLifeCycleState());
            out.writeUTF(fdm.getType());
            out.writeBoolean(fdm.getIsFolder());
            // only sends data : nothing is dirty for now
            out.writeObject(fdm.getData());

        }
    }
}
