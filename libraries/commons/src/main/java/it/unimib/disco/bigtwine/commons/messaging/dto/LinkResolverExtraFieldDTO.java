package it.unimib.disco.bigtwine.commons.messaging.dto;

import java.io.Serializable;

public class LinkResolverExtraFieldDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String valuePath;
    private String saveAs;
    private boolean isList;

    public LinkResolverExtraFieldDTO() {
    }

    public LinkResolverExtraFieldDTO(String valuePath, String saveAs) {
        this.valuePath = valuePath;
        this.saveAs = saveAs;
    }

    public LinkResolverExtraFieldDTO(String valuePath, String saveAs, boolean isList) {
        this(valuePath, saveAs);
        this.isList = isList;
    }

    public String getValuePath() {
        return valuePath;
    }

    public void setValuePath(String valuePath) {
        this.valuePath = valuePath;
    }

    public String getSaveAs() {
        return saveAs;
    }

    public void setSaveAs(String saveAs) {
        this.saveAs = saveAs;
    }

    public boolean isList() {
        return isList;
    }

    public void setList(boolean list) {
        isList = list;
    }
}
