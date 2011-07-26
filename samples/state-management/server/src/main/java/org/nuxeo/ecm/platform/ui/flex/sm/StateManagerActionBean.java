package org.nuxeo.ecm.platform.ui.flex.sm;

import java.io.Serializable;

import javax.jws.WebMethod;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.platform.ui.flex.services.FlexNavigationContext;

@Scope(ScopeType.SESSION)
@Name("stateManagerActionBean")
public class StateManagerActionBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @In(create=true, required=false)
    protected CoreSession flexDocumentManager;

    @In(create=true, required=false)
    protected FlexNavigationContext flexNavigationContext;

    protected boolean createMode = false;

    protected static final String DOC_KEY ="sampleDocument";

    @WebMethod
    public String createDocument(String parentPath, String type, String name) throws ClientException {
        DocumentModel doc = flexDocumentManager.createDocumentModel(parentPath, name, type);
        flexNavigationContext.storeEditableDocument(DOC_KEY, doc);
        createMode=true;
        return "CREATED_OK";
    }

    @WebMethod
    public String editDocument(String docId) throws ClientException {
        DocumentModel doc = flexDocumentManager.getDocument(new IdRef(docId));
        flexNavigationContext.storeEditableDocument(DOC_KEY, doc);
        return "OK";
    }

    @WebMethod
    public void pushChanges(DocumentModel doc) {
        flexNavigationContext.storeEditableDocument(DOC_KEY, doc);
    }

    @WebMethod
    public DocumentModel getDocument() {
        return flexNavigationContext.getStoredEditableDocument(DOC_KEY);
    }

    @WebMethod
    public DocumentModel save() throws Exception {
        DocumentModel doc =  flexNavigationContext.getStoredEditableDocument(DOC_KEY);
        //flexDocumentManager.clear(DOC_KEY);
        if (createMode) {
            return flexDocumentManager.createDocument(doc);
        } else {
            return flexDocumentManager.saveDocument(doc);
        }
    }

}
