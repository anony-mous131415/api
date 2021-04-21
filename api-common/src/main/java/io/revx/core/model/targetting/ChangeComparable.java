package io.revx.core.model.targetting;

import java.io.Serializable;
import io.revx.core.exception.NotComparableException;

public interface ChangeComparable extends Serializable {

    public Difference compareTo(ChangeComparable o) throws NotComparableException;
}
