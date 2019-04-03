package com.natixis.natixisresearch.app.network.request.types;

/**
 * Created by Thibaud on 08/04/2017.
 */
public enum RequestLanguage {
    FR("fr"),
    EN("en");


    /**
     * @param text
     */
    private RequestLanguage(final String text) {
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
