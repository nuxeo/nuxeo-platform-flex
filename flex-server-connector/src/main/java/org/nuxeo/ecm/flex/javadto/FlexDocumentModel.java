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

package org.nuxeo.ecm.flex.javadto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;

/**
 * {@link DocumentModel} reprosentation in the Flex world
 *
 * Basically the {@link FlexDocumentModel} is a deconected DTO that represents a
 * {@link DocumentModel}
 *
 * @author Tiry (tdelprat@nuxeo.com)
 *
 */
public class FlexDocumentModel {

    private String docRef;

    private String name;

    private String path;

    private String lifeCycleState;

    private String type;

    private String icon;

    private String iconExpanded;

    private Boolean isFolder = false;

    private String sessionId;

    private Map<String, Map<String, Serializable>> data = new HashMap<String, Map<String, Serializable>>();

    private Map<String, Serializable> dirtyFields = new HashMap<String, Serializable>();

    public FlexDocumentModel() {
        docRef = "docRef";
        name = "docName";
        path = "/default/folder" + name;
        lifeCycleState = "work";
        type = null;
        icon = null;
        iconExpanded = null;
    }

    public FlexDocumentModel(String sessionId, DocumentRef ref, String name,
            String path, String lcState, String type, String icon,
            String iconExpanded) {
        docRef = ref.toString();
        this.name = name;
        this.path = path;
        lifeCycleState = lcState;
        this.type = type;
        this.icon = icon;
        this.iconExpanded = iconExpanded;
        this.sessionId = sessionId;
    }

    public void setIsFolder(Boolean isFolder) {
        this.isFolder = isFolder;
    }

    public Map<String, Serializable> getDirtyFields() {
        return dirtyFields;
    }

    public void feed(String schemaName, Map<String, Serializable> map) {
        data.put(schemaName, map);
    }

    public void setProperty(String schemaName, String fieldName,
            Serializable value) {
        data.get(schemaName).put(fieldName, value);
    }

    public Serializable getProperty(String schemaName, String fieldName) {
        return data.get(schemaName).get(fieldName);
    }

    public Map<String, Map<String, Serializable>> getData() {
        return data;
    }

    public void setData(Map<String, Map<String, Serializable>> data) {
        this.data = data;
    }

    public Boolean getIsFolder() {
        return isFolder;
    }

    public void setDirtyFields(Map<String, Serializable> dirtyFields) {
        this.dirtyFields = dirtyFields;
    }

    public String getDocRef() {
        return docRef;
    }

    public void setDocRef(String docRef) {
        this.docRef = docRef;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getLifeCycleState() {
        return lifeCycleState;
    }

    public void setLifeCycleState(String lifeCycleState) {
        this.lifeCycleState = lifeCycleState;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getIconExpanded() {
        return iconExpanded;
    }

    public void setIconExpanded(String iconExpanded) {
        this.iconExpanded = iconExpanded;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

}
