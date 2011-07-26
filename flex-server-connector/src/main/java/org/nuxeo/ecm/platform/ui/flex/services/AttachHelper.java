package org.nuxeo.ecm.platform.ui.flex.services;

import java.lang.reflect.Field;

import org.nuxeo.ecm.core.api.ClientRuntimeException;
import org.nuxeo.ecm.core.api.impl.DocumentModelImpl;

public class AttachHelper {

    protected static Field sidField;

    protected static Field getSidField() {
        if (sidField==null) {
            for (Field field : DocumentModelImpl.class.getDeclaredFields()) {
                if (field.getName().equals("sid")) {
                    field.setAccessible(true);
                    sidField = field;
                    break;
                }
            }
        }
        return sidField;
    }

    public static void attach(DocumentModelImpl doc, String sid) {

        try {
            getSidField().set(doc, sid);
        } catch (Exception e) {
            throw new ClientRuntimeException("Unable to set session id", e);
        }
    }
}
