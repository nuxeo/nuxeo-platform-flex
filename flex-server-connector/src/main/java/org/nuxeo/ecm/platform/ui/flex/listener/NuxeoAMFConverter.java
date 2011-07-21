package org.nuxeo.ecm.platform.ui.flex.listener;

import java.lang.reflect.Type;

import org.granite.messaging.amf.io.convert.Converter;
import org.granite.messaging.amf.io.convert.Converters;
import org.granite.messaging.amf.io.convert.Reverter;

public class NuxeoAMFConverter extends Converter implements Reverter {

    public NuxeoAMFConverter(Converters converters) {
        super(converters);
    }

    @Override
    protected boolean internalCanConvert(Object paramObject, Type paramType) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected Object internalConvert(Object paramObject, Type paramType) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean canRevert(Object paramObject) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Object revert(Object paramObject) {
        // TODO Auto-generated method stub
        return null;
    }

}
