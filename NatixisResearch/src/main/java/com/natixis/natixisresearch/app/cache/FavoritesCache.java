package com.natixis.natixisresearch.app.cache;

import android.content.SharedPreferences;

import com.natixis.natixisresearch.app.NatixisResearchApp;
import com.natixis.natixisresearch.app.network.bean.ResearchDocument;
import com.natixis.natixisresearch.app.network.bean.ResearchDocumentFavoriteList;
import com.natixis.natixisresearch.app.network.generic.NatixisObjectMapper;

import java.util.Collection;

/**
 * Created by Thibaud on 05/04/2017.
 */
public class FavoritesCache {
    private final static String PREF_FAVORITE_DOCS = "favorites_docs";
    ResearchDocumentFavoriteList favoritesDocuments = null;
    NatixisResearchApp app;
    SharedPreferences preferences;

    public FavoritesCache(NatixisResearchApp baseApp, SharedPreferences sharedPref) {
        app = baseApp;
        preferences = sharedPref;

    }

    public void addDocumentToFavorites(ResearchDocument doc) {
        if (favoritesDocuments == null) {
            loadFavorites();
        }

        if (favoritesDocuments != null) {
            favoritesDocuments.put(doc.getDocumentUniqueId(), doc);
            saveFavorites();
        }
    }

    public boolean isInFavorites(ResearchDocument doc) {
        if (favoritesDocuments == null) {
            loadFavorites();
        }

        if (favoritesDocuments != null) {
            return favoritesDocuments.containsKey(doc.getDocumentUniqueId());
        }
        return false;
    }
    public int size(){
        if (favoritesDocuments == null) {
            loadFavorites();
        }
        return favoritesDocuments.size();
    }
    private void loadFavorites() {
        String favorites = preferences.getString(PREF_FAVORITE_DOCS, null);
        if (favorites != null) {
            try {
                NatixisObjectMapper mapper = new NatixisObjectMapper();
                favoritesDocuments = mapper.readValue(favorites, ResearchDocumentFavoriteList.class);

            } catch (Exception ex) {
                ex.printStackTrace();
                favoritesDocuments = new ResearchDocumentFavoriteList();
            }

        } else if (favoritesDocuments == null) {
            favoritesDocuments = new ResearchDocumentFavoriteList();
        }

    }

    private void saveFavorites() {
        try {
            NatixisObjectMapper mapper = new NatixisObjectMapper();
            String json = mapper.writeValueAsString(favoritesDocuments);
            if (json != null) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(PREF_FAVORITE_DOCS, json);
                editor.commit();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void removeDocumentFromFavorites(ResearchDocument doc) {
        if (favoritesDocuments == null) {
            loadFavorites();
        }

        if (favoritesDocuments != null && favoritesDocuments.containsKey(doc.getDocumentUniqueId())) {
            favoritesDocuments.remove(doc.getDocumentUniqueId());
            saveFavorites();
        }
    }


    public void clearCache() {
        if (favoritesDocuments == null) {
            loadFavorites();
        }
        favoritesDocuments.clear();
        saveFavorites();
    }

    public Collection<ResearchDocument> getDocuments() {
        if (favoritesDocuments == null) {
            loadFavorites();
        }
        return favoritesDocuments.values();
    }
}
