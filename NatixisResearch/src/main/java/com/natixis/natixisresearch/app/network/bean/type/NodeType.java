package com.natixis.natixisresearch.app.network.bean.type;

import java.util.HashMap;

/**
 * Created by Thibaud on 07/04/2017.
 */
public enum NodeType {

    NODE(1),
    LEAF(2);

    private final int value;

    private NodeType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }



    private static final HashMap<Integer, NodeType> typesByValue = new HashMap<Integer, NodeType>();

    static {
        for (NodeType type : NodeType.values()) {
            typesByValue.put(type.value, type);
        }
    }

    public static NodeType forValue(int value) {
        return typesByValue.get(value);
    }
}
