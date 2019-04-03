package com.natixis.natixisresearch.app.cache;

import android.content.SharedPreferences;

import com.natixis.natixisresearch.app.NatixisResearchApp;
import com.natixis.natixisresearch.app.network.bean.ResearchDocument;
import com.natixis.natixisresearch.app.network.bean.ResearchDocumentFavoriteList;
import com.natixis.natixisresearch.app.network.bean.ResearchDocumentReadList;
import com.natixis.natixisresearch.app.network.generic.NatixisObjectMapper;

import java.util.Collection;

/**
 * Created by Thibaud on 05/04/2017.
 */
public class ReadCache {
    private final static String PREF_READ_DOCS = "read";
    ResearchDocumentReadList readDocuments = null;
    NatixisResearchApp app;
    SharedPreferences preferences;

    public ReadCache(NatixisResearchApp baseApp, SharedPreferences sharedPref) {
        app = baseApp;
        preferences = sharedPref;

    }

    public void addDocumentToRead(ResearchDocument doc) {
        if (readDocuments == null) {
            loadReadDocs();
        }

        if (readDocuments != null) {
            readDocuments.put(doc.getDocumentUniqueId(), doc);
            saveFavorites();
        }
    }

    public boolean isRead(ResearchDocument doc) {
        if (readDocuments == null) {
            loadReadDocs();
        }

        if (readDocuments != null) {
            return readDocuments.containsKey(doc.getDocumentUniqueId());
        }
        return false;
    }

    private void loadReadDocs() {
        String favorites = preferences.getString(PREF_READ_DOCS, null);
        if (favorites != null) {
            try {
                NatixisObjectMapper mapper = new NatixisObjectMapper();
                readDocuments = mapper.readValue(favorites, ResearchDocumentReadList.class);

            } catch (Exception ex) {
                ex.printStackTrace();
                readDocuments = new ResearchDocumentReadList();
            }

        } else if (readDocuments == null) {
            readDocuments = new ResearchDocumentReadList();
        }

    }

    private void saveFavorites() {
        try {
            NatixisObjectMapper mapper = new NatixisObjectMapper();
            String json = mapper.writeValueAsString(readDocuments);
            if (json != null) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(PREF_READ_DOCS, json);
                editor.commit();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void removeDocumentFromFavorites(ResearchDocument doc) {
        if (readDocuments == null) {
            loadReadDocs();
        }

        if (readDocuments != null && readDocuments.containsKey(doc.getDocumentUniqueId())) {
            readDocuments.remove(doc.getDocumentUniqueId());
            saveFavorites();
        }
    }


    public void clearCache() {
        if (readDocuments == null) {
            loadReadDocs();
        }
        readDocuments.clear();
        saveFavorites();
    }

    public Collection<ResearchDocument> getDocuments() {
        if (readDocuments == null) {
            loadReadDocs();
        }
        return readDocuments.values();
    }
}
