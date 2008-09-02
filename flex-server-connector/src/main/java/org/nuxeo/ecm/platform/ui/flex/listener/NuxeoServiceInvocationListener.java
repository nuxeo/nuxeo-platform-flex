package org.nuxeo.ecm.platform.ui.flex.listener;

import java.util.ArrayList;
import java.util.List;

import org.granite.messaging.service.ServiceInvocationContext;
import org.granite.messaging.service.ServiceInvocationListener;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.flex.javadto.FlexDocumentModel;
import org.nuxeo.ecm.platform.ui.flex.mapping.DocumentModelTranslator;

public class NuxeoServiceInvocationListener implements
        ServiceInvocationListener {

    public Object afterInvocation(ServiceInvocationContext context,
            Object result) {
        try
        {
            if (result instanceof DocumentModel)
            {
                return DocumentModelTranslator.toFlexType((DocumentModel) result );
            }
            else if (result instanceof List)
            {
                List lst = (List) result;
                for (int i=0;i<lst.size();i++)
                {
                    Object o = lst.get(i);

                    if (o instanceof DocumentModel)
                    {
                        lst.remove(i);
                        lst.add(i, DocumentModelTranslator.toFlexType((DocumentModel) o));
                    }
                }
            }
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public void afterInvocationError(ServiceInvocationContext context,
            Throwable t) {
        // TODO Auto-generated method stub

    }

    public void beforeInvocation(ServiceInvocationContext context) {
        // TODO Auto-generated method stub

    }

    public Object[] beforeMethodSearch(Object invokee, String methodName,
            Object[] args) {
        try {
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof FlexDocumentModel) {
                    args[i] = DocumentModelTranslator
                            .toDocumentModel((FlexDocumentModel) args[i]);
                }
            }
        } catch (Exception e) {

        }
        return args;
    }

}
