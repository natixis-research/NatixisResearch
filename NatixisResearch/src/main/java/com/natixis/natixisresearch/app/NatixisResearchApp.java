package com.natixis.natixisresearch.app;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.natixis.natixisresearch.app.BuildConfig;
import com.natixis.natixisresearch.app.activity.BaseActivity;
import com.natixis.natixisresearch.app.cache.DocumentCache;
import com.natixis.natixisresearch.app.cache.FavoritesCache;
import com.natixis.natixisresearch.app.cache.ReadCache;
import com.natixis.natixisresearch.app.network.bean.ResearchUniverse;
import com.natixis.natixisresearch.app.network.bean.UniversesList;
import com.natixis.natixisresearch.app.network.bean.User;
import com.natixis.natixisresearch.app.network.generic.NatixisObjectMapper;
import com.natixis.natixisresearch.app.utils.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * Created by Thibaud on 08/04/2017. e5924182
 */
public class NatixisResearchApp extends Application {
    private static NatixisResearchApp instance = null;

    private final static String WS_BASE_URL = BuildConfig.WS_BASE_URL;
    private final static String BO_BASE_URL = BuildConfig.BO_BASE_URL;
    private final static String WS_IMAGE_BASE_URL = BuildConfig.WS_IMAGE_BASE_URL;
    private final static String LOGGED_USER_PREF = "user";
    private final static String FAVORITES_USER_PREF = "favorites_universes";
    private final static String REGISTRATION_ID_PREF = "registration_id";
    private final static String CGV_PREF = "cgv";
    private final static String PREF_DEVICE_IDENTIFIER = "deviceid";



    SharedPreferences appPreferences = null;

    User mLoggedUser = null;
    UniversesList mFavoritesUniverses = null;

    public static NatixisResearchApp getInstance() {
        return instance;
    }

    private ArrayList<ResearchUniverse> universes = new ArrayList<ResearchUniverse>();
    boolean mCGVAccepted = false;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        appPreferences = getSharedPreferences("NatixisResearch", Context.MODE_PRIVATE);
        mCGVAccepted = appPreferences.getBoolean(CGV_PREF, false);
        ImageLoaderConfiguration config = ImageLoaderConfiguration.createDefault(getApplicationContext());
        ImageLoader.getInstance().init(config);

