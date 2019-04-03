package com.natixis.natixisresearch.app.cache;

import android.content.SharedPreferences;

import com.natixis.natixisresearch.app.NatixisResearchApp;
import com.natixis.natixisresearch.app.network.bean.ResearchDocument;
import com.natixis.natixisresearch.app.network.bean.ResearchDocumentCacheList;
import com.natixis.natixisresearch.app.network.generic.NatixisObjectMapper;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Thibaud on 05/04/2017.
 */
public class DocumentCache {


    private final static String PREF_CACHE_DOCS = "cache";

    ResearchDocumentCacheList cachedDocuments = null;
    NatixisResearchApp app;
    SharedPreferences preferences;

    public DocumentCache(NatixisResearchApp baseApp, SharedPreferences sharedPref) {
        app = baseApp;
        preferences = sharedPref;

    }


    public void addDocumentToCache(ResearchDocument doc) {
        if (cachedDocuments == null) {
            loadDocumentCache();
        }

        if (cachedDocuments != null) {
            cachedDocuments.put(doc.getDocumentUniqueId(), doc);
            saveDocumentCache();
        }
    }

    public boolean isInCache(ResearchDocument doc) {
        if (cachedDocuments == null) {
            loadDocumentCache();
        }

        if (cachedDocuments != null) {
            if( cachedDocuments.containsKey(doc.getDocumentUniqueId())){
                ResearchDocument cachedDoc = cachedDocuments.get(doc.getDocumentUniqueId());
                if((cachedDoc.getUpdateDateStr()==null&&doc.getUpdateDateStr()==null)
                    ||  (cachedDoc.getUpdateDateStr()!=null&&doc.getUpdateDateStr()!=null && cachedDoc.getUpdateDateStr().equals(doc.getUpdateDateStr()))) {
                    File f = new File(doc.getFilepath());
                    if (f.exists()) {
                        return true;
                    } else {
                        removeDocumentFromCache(doc);
                        return false;
                    }
                }
                else{
                    return false;
                }
            }

        }
        return false;
    }

    private void loadDocumentCache() {
        String cache = preferences.getString(PREF_CACHE_DOCS, null);
        if (cache != null) {
            try {
                NatixisObjectMapper mapper = new NatixisObjectMapper();
                cachedDocuments = mapper.readValue(cache, ResearchDocumentCacheList.class);

            } catch (Exception ex) {
                ex.printStackTrace();
                cachedDocuments = new ResearchDocumentCacheList();
            }

        } else if (cachedDocuments == null) {
            cachedDocuments = new ResearchDocumentCacheList();
        }

    }

    private void saveDocumentCache() {
        try {
            NatixisObjectMapper mapper = new NatixisObjectMapper();
            String json = mapper.writeValueAsString(cachedDocuments);
            if (json != null) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(PREF_CACHE_DOCS, json);
                editor.commit();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void removeDocumentFromCache(ResearchDocument doc) {
        if (cachedDocuments == null) {
            loadDocumentCache();
        }

        if (cachedDocuments != null && cachedDocuments.containsKey(doc.getDocumentUniqueId())) {
            cachedDocuments.remove(doc.getDocumentUniqueId());
            File f = new File(doc.getFilepath());
            if(f.exists()){
                f.delete();
            }
            saveDocumentCache();
        }
    }

    public void clearCache() {
        if (cachedDocuments == null) {
            loadDocumentCache();
        }
        cachedDocuments.clear();
        saveDocumentCache();
    }

    public Collection<ResearchDocument> getDocuments() {
        if (cachedDocuments == null) {
            loadDocumentCache();
        }
        return cachedDocuments.values();
    }
}
