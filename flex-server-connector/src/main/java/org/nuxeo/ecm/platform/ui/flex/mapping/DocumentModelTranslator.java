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

package org.nuxeo.ecm.platform.ui.flex.mapping;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.common.utils.Path;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreInstance;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.model.DocumentPart;
import org.nuxeo.ecm.core.api.model.Property;
import org.nuxeo.ecm.core.api.model.impl.ArrayProperty;
import org.nuxeo.ecm.core.api.model.impl.ListProperty;
import org.nuxeo.ecm.core.api.model.impl.MapProperty;
import org.nuxeo.ecm.core.api.model.impl.primitives.BlobProperty;
import org.nuxeo.ecm.core.schema.SchemaManager;
import org.nuxeo.ecm.core.schema.types.Schema;
import org.nuxeo.ecm.flex.javadto.FlexDocumentModel;
import org.nuxeo.ecm.platform.types.Type;
import org.nuxeo.ecm.platform.types.TypeManager;
import org.nuxeo.ecm.platform.ui.web.util.BaseURL;
import org.nuxeo.runtime.api.Framework;

public class DocumentModelTranslator {

    private static final Log log = LogFactory.getLog(DocumentModelTranslator.class);

    private static SchemaManager sm;

    private static final Map<String, String> schemaCache = new ConcurrentHashMap<String, String>();

    private static String getSchemaFromPrefix(String prefix) throws Exception {
        String schemaName = schemaCache.get(prefix);
        if (schemaName != null) {
            return schemaName;
        }

        if (sm == null) {
            sm = Framework.getService(SchemaManager.class);
        }

        Schema schema = sm.getSchemaFromPrefix(prefix);

        schemaName = schema.getSchemaName();

        if (schemaName == null) {
            schemaName = prefix;
        }
        schemaCache.put(prefix, schemaName);
        return schemaName;
    }

    public static FlexDocumentModel toFlexTypeFromPrefetch(DocumentModel doc)
            throws Exception {
        String bigIcon = "";
        String bigIconExpanded = "";
        try {
            TypeManager typeManager = Framework.getService(TypeManager.class);
            Type docType = typeManager.getType(doc.getType());
            bigIcon = docType.getBigIcon();
            bigIconExpanded = docType.getBigIconExpanded();
        } catch (Exception e) {
            log.error("Could not get TypeManager Service, use default Icon.");
            bigIcon = "/icons/file_100.png";
            bigIconExpanded = "/icons/file_100.png";
        }
        FlexDocumentModel fdm = new FlexDocumentModel(doc.getSessionId(),
                doc.getRef(), doc.getName(), doc.getPathAsString(),
                doc.getCurrentLifeCycleState(), doc.getType(),
                BaseURL.getServerURL()+"nuxeo"+bigIcon,
                BaseURL.getServerURL()+"nuxeo"+bigIconExpanded);

        if (fdm.getType().equals("Picture")) {
            String bigDownloadURL = BaseURL.getServerURL() + "nuxeo/";
            bigDownloadURL += "nxbigfile" + "/";
            bigDownloadURL += doc.getRepositoryName() + "/";
            bigDownloadURL += doc.getRef().toString() + "/";
            bigDownloadURL += "picture:views/0/content/";
            bigDownloadURL += "midSize";
            fdm.setIcon(bigDownloadURL);
        }

        fdm.setIsFolder(doc.isFolder());

        Map<String, Serializable> prefetch = doc.getPrefetch();
        String[] schemas = doc.getDeclaredSchemas();

        for (String schema : schemas) {
            Map<String, Serializable> map = new HashMap<String, Serializable>();
            fdm.feed(schema, map);
        }

        for (String prefetchKey : prefetch.keySet()) {
            String schemaName;
            String fieldName;

            if (prefetchKey.contains(":")) {
                schemaName = prefetchKey.split(":")[0];
                fieldName = prefetchKey.split(":")[1];
                schemaName = getSchemaFromPrefix(schemaName);
            } else {
                schemaName = prefetchKey.split("\\.")[0];
                fieldName = prefetchKey.split("\\.")[1];
            }

            fdm.setProperty(schemaName, fieldName, prefetch.get(prefetchKey));
        }
        return fdm;
    }

