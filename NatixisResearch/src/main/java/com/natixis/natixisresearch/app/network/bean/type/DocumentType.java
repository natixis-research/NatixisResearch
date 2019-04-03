package com.natixis.natixisresearch.app.network.bean.type;

import java.util.HashMap;

/**
 * Created by Thibaud on 07/04/2017.
 */
public enum DocumentType {

    PDF(1),
    VIDEO(2),
    AUDIO(3),
    NEWS(4);

    private final int value;

    private DocumentType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }


    private static final HashMap<Integer, DocumentType> typesByValue = new HashMap<Integer, DocumentType>();

    static {
        for (DocumentType type : DocumentType.values()) {
            typesByValue.put(type.value, type);
        }
    }

    public static DocumentType forValue(int value) {
        return typesByValue.get(value);
    }
}
