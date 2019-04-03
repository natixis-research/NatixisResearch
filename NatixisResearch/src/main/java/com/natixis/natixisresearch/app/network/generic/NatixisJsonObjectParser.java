package com.natixis.natixisresearch.app.network.generic;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.api.client.util.ObjectParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

/**
 * Created by Thibaud on 01/04/2017.
 */
public class NatixisJsonObjectParser implements ObjectParser {
    NatixisObjectMapper mapper = new NatixisObjectMapper();

    /**
     * Parses the given input stream into a new instance of the the given data class of key/value
     * pairs and closes the input stream.
     *
     * @param in        input stream which contains the data to parse
     * @param charset   charset which should be used to decode the input stream or {@code null} if
     *                  unknown
     * @param dataClass class into which the data is parsed
     */
    @Override
    public <T> T parseAndClose(InputStream in, Charset charset, Class<T> dataClass) throws IOException,JsonMappingException {

            return mapper.readValue(in, dataClass);


    }

    /**
     * Parses the given input stream into a new instance of the the given data type of key/value pairs
     * and closes the input stream.
     *
     * @param in       input stream which contains the data to parse
     * @param charset  charset which should be used to decode the input stream or {@code null} if
     *                 unknown
     * @param dataType type into which the data is parsed
     */
    @Override
    public Object parseAndClose(InputStream in, Charset charset, Type dataType) throws IOException {

        return mapper.readValue(in, dataType.getClass());
    }

    /**
     * Parses the given reader into a new instance of the the given data class of key/value pairs and
     * closes the reader.
     *
     * @param reader    reader which contains the text data to parse
     * @param dataClass class into which the data is parsed
     */
    @Override
    public <T> T parseAndClose(Reader reader, Class<T> dataClass) throws IOException {
        return mapper.readValue(reader, dataClass);
    }

    /**
     * Parses the given reader into a new instance of the the given data type of key/value pairs and
     * closes the reader.
     *
     * @param reader   reader which contains the text data to parse
     * @param dataType type into which the data is parsed
     */
    @Override
    public Object parseAndClose(Reader reader, Type dataType) throws IOException {
        return mapper.readValue(reader, dataType.getClass());
    }
}
