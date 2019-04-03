package com.natixis.natixisresearch.app.network.request.types;

/**
 * Created by Thibaud on 08/04/2017.
 */
public enum NodeTreeType {
    ECO("eco"),
    CREDIT("credit"),
    EQUITY("equity");

    /**
     * @param text
     */
    private NodeTreeType(final String text) {
        this.text = text;
    }

    private final String text;

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return text;
    }
}
