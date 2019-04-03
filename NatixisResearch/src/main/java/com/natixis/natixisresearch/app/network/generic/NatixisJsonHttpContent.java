package com.natixis.natixisresearch.app.network.generic;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Thibaud on 01/04/2017.
 */
public class NatixisJsonHttpContent extends JsonHttpContent {


    /**
     * @param data        JSON key name/value data
     * @since 1.5
     */
    public NatixisJsonHttpContent(Object data) {
        super(new JacksonFactory(), data);

    }

    @Override
    public void writeTo(OutputStream out) throws IOException {
        //super.writeTo(out);
        ObjectMapper mapper = new ObjectMapper();

        mapper.writeValue(out, getData());
    }



}
