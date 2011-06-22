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

package org.nuxeo.ecm.platform.ui.flex.listener;

import java.util.List;

import org.granite.messaging.service.ServiceInvocationContext;
import org.granite.messaging.service.ServiceInvocationListener;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.flex.javadto.FlexDocumentModel;
import org.nuxeo.ecm.platform.ui.flex.mapping.DocumentModelTranslator;

/**
 * Manage types translation for parameters and return values.
 *
 * For example, AMF Call will always maipulate {@link FlexDocumentModel} whereas
 * the java part will always deal with {@link DocumentModel}. This listener is
 * responsible for doing the signatures translations and the return values
 * conversions.
 *
 * @author Tiry (tdelprat@nuxeo.com)
 *
 */
public class NuxeoServiceInvocationListener implements
        ServiceInvocationListener {

    public Object afterInvocation(ServiceInvocationContext context,
            Object result) {
        try {
            if (result instanceof DocumentModel) {
                return DocumentModelTranslator.toFlexType((DocumentModel) result);
            } else if (result instanceof List) {
                List lst = (List) result;
                for (int i = 0; i < lst.size(); i++) {
                    Object o = lst.get(i);

                    if (o instanceof DocumentModel) {
                        lst.remove(i);
                        lst.add(
                                i,
                                DocumentModelTranslator.toFlexType((DocumentModel) o));
                    }
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
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
                    args[i] = DocumentModelTranslator.toDocumentModel((FlexDocumentModel) args[i]);
                }
                if (args[i] instanceof String) {
                    String[] tokens = ((String) args[i]).split(":");
                    if (tokens[0].endsWith("idRef")) {
                        args[i] = new IdRef(tokens[1]);
                    } else if (tokens[0].endsWith("pathRef")) {
                        args[i] = new PathRef(tokens[1]);
                    }
                }
            }
        } catch (Exception e) {

        }
        return args;
    }

}
