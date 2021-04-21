package io.revx.core.model.creative;

import java.util.List;

public class CreativeHtmlMockupDTO {

    private CreativeDetails basicDetails;
    private List<CreativeHtmlFile> creativeHtmlFiles;

    public CreativeDetails getBasicDetails() {
        return basicDetails;
    }

    public void setBasicDetails(CreativeDetails basicDetails) {
        this.basicDetails = basicDetails;
    }

    public List<CreativeHtmlFile> getCreativeHtmlFiles() {
        return creativeHtmlFiles;
    }

    public void setCreativeHtmlFiles(List<CreativeHtmlFile> creativeHtmlFiles) {
        this.creativeHtmlFiles = creativeHtmlFiles;
    }

    @Override
    public String toString() {
        return "CreativeHtmlMockupDTO{" +
                "basicDetails=" + basicDetails +
                ", creativeHtmlFiles=" + creativeHtmlFiles +
                '}';
    }
}
