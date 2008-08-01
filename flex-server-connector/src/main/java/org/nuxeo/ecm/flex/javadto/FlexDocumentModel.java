package org.nuxeo.ecm.flex.javadto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.nuxeo.ecm.core.api.DocumentRef;

    public class FlexDocumentModel {


    private String docRef;
    private String name;
    private String path;
    private String lifeCycleState;
    private String type;
    private Boolean isFolder=false;


    private transient Map<String, Map<String,Serializable>> data = new HashMap<String, Map<String,Serializable>>();
    private Map<String,Serializable> dirtyFields =  new HashMap<String, Serializable>();

    public FlexDocumentModel()
    {
        docRef="docRef";
        name="docName";
        path="/default/folder"+name;
        lifeCycleState="work";
        type=null;
    }

    public FlexDocumentModel(DocumentRef ref, String name, String path, String lcState,String type)
    {
        docRef=ref.toString();
        this.name=name;
        this.path=path;
        lifeCycleState=lcState;
        this.type=type;
    }



    public void setIsFolder(Boolean isFolder)
    {
        this.isFolder=isFolder;
    }

    public Map<String,Serializable> getDirtyFields()
    {
        return dirtyFields;
    }

    public void feed(String schemaName, Map<String,Serializable> map)
    {
        data.put(schemaName, map);
    }

    public void setProperty(String schemaName, String fieldName, Serializable value)
    {
        data.get(schemaName).put(fieldName, value);
    }

    public Serializable getProperty(String schemaName, String fieldName)
    {
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

}
