package io.revx.api.mysql.entity.creative;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "CreativeTemplates")
public class CreativeTemplateEntity {

    @Id
    @Column(name = "ct_id")
    private long templateId;

    @Column(name = "ct_name")
    private String templateName;

    @Column(name = "ct_height", nullable = false)
    private int height;

    @Column(name = "ct_width", nullable = false)
    private int width;

    @Column(name = "ct_size", nullable = false)
    private String  size;

    @Column(name = "ct_html", nullable = false)
    private String htmlContent;

    @Column(name = "ct_variables", nullable = false)
    private String templateVariables;

    @Column(name = "ct_macros")
    private String macros;

    @Column(name = "ct_slots", nullable = false)
    private int slots;

    @Column(name = "ct_is_dynamic", nullable = false, columnDefinition = "tinyint(1) default 0")
    private boolean dynamic;

    @Column(name = "ct_has_overlay", nullable = false, columnDefinition = "tinyint(1) default 0")
    private boolean hasOverlay;

    @Column(name = "ct_is_active", nullable = false, columnDefinition = "tinyint(1) default 1")
    private boolean active;

    public long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(long templateId) {
        this.templateId = templateId;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
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

    public int getSlots() {
        return slots;
    }

    public void setSlots(int slots) {
        this.slots = slots;
    }

    public boolean isDynamic() {
        return dynamic;
    }

    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }

    public boolean isHasOverlay() {
        return hasOverlay;
    }

    public void setHasOverlay(boolean hasOverlay) {
        this.hasOverlay = hasOverlay;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "CreativeTemplateEntity{" +
                "templateId=" + templateId +
                ", templateName='" + templateName + '\'' +
                ", height=" + height +
                ", width=" + width +
                ", size=" + size +
                ", htmlContent='" + htmlContent + '\'' +
                ", templateVariables='" + templateVariables + '\'' +
                ", macros='" + macros + '\'' +
                ", slots=" + slots +
                ", dynamic=" + dynamic +
                ", hasOverlay=" + hasOverlay +
                ", active=" + active +
                '}';
    }
}
