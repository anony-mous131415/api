package io.revx.core.model.creative;

public class CreativeHtmlFile {

    private String name;
    private String contentType;
    private CreativeType type;
    private boolean isNative;
    private boolean isDco;
    private Integer noOfSlots;
    private String macroList;
    private Integer height;
    private Integer width;
    private String errorMsg;
    private String htmlContent;
    private String dynamicItemList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public CreativeType getType() {
        return type;
    }

    public void setType(CreativeType type) {
        this.type = type;
    }

    public boolean isNative() {
        return isNative;
    }

    public void setNative(boolean aNative) {
        isNative = aNative;
    }

    public boolean isDco() {
        return isDco;
    }

    public void setDco(boolean dco) {
        isDco = dco;
    }

    public Integer getNoOfSlots() {
        return noOfSlots;
    }

    public void setNoOfSlots(Integer noOfSlots) {
        this.noOfSlots = noOfSlots;
    }

    public String getMacroList() {
        return macroList;
    }

    public void setMacroList(String macroList) {
        this.macroList = macroList;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getHtmlContent() {
        return htmlContent;
    }

    public void setHtmlContent(String htmlContent) {
        this.htmlContent = htmlContent;
    }

    public String getDynamicItemList() {
        return dynamicItemList;
    }

    public void setDynamicItemList(String dynamicItemList) {
        this.dynamicItemList = dynamicItemList;
    }

    @Override
    public String toString() {
        return "CreativeHtmlFile{" +
                "name='" + name + '\'' +
                ", contentType='" + contentType + '\'' +
                ", type=" + type +
                ", isNative=" + isNative +
                ", isDco=" + isDco +
                ", noOfSlots=" + noOfSlots +
                ", macroList='" + macroList + '\'' +
                ", height=" + height +
                ", width=" + width +
                ", errorMsg='" + errorMsg + '\'' +
                ", htmlContent='" + htmlContent + '\'' +
                ", dynamicItemList='" + dynamicItemList + '\'' +
                '}';
    }
}
