package io.revx.core.model.creative;

public class CreativeTemplateDTO {

    private Long templateId;
    private String templateName;
    private Integer height;
    private Integer width;
    private String size;
    private String htmlContent;
    private String templateVariables;
    private String macros;
    private Integer slots;
    private Boolean isDynamic;
    private Boolean hasOverlay;
    private Boolean isActive;
    private String dynamicItemList;

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
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

    public String getHtmlContent() {
        return htmlContent;
    }

    public void setHtmlContent(String htmlContent) {
        this.htmlContent = htmlContent;
    }

    public String getTemplateVariables() {
        return templateVariables;
    }

    public void setTemplateVariables(String templateVariables) {
        this.templateVariables = templateVariables;
    }

    public String getMacros() {
        return macros;
    }

    public void setMacros(String macros) {
        this.macros = macros;
    }

    public Integer getSlots() {
        return slots;
    }

    public void setSlots(Integer slots) {
        this.slots = slots;
    }

    public Boolean getIsDynamic() {
        return isDynamic;
    }

    public void setIsDynamic(Boolean isDynamic) {
        this.isDynamic = isDynamic;
    }

    public Boolean getHasOverlay() {
        return hasOverlay;
    }

    public void setHasOverlay(Boolean hasOverlay) {
        this.hasOverlay = hasOverlay;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getDynamicItemList() {
        return dynamicItemList;
    }

    public void setDynamicItemList(String dynamicItemList) {
        this.dynamicItemList = dynamicItemList;
    }

    @Override
    public String toString() {
        return "CreativeTemplateDTO{" +
                "templateId=" + templateId +
                ", templateName='" + templateName + '\'' +
                ", height=" + height +
                ", width=" + width +
                ", size='" + size + '\'' +
                ", htmlContent='" + htmlContent + '\'' +
                ", templateVariables='" + templateVariables + '\'' +
                ", macros='" + macros + '\'' +
                ", slots=" + slots +
                ", isDynamic=" + isDynamic +
                ", hasOverlay=" + hasOverlay +
                ", isActive=" + isActive +
                ", dynamicItemList='" + dynamicItemList + '\'' +
                '}';
    }
}
