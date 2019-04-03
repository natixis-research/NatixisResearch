package com.natixis.natixisresearch.app.network.generic;

import android.app.Application;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.octo.android.robospice.persistence.exception.CacheCreationException;
import com.octo.android.robospice.persistence.exception.CacheLoadingException;
import com.octo.android.robospice.persistence.exception.CacheSavingException;
import com.octo.android.robospice.persistence.file.InFileObjectPersister;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import roboguice.util.temp.Ln;

/**
 * Created by Thibaud on 02/04/2017.
 */


public final class NatixisJsonObjectPersister<T> extends InFileObjectPersister<T> {

    // ============================================================================================
    // ATTRIBUTES
    // ============================================================================================

   // private final JsonFactory jsonFactory;
    private  NatixisObjectMapper mapper;
    // ============================================================================================
    // CONSTRUCTOR
    // ============================================================================================

    public NatixisJsonObjectPersister(Application application,  Class<T> clazz, File cacheFolder)
            throws CacheCreationException {
        super(application, clazz, cacheFolder);

        createMapper();
    }

    public NatixisJsonObjectPersister(Application application, Class<T> clazz)
            throws CacheCreationException {
        super(application, clazz);
      createMapper();
    }

    private void createMapper(){
        mapper = new NatixisObjectMapper();

    }
    // ============================================================================================
    // METHODS
    // ============================================================================================

    @Override
    protected T readCacheDataFromFile(File file) throws CacheLoadingException {
        try {
           // JsonParser jsonParser = jsonFactory.createParser(new FileReader(file));

            T result = mapper.readValue(file,getHandledClass());
            return result;
        } catch (FileNotFoundException e) {
            // Should not occur (we test before if file exists)
            // Do not throw, file is not cached
            Ln.w("file " + file.getAbsolutePath() + " does not exists", e);
            return null;
        } catch (Exception e) {
            throw new CacheLoadingException(e);
        }
    }

    @Override
    public T saveDataToCacheAndReturnData(final T data, final Object cacheKey) throws CacheSavingException {

        try {
            if (isAsyncSaveEnabled()) {
                Thread t = new Thread() {
                    @Override
                    public void run() {
                        try {
                            saveData(data, cacheKey);
                        } catch (IOException e) {
                            Ln.e(e, "An error occured on saving request " + cacheKey + " data asynchronously");
                        } catch (CacheSavingException e) {
                            Ln.e(e, "An error occured on saving request " + cacheKey + " data asynchronously");
                        }
                    }

                    ;
                };
                t.start();
            } else {
                saveData(data, cacheKey);
            }
        } catch (CacheSavingException e) {
            throw e;
        } catch (Exception e) {
            throw new CacheSavingException(e);
        }
        return data;
    }

    private void saveData(T data, Object cacheKey) throws IOException, CacheSavingException {
        // transform the content in json to store it in the cache
        //JsonGenerator jsonGenerator = jsonFactory.createGenerator(new FileWriter(getCacheFile(cacheKey)));
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(new FileWriter(getCacheFile(cacheKey)),data);
      /*  jsonGenerator.w(data);
        jsonGenerator.flush();
        jsonGenerator.close();*/
    }

}


