package org.nuxeo.ecm.platform.ui.granite.factory;

import java.util.Map;

import org.granite.config.flex.Destination;
import org.granite.context.GraniteContext;
import org.granite.messaging.service.ServiceException;
import org.granite.messaging.service.ServiceFactory;
import org.granite.messaging.service.ServiceInvoker;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;
import org.nuxeo.runtime.api.Framework;

import flex.messaging.messages.RemotingMessage;

public class NuxeoRuntimeServiceFactory extends ServiceFactory {

    private static final long serialVersionUID = 1L;

    private static final Log log = Logging.getLog(NuxeoRuntimeServiceFactory.class);

    @Override
    public void configure(Map<String, Object> properties) throws ServiceException {
        super.configure(properties);
    }

    @Override
    public ServiceInvoker<?> getServiceInstance(RemotingMessage request) throws ServiceException {
        String messageType = request.getClass().getName();
        String destinationId = request.getDestination();

        GraniteContext context = GraniteContext.getCurrentInstance();
        Destination destination = context.getServicesConfig().findDestinationById(messageType, destinationId);
        if (destination == null)
            throw new ServiceException("No matching destination: " + destinationId);

        // all we need is to get bean name
        String clazz = (String) destination.getProperties().get("class");

        // Find the component we're calling
        Class service;
        try {
            service = Class.forName(clazz);
        } catch (ClassNotFoundException e2) {
            String msg = "Unable to find Class [" + clazz + "]";
            log.error(msg);
            ServiceException e = new ServiceException(msg, e2);
            throw e;
        }

        if (service == null) {
            String msg = "Unable to find Class [" + clazz + "]";
            log.error(msg);
            ServiceException e = new ServiceException(msg);
            throw e;
        }

        //Create an instance of the component
        Object instance;
        try {
            instance = Framework.getService(service);
        } catch (Exception e1) {
            String msg = "Unable to find service [" + clazz + "]";
            log.error(msg);
            ServiceException e = new ServiceException(msg, e1);
            throw e;
        }


        if (instance == null) {
            String msg = "Unable to find service [" + clazz + "]";
            log.error(msg);
            ServiceException e = new ServiceException(msg);
            throw e;
        }
        
        return new NuxeoRuntimeServiceInvoker(destination, this, instance);
    }

}
