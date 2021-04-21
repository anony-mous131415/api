package io.revx.core.model.requests;

import io.revx.core.model.BaseModel;

import java.util.List;

public class SkadTargetPrivileges extends DictionaryResponse{

    private boolean isAllowed;

    public SkadTargetPrivileges(long totalNoOfRecords, List<BaseModel> data) {
        super(totalNoOfRecords, data);
    }

    public boolean isAllowed() {
        return isAllowed;
    }

    public void setAllowed(boolean allowed) {
        isAllowed = allowed;
    }

    @Override
    public String toString() {
        return "SkadTargetPrivileges{" +
                "totalNoOfRecords=" + totalNoOfRecords +
                ", data=" + data +
                ", isAllowed=" + isAllowed +
                '}';
    }
}
