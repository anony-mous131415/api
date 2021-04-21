package io.revx.core.model;

import java.util.Objects;

public class CurrencyModel extends BaseModel{

    private static final long serialVersionUID = 1L;

    public CurrencyModel(){}

    public CurrencyModel(long id, String name, String currencyCode) {
        super(id,name);
        this.currencyCode = currencyCode;
    }

    private String currencyCode;

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), currencyCode);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return "CurrencyCode{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", currencyCode='" + currencyCode + '\'' +
                '}';
    }
}
