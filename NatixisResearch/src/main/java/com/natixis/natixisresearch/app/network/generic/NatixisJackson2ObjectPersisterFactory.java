package com.natixis.natixisresearch.app.network.generic;

import android.app.Application;


import com.fasterxml.jackson.core.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.octo.android.robospice.persistence.exception.CacheCreationException;

import java.io.File;
import java.util.List;

/**
 * Created by Thibaud on 02/04/2017.
 */

/**
 * Allows to serialize objects using the google http java client jackson 2
 * module.
 * @author sni
 */
public class NatixisJackson2ObjectPersisterFactory extends NatixisJsonObjectPersisterFactory {

    public NatixisJackson2ObjectPersisterFactory(Application application, File cacheFolder) throws CacheCreationException {
        super(application,  cacheFolder);
    }

    public NatixisJackson2ObjectPersisterFactory(Application application, List<Class<?>> listHandledClasses, File cacheFolder)
            throws CacheCreationException {
        super(application,  listHandledClasses, cacheFolder);
    }

    public NatixisJackson2ObjectPersisterFactory(Application application, List<Class<?>> listHandledClasses)
            throws CacheCreationException {
        super(application,  listHandledClasses);
    }

    public NatixisJackson2ObjectPersisterFactory(Application application) throws CacheCreationException {
        super(application);
    }

}
