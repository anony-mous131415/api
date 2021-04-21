/**
 * @author abhinav dubey
 */

package io.revx.core.model.creative;

public enum GeneratedUrlType {

    CLICK, S2S, SKAD;

    public static GeneratedUrlType getGeneratedUrlType(String str)
    {
        if(str.equals(GeneratedUrlType.CLICK.toString()))
            return GeneratedUrlType.CLICK;

        if(str.equals(GeneratedUrlType.S2S.toString()))
            return GeneratedUrlType.S2S;

        return null;

    }

}
