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

package org.nuxeo.ecm.platform.ui.flex.auth;

import java.util.ArrayList;
import java.util.List;

import org.nuxeo.ecm.platform.ui.web.auth.plugins.FormAuthenticator;

/**
 * Extends default {@link FormAuthenticator} just to change the login page and
 * have a different plugin name
 *
 * @author Tiry (tdelprat@nuxeo.com)
 *
 */
public class FlexAuthenticationPlugin extends FormAuthenticator {

    protected String loginPage = "login.swf";

    protected String usernameKey = "user_name";

    protected String passwordKey = "user_password";

    @Override
    public List<String> getUnAuthenticatedURLPrefix() {
        // Login Page is unauthenticated !
        List<String> prefix = new ArrayList<String>();
        prefix.add(loginPage);
        return prefix;
    }

}
