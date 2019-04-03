package com.natixis.natixisresearch.app.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.natixis.natixisresearch.app.NatixisResearchApp;
import com.natixis.natixisresearch.app.R;
import com.natixis.natixisresearch.app.network.generic.NatixisJackson2GoogleHttpClientSpiceService;
import com.natixis.natixisresearch.app.network.request.types.RequestLanguage;
import com.octo.android.robospice.SpiceManager;

import java.util.Locale;

/**
 * Created by Thibaud on 06/04/2017.
 */
public class BaseActivity extends AppCompatActivity {

    private SpiceManager spiceManager = new SpiceManager(NatixisJackson2GoogleHttpClientSpiceService.class);

    int currentContentView =0;
    private RequestLanguage researchRequestLanguage;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        currentContentView=layoutResID;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
/*
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(
                this,  mDrawerLayout, mToolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerToggle.syncState();*/


    }

    @Override
    protected void onStart() {
        spiceManager.start(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        spiceManager.shouldStop();
        super.onStop();
    }

    public SpiceManager getSpiceManager() {
        return spiceManager;
    }

    public NatixisResearchApp getNatixisApp() {
        return (NatixisResearchApp) getApplication();
    }

/*
    protected void requestDocument(ResearchDocument document, boolean open) {
        Log.e("TEST", "test doc  : " + document.getFilename());

        DocumentRequestListener listener = new DocumentRequestListener(this,getSpiceManager(),document, open);

        if (getNatixisApp().getDocumentCache().isInCache(document)) {
            listener.onRequestSuccess(null);
        } else {
            DocumentRequest documentRequest = new DocumentRequest(document);
            getSpiceManager().execute(documentRequest, document.getFilename(), DurationInMillis.ALWAYS_RETURNED, listener);
        }

    }
*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()== R.id.action_language){
            Configuration config = new Configuration(getResources().getConfiguration());
            if(config.locale.getLanguage().equals(Locale.ENGLISH.getLanguage())) {
                config.locale = Locale.FRENCH;
            }else{
                config.locale = Locale.ENGLISH;
            }
            onConfigurationChanged(config);
            //this.recreate();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

       recreate();
    }


    public Locale getLocale(){
        return getResources().getConfiguration().locale;
    }

    public RequestLanguage getResearchRequestLanguage() {
        RequestLanguage lang;
        if(getLocale().getLanguage().equals(RequestLanguage.FR.toString())){
            lang = RequestLanguage.FR;
        }
        else{
            lang= RequestLanguage.EN;
        }
        return lang;
    }

    boolean drawerOpened=false;
    public void onDrawerOpened(){
        drawerOpened=true;

    }
    public void onDrawerClosed(){
        drawerOpened=false;
    }

    public boolean isDrawerOpened(){
        return drawerOpened;
    }
}
