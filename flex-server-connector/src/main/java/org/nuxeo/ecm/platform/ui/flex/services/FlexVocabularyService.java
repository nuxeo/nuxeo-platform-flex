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

package org.nuxeo.ecm.platform.ui.flex.services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Locale;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.remoting.WebRemote;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.directory.Session;
import org.nuxeo.ecm.directory.api.DirectoryService;
import org.nuxeo.runtime.api.Framework;

@Name("flexVocabularyService")
@Scope(ScopeType.STATELESS)
public class FlexVocabularyService {

    private Locale translationLocal = Locale.getDefault();

    private String getTranslation(String key, Locale local) {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("messages", local,
                    Thread.currentThread().getContextClassLoader());
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            return key;
        }
    }

    @WebRemote
    public List<Object> getVocabularyEntries(String vocName) throws Exception {
        return getVocabularyEntries(vocName, null);
    }

    @WebRemote
    public List<Object> getVocabularyEntries(String vocName, String parentKey)
            throws Exception {
        DirectoryService directoryService = Framework.getService(DirectoryService.class);
        Session dirSession = directoryService.open(vocName);

        String directorySchema = directoryService.getDirectorySchema(vocName);

        List<Object> entries = new ArrayList<Object>();

        if (directorySchema.equals("vocabulary")) {
            for (DocumentModel entry : dirSession.getEntries()) {

                Map<String, String> mapEntry = new HashMap<String, String>();

                String label = getTranslation((String) entry.getProperty(
                        "vocabulary", "label"), translationLocal);
                mapEntry.put("label", label);
                mapEntry.put("data", entry.getId());
                entries.add(mapEntry);

            }
        } else if (directorySchema.equals("xvocabulary")) {

            Map<String, Serializable> filter = new HashMap<String, Serializable>();

            if (parentKey != null) {
                filter.put("parent", parentKey);
            }

            for (DocumentModel entry : dirSession.query(filter)) {

                Map<String, String> mapEntry = new HashMap<String, String>();

                String label = getTranslation((String) entry.getProperty(
                        "xvocabulary", "label"), translationLocal);
                mapEntry.put("label", label);
                mapEntry.put("data", entry.getId());
                entries.add(mapEntry);

            }
        }
        try {
            dirSession.close();
        } catch (ClientException e) {
            // XXX
        }
        return entries;
    }

}
