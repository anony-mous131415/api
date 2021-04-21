package io.revx.core.enums;

public enum MacroType {

    feed(1), pixel(2), custom(3), hybrid(4);

    private int value;

    private MacroType(int value) {
        this.value = value;
    }

    public static MacroType get(int value) {
        switch (value) {
            case 1:
                return MacroType.feed;
            case 2:
                return MacroType.pixel;
            case 3:
                return MacroType.custom;
            case 4:
                return MacroType.hybrid;
            default:
                return null;
        }
    }

    public int getValue() {
        return value;
    }
}