        loadUserInfos();
        loadUserFavoritesUniverses();
    }

    private void loadUserFavoritesUniverses() {
        String jsonUniverses = appPreferences.getString(FAVORITES_USER_PREF, null);
        if (jsonUniverses != null) {

            try {
                //   mLoggedUser= parser.parse(  Person.class);
                NatixisObjectMapper mapper = new NatixisObjectMapper();
                mFavoritesUniverses = mapper.readValue(jsonUniverses, UniversesList.class);
                int i = 0;

            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
        if(mFavoritesUniverses ==null)
            mFavoritesUniverses = new UniversesList();


    }

public String getImageUrl(String imageName){
    return WS_IMAGE_BASE_URL+imageName;
}
    public String getRegistrationToken() {
        String token = appPreferences.getString(REGISTRATION_ID_PREF, null);
      return token;
    }

    public void setRegistrationToken(String token) {
        try {

            if (token != null) {
                SharedPreferences.Editor editor = appPreferences.edit();
                editor.putString(REGISTRATION_ID_PREF, token);
                editor.commit();
            }else{
                SharedPreferences.Editor editor = appPreferences.edit();
                editor.remove(REGISTRATION_ID_PREF);
                editor.commit();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public UniversesList getFavorites(){
        if(mFavoritesUniverses ==null){
            loadUserFavoritesUniverses();
        }
        return mFavoritesUniverses;
    }

    public void updateFavorites(UniversesList listUniverses) {

        try {

            NatixisObjectMapper mapper = new NatixisObjectMapper();

            String json = mapper.writeValueAsString(listUniverses);
            if (json != null) {
                SharedPreferences.Editor editor = appPreferences.edit();
                editor.putString(FAVORITES_USER_PREF, json);
                editor.commit();
                loadUserFavoritesUniverses();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private void loadUserInfos() {
        String jsonPerson = appPreferences.getString(LOGGED_USER_PREF, null);
        if (jsonPerson != null) {
            // AndroidJsonFactory factory = new AndroidJsonFactory();
            //  JsonParser parser= factory.createJsonParser(jsonPerson);
            try {
                //   mLoggedUser= parser.parse(  Person.class);
                NatixisObjectMapper mapper = new NatixisObjectMapper();
                mLoggedUser = mapper.readValue(jsonPerson, User.class);
                int i = 0;

            } catch (Exception ex) {
                ex.printStackTrace();
            }

        } else {
            mLoggedUser = null;
        }

        universes.clear();
        if (mLoggedUser != null) {
         /*   ArrayList<ResearchUniverse> subCatStock = new ArrayList<ResearchUniverse>();

            ArrayList<ResearchUniverse> subCatAction = new ArrayList<ResearchUniverse>();
            subCatAction.add(new ResearchUniverse(R.string.title_section_matieres, R.drawable.ic_contact, "recherche"));
            subCatAction.add(new ResearchUniverse(R.string.title_section_allocation, R.drawable.ic_contact, "recherche",subCatStock));
            subCatAction.add(new ResearchUniverse(R.string.title_section_taux, R.drawable.ic_contact, "recherche"));


            ArrayList<ResearchUniverse> subCatCredits = new ArrayList<ResearchUniverse>();
            subCatCredits.add(new ResearchUniverse(R.string.title_section_expertise, R.drawable.ic_contact, "recherche"));
            subCatCredits.add(new ResearchUniverse(R.string.title_section_actions, R.drawable.ic_contact, "recherche",subCatAction));
            subCatCredits.add(new ResearchUniverse(R.string.title_section_taux, R.drawable.ic_contact, "recherche"));


            subCatStock.add(new ResearchUniverse(R.string.title_section_credit, R.drawable.ic_contact, "recherche"));
            subCatStock.add(new ResearchUniverse(R.string.title_section_actions, R.drawable.ic_contact, "recherche"));
            subCatStock.add(new ResearchUniverse(R.string.title_section_taux, R.drawable.ic_contact, "recherche"));


            universes.add(new ResearchUniverse(R.string.title_section_credit, R.drawable.ic_contact, "credit",subCatStock));
            universes.add(new ResearchUniverse(R.string.title_section_economic, R.drawable.ic_contact, "eco", subCatCredits));

            universes.add(new ResearchUniverse(R.string.title_section_actions, R.drawable.ic_contact, "actions"));
            universes.add(new ResearchUniverse(R.string.title_section_expertise, R.drawable.ic_contact, "expertise"));
            universes.add(new ResearchUniverse(R.string.title_section_taux, R.drawable.ic_contact, "taux"));
            universes.add(new ResearchUniverse(R.string.title_section_matieres, R.drawable.ic_contact, "matieres",subCatStock));
            universes.add(new ResearchUniverse(R.string.title_section_allocation, R.drawable.ic_contact, "equity"));*/
        } else {
            //     universes.add(new ResearchUniverse(R.string.title_section_economic, R.drawable.ic_contact, "eco"));
        }

    }

    public void onLogout(BaseActivity context) {
        Toast.makeText(this, getString(R.string.you_are_disconnected), Toast.LENGTH_SHORT).show();
        SharedPreferences.Editor editor = appPreferences.edit();
        editor.remove(LOGGED_USER_PREF);
        editor.remove(FAVORITES_USER_PREF);
        editor.remove(REGISTRATION_ID_PREF);
        editor.commit();
        loadUserInfos();
        clearDownloadCache(context);
        clearFavoritesCache();
        clearReadCache();
    }

    public User getLoggedUser() {
        return mLoggedUser;
    }

    public boolean isUserLogged() {
        return getLoggedUser() != null;
    }

    public String getWebserviceBaseUrl() {
        return WS_BASE_URL;
    }

    public String getBackOfficeBaseUrl() {
        return BO_BASE_URL;
    }


    public void onAuthenticationSuccess(BaseActivity activity, User user) {
        if (user != null) {

            Toast.makeText(this, getString(R.string.you_are_connected), Toast.LENGTH_SHORT).show();
            activity.getSpiceManager().removeAllDataFromCache();
            try {
                //    JacksonFactory jsonFactory = new JacksonFactory();
                ///  StringWriter output = new StringWriter();
                //  JsonGenerator generator = jsonFactory.createJsonGenerator(output);

                NatixisObjectMapper mapper = new NatixisObjectMapper();

                String json = mapper.writeValueAsString(user);
                if (json != null) {
                    SharedPreferences.Editor editor = appPreferences.edit();
                    editor.putString(LOGGED_USER_PREF, json);
                    editor.commit();
                    loadUserInfos();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public String generateDeviceId() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String did = telephonyManager.getDeviceId();
        did += did + new SimpleDateFormat("yyMMddHHmmss").format(new Date());
        did = did.substring(did.length() - 33);
        SharedPreferences.Editor editor = appPreferences.edit();
        editor.putString(PREF_DEVICE_IDENTIFIER, did);
        editor.commit();

        return did;
    }

    public String getDeviceId() {
        return appPreferences.getString(PREF_DEVICE_IDENTIFIER, null);
    }


    public ArrayList<ResearchUniverse> getUniverses() {
        return universes;
    }


    public long getCacheSize() {
        return Utils.dirSize(getCacheDir());
    }


    public void clearDownloadCache(BaseActivity context) {

        if (context != null) {
            context.getSpiceManager().removeAllDataFromCache();
        }

        getDocumentCache().clearCache();
        Utils.clearDir(getCacheDir());
        File f = new File(getCacheDir() + "/robospice-cache/");
        if (!f.exists()) {
            f.mkdir();
        }

    }

    public void clearFavoritesCache() {
        getFavoritesCache().clearCache();
    }

    FavoritesCache favoritesCache = null;

    public FavoritesCache getFavoritesCache() {
        if (favoritesCache == null) {
            favoritesCache = new FavoritesCache(this, appPreferences);
        }
        return favoritesCache;
    }


    public void clearReadCache() {
        getReadCache().clearCache();
    }

    ReadCache readCache = null;

    public ReadCache getReadCache() {
        if (readCache == null) {
            readCache = new ReadCache(this, appPreferences);
        }
        return readCache;
    }


    DocumentCache documentCache = null;

    public DocumentCache getDocumentCache() {
        if (documentCache == null) {
            documentCache = new DocumentCache(this, appPreferences);
        }
        return documentCache;
    }

    public boolean isCGVAccepted() {
        return mCGVAccepted;
    }

    public void markCGVAccepted() {
        mCGVAccepted = true;
        SharedPreferences.Editor editor = appPreferences.edit();
        editor.putBoolean(CGV_PREF, mCGVAccepted);
        editor.commit();
    }


//Document Cache


}
