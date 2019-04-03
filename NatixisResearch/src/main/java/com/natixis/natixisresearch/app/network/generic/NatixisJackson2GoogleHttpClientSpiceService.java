package com.natixis.natixisresearch.app.network.generic;

import android.app.Application;

import com.octo.android.robospice.GoogleHttpClientSpiceService;
import com.octo.android.robospice.persistence.CacheManager;
import com.octo.android.robospice.persistence.exception.CacheCreationException;
import com.octo.android.robospice.persistence.googlehttpclient.json.Jackson2ObjectPersisterFactory;

/**
 * Created by Thibaud on 02/04/2017.
 */


/**
 * A {@link com.octo.android.robospice.GoogleHttpClientSpiceService} dedicated to json web services via
 * Jackson. Provides caching.
 * @author sni
 */
public class NatixisJackson2GoogleHttpClientSpiceService extends GoogleHttpClientSpiceService {
    @Override
    public CacheManager createCacheManager(Application application) throws CacheCreationException {
        CacheManager cacheManager = new CacheManager();
        cacheManager.addPersister(new NatixisJackson2ObjectPersisterFactory(application));
        return cacheManager;
    }

}
