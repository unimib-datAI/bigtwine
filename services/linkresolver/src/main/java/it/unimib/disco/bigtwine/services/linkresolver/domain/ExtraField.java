package it.unimib.disco.bigtwine.services.linkresolver.domain;

public class ExtraField {
    private String valuePath;
    private String saveAs;
    private boolean isList;

    public ExtraField() {
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
