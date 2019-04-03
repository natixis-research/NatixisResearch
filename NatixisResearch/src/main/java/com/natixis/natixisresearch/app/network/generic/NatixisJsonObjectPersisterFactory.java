package com.natixis.natixisresearch.app.network.generic;

/**
 * Created by Thibaud on 02/04/2017.
 */

import android.app.Application;


import com.fasterxml.jackson.core.JsonFactory;
import com.octo.android.robospice.persistence.exception.CacheCreationException;
import com.octo.android.robospice.persistence.file.InFileObjectPersisterFactory;

import java.io.File;
import java.util.List;



import java.io.File;
import java.util.List;



public abstract class NatixisJsonObjectPersisterFactory extends InFileObjectPersisterFactory {


    // ----------------------------------
    // CONSTRUCTOR
    // ----------------------------------
    public NatixisJsonObjectPersisterFactory(Application application) throws CacheCreationException {
        this(application, null, null);
    }

    public NatixisJsonObjectPersisterFactory(Application application, File cacheFolder)
            throws CacheCreationException {
        this(application,null, cacheFolder);
    }

    public NatixisJsonObjectPersisterFactory(Application application,
                                      List<Class<?>> listHandledClasses) throws CacheCreationException {
        this(application, listHandledClasses, null);
    }

    public NatixisJsonObjectPersisterFactory(Application application,
                                      List<Class<?>> listHandledClasses, File cacheFolder) throws CacheCreationException {
        super(application, listHandledClasses, cacheFolder);
    }

    // ----------------------------------
    // API
    // ----------------------------------

    @Override
    public <DATA> NatixisJsonObjectPersister<DATA> createInFileObjectPersister(Class<DATA> clazz, File cacheFolder)
            throws CacheCreationException {
        return new NatixisJsonObjectPersister<DATA>(getApplication(), clazz, cacheFolder);
    }

}