    public static FlexDocumentModel toFlexType(DocumentModel doc)
            throws Exception {
        String bigIcon = "";
        String bigIconExpanded = "";
        try {
            TypeManager typeManager = Framework.getService(TypeManager.class);
            Type docType = typeManager.getType(doc.getType());
            bigIcon = docType.getBigIcon();
            bigIconExpanded = docType.getBigIconExpanded();
        } catch (Exception e) {
            log.error("Could not get TypeManager Service, use default Icon.");
            bigIcon = "/icons/file_100.png";
            bigIconExpanded = "/icons/file_100.png";
        }
        FlexDocumentModel fdm = new FlexDocumentModel(doc.getSessionId(),
                doc.getRef(), doc.getName(), doc.getPathAsString(),
                doc.getCurrentLifeCycleState(), doc.getType(),
                BaseURL.getServerURL()+"nuxeo"+bigIcon,
                BaseURL.getServerURL()+"nuxeo"+bigIconExpanded);

        if (fdm.getType().equals("Picture")){
            String bigDownloadURL = BaseURL.getServerURL()+"nuxeo/";
            bigDownloadURL += "nxbigfile" + "/";
            bigDownloadURL += doc.getRepositoryName() + "/";
            bigDownloadURL += doc.getRef().toString() + "/";
            bigDownloadURL += "picture:views/0/content/";
            bigDownloadURL += "midSize";
            fdm.setIcon(bigDownloadURL);
        }

        fdm.setIsFolder(doc.isFolder());

        DocumentPart[] parts = doc.getParts();

        for (DocumentPart part : parts) {
            Map<String, Serializable> map = new HashMap<String, Serializable>();
            Collection<Property> props = part.getChildren();

            String schemaPrefix = part.getSchema().getNamespace().prefix;
            if (schemaPrefix == "") {
                schemaPrefix = part.getSchema().getName();
            }

            for (Property prop : props) {
                String fieldName = prop.getName();
                fieldName = fieldName.replace(schemaPrefix + ":", "");
                map.put(fieldName, introspectProperty(prop, doc));
            }
            fdm.feed(part.getName(), map);
        }
        return fdm;
    }

    public static Serializable introspectProperty(Property prop, DocumentModel doc)
            throws Exception {
        if (prop.getType().isSimpleType()) {
            return prop.getValue();
        } else if (prop.getType().isComplexType()) {
            if (prop instanceof BlobProperty) {

                BlobProperty blobProp = (BlobProperty) prop;
                Blob blob = (Blob) blobProp.getValue();
                if (blob != null) {
                    String fileName = blob.getFilename();
                    if (fileName == null) {
                        // XXX Hack !
                        fileName = (String) doc.getProperty("dublincore",
                                "title");
                    }
                    String bigDownloadURL = BaseURL.getServerURL() + "nuxeo/";
                    bigDownloadURL += "nxbigfile" + "/";
                    bigDownloadURL += doc.getRepositoryName() + "/";
                    bigDownloadURL += doc.getRef().toString() + "/";
                    bigDownloadURL += prop.getSchema().getName() + ";" + prop.getName() + "/";
                    bigDownloadURL += fileName;
                    return bigDownloadURL;
                }
            } else if (prop instanceof MapProperty) {
                MapProperty mapProp = (MapProperty) prop;
                Set<Entry<String, Property>> properties = mapProp.entrySet();
                Map<String, Serializable> map = new HashMap<String, Serializable>();
                for (Entry<String, Property> entry : properties) {
                    map.put(entry.getKey(), introspectProperty(entry.getValue(), doc));
                }
                return (Serializable) map;
            }
        } else if (prop.getType().isListType()) {
            if (prop instanceof ListProperty) {
                ListProperty listProp = (ListProperty) prop;
                List<Serializable> lstProp = new ArrayList<Serializable>();
                lstProp.addAll(listProp.getChildren());
                return (Serializable) lstProp;
            } else if ((prop instanceof ArrayProperty) && (prop.getValue() != null)) {
                Object[] arrayProp = (Object[]) prop.getValue();
                List<Serializable> lstProp = new ArrayList<Serializable>();
                int length = arrayProp.length;
                for (int i = 0; i < length; i++) {
                    lstProp.add((Serializable) arrayProp[i]);
                }
                return (Serializable) lstProp;
            }
        }
        return "";
    }

    public static DocumentModel toDocumentModel(FlexDocumentModel fdoc)
            throws Exception {
        CoreSession session = CoreInstance.getInstance().getSession(
                fdoc.getSessionId());
        return toDocumentModel(fdoc, session);
    }

    public static DocumentModel toDocumentModel(FlexDocumentModel fdoc,
            CoreSession session) throws Exception {

        String refAsString = fdoc.getDocRef();
        DocumentModel doc = null;
        if (refAsString == null || "".equals(refAsString)) {
            String docType = fdoc.getType();
            String name = fdoc.getName();
            String docPath = fdoc.getPath();
            String parentPath = new Path(docPath).removeLastSegments(1).toString();
            doc = session.createDocumentModel(parentPath, name, docType);
            doc = session.createDocument(doc);
        } else {
            DocumentRef docRef = new IdRef(refAsString);
            doc = session.getDocument(docRef);
        }

        Map<String, Serializable> dirtyFields = fdoc.getDirtyFields();

        for (String path : dirtyFields.keySet()) {
            doc.setPropertyValue(path, dirtyFields.get(path));
        }

        return doc;
    }

}
