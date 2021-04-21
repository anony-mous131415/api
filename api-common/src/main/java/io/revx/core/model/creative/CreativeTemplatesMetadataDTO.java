package io.revx.core.model.creative;

import java.util.List;

public class CreativeTemplatesMetadataDTO {

    private List<String> templateSizes;
    private List<Integer> slots;

    public List<String> getTemplateSizes() {
        return templateSizes;
    }

    public void setTemplateSizes(List<String> templateSizes) {
        this.templateSizes = templateSizes;
    }

    public List<Integer> getSlots() {
        return slots;
    }

    public void setSlots(List<Integer> slots) {
        this.slots = slots;
    }

    @Override
    public String toString() {
        return "CreativeTemplatesMetadataDTO{" +
                "templateSizes=" + templateSizes +
                ", slots=" + slots +
                '}';
    }
}
