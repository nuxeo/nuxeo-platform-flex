package org.nuxeo.ecm.platform.ui.granite.factory;

import org.granite.config.flex.Destination;
import org.granite.logging.Logger;
import org.granite.messaging.service.ServiceException;
import org.granite.messaging.service.ServiceInvocationContext;
import org.granite.messaging.service.ServiceInvoker;

public class NuxeoRuntimeServiceInvoker extends ServiceInvoker<NuxeoRuntimeServiceFactory>  {


    private static final long serialVersionUID = 1L;

    private static final Logger log = Logger.getLogger(NuxeoRuntimeServiceInvoker.class);

    public static final String CAPITALIZED_DESTINATION_ID = "{capitalized.destination.id}";
    public static final String DESTINATION_ID = "{destination.id}";

    public NuxeoRuntimeServiceInvoker(Destination destination, NuxeoRuntimeServiceFactory factory, Object instance)
        throws ServiceException {
        super(destination, factory);

        this.invokee = instance;
    }

    @Override
    protected void beforeInvocation(ServiceInvocationContext context) {
        log.debug("Before Invocation");
    }

    @Override
    protected Object afterInvocation(ServiceInvocationContext context, Object result) {
        log.debug("After Invocation");
        return result;
    }

}
