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

package org.nuxeo.ecm.platform.ui.granite.factory;

import org.granite.config.flex.Destination;
import org.granite.context.GraniteContext;
import org.granite.messaging.service.ServiceException;
import org.granite.messaging.service.ServiceFactory;
import org.granite.messaging.service.ServiceInvoker;
import org.granite.util.XMap;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;
import org.nuxeo.runtime.api.Framework;

import flex.messaging.messages.RemotingMessage;

/**
 * {@link ServiceFactory} for Nuxeo Runtime Services
 *
 * @author Tiry (tdelprat@nuxeo.com)
 *
 */
public class NuxeoRuntimeServiceFactory extends ServiceFactory {

    private static final long serialVersionUID = 1L;

    private static final Log log = Logging.getLog(NuxeoRuntimeServiceFactory.class);

    @Override
    public void configure(XMap properties) throws ServiceException {
        super.configure(properties);
    }

    @Override
    public ServiceInvoker<?> getServiceInstance(RemotingMessage request)
            throws ServiceException {
        String messageType = request.getClass().getName();
        String destinationId = request.getDestination();

        GraniteContext context = GraniteContext.getCurrentInstance();
        Destination destination = context.getServicesConfig().findDestinationById(
                messageType, destinationId);
        if (destination == null) {
            throw new ServiceException("No matching destination: "
                    + destinationId);
        }

        // all we need is to get bean name
        String clazz = (String) destination.getProperties().get("class");

        // Find the component we're calling
        Class service;
        try {
            service = Class.forName(clazz);
        } catch (ClassNotFoundException e2) {
            String msg = "Unable to find Class [" + clazz + "]";
            log.error(msg);
            throw new ServiceException(msg, e2);
        }

        if (service == null) {
            String msg = "Unable to find Class [" + clazz + "]";
            log.error(msg);
            throw new ServiceException(msg);
        }

        // Create an instance of the component
        Object instance;
        try {
            instance = Framework.getService(service);
        } catch (Exception e1) {
            String msg = "Unable to find service [" + clazz + "]";
            log.error(msg);
            throw new ServiceException(msg, e1);
        }

        if (instance == null) {
            String msg = "Unable to find service [" + clazz + "]";
            log.error(msg);
            throw new ServiceException(msg);
        }

        return new NuxeoRuntimeServiceInvoker(destination, this, instance);
    }

}
