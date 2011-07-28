/*
  GRANITE DATA SERVICES
  Copyright (C) 2007-2008 ADEQUATE SYSTEMS SARL

  This file is part of Granite Data Services.

  Granite Data Services is free software; you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as published by
  the Free Software Foundation; either version 3 of the License, or (at your
  option) any later version.

  Granite Data Services is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
  FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
  for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, see <http://www.gnu.org/licenses/>.
 */

package org.nuxeo.ecm.platform.ui.granite.factory;

import java.security.Principal;
import org.granite.config.flex.Destination;
import org.granite.context.GraniteContext;
import org.granite.messaging.service.ServiceException;
import org.granite.messaging.service.ServiceFactory;
import org.granite.messaging.service.ServiceInvoker;
import org.granite.messaging.webapp.HttpGraniteContext;
import org.granite.seam.SeamServiceFactory;
import org.granite.seam.SeamServiceInvoker;
import org.jboss.seam.Component;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;

import flex.messaging.messages.RemotingMessage;

/**
 * {@link ServiceFactory} for Seam Components
 *
 * Overrides default Granite Implementation to inject flexUser into the context
 *
 * @author Laurent Doguin
 * @author Tiry (tdelprat@nuxeo.com)
 */
public class NuxeoSeamServiceFactory extends SeamServiceFactory {

    private static final long serialVersionUID = 1L;

    private static final Log log = Logging.getLog(NuxeoSeamServiceFactory.class);

    @Override
    public ServiceInvoker<?> getServiceInstance(RemotingMessage request)
            throws ServiceException {
        String messageType = request.getClass().getName();
        String destinationId = request.getDestination();

        GraniteContext context = GraniteContext.getCurrentInstance();
        if (context instanceof HttpGraniteContext) {
            Principal principal = ((HttpGraniteContext) context).getRequest().getUserPrincipal();
            Contexts.getEventContext().set("flexUser",
                    (NuxeoPrincipal) principal);
        }

        Destination destination = context.getServicesConfig().findDestinationById(
                messageType, destinationId);
        if (destination == null) {
            throw new ServiceException("No matching destination: "
                    + destinationId);
        }

        // all we need is to get bean name
        String componentName = destinationId;

        // Find the component we're calling
        Component component = Component.forName(destinationId);

        if (component == null) {
            String msg = "Unable to create a Seam component named ["
                    + componentName + "]";
            log.error(msg);
            throw new ServiceException(msg);
        }

        // Create an instance of the component
        Object instance = Component.getInstance(componentName, true);

        return new SeamServiceInvoker(destination, this, instance);
    }

}
