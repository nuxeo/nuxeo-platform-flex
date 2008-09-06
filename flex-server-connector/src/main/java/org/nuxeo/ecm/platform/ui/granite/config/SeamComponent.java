package org.nuxeo.ecm.platform.ui.granite.config;

import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XObject;

@XObject("seam")
public class SeamComponent {

    @XNode("@id")
    private String id;
    
    @XNode("@source")
    private String source;
    
    @XNode("@destinationId")
    private String destinationId;    

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(String destinationId) {
        this.destinationId = destinationId;
    }
}