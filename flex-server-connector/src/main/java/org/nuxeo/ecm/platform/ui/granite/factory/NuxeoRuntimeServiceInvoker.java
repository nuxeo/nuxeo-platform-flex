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
import org.granite.logging.Logger;
import org.granite.messaging.service.ServiceException;
import org.granite.messaging.service.ServiceInvocationContext;
import org.granite.messaging.service.ServiceInvoker;

public class NuxeoRuntimeServiceInvoker extends
        ServiceInvoker<NuxeoRuntimeServiceFactory> {

    private static final long serialVersionUID = 1L;

    private static final Logger log = Logger.getLogger(NuxeoRuntimeServiceInvoker.class);

    public static final String CAPITALIZED_DESTINATION_ID = "{capitalized.destination.id}";

    public static final String DESTINATION_ID = "{destination.id}";

    public NuxeoRuntimeServiceInvoker(Destination destination,
            NuxeoRuntimeServiceFactory factory, Object instance)
            throws ServiceException {
        super(destination, factory);

        invokee = instance;
    }

    @Override
    protected void beforeInvocation(ServiceInvocationContext context) {
        log.debug("Before Invocation");
    }

    @Override
    protected Object afterInvocation(ServiceInvocationContext context,
            Object result) {
        log.debug("After Invocation");
        return result;
    }

}
