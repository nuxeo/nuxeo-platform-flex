package org.nuxeo.ecm.platform.ui.granite.config;

import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XObject;

@XObject("runtime")
public class RuntimeComponent {

    @XNode("@id")
    private String id;
    
    @XNode("@class")
    private String className;
    
    @XNode("@destinationId")
    private String destinationId;    

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(String destinationId) {
        this.destinationId = destinationId;
    }
}
