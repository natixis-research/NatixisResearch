package com.natixis.natixisresearch.app.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.natixis.natixisresearch.app.R;
import com.natixis.natixisresearch.app.activity.fragment.ParameterFragment;
import com.natixis.natixisresearch.app.activity.fragment.ParameterLanguageFragment;

import java.util.Locale;


public class ParameterActivity extends BaseActivity implements ParameterLanguageFragment.OnListFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parameter);
        setTitle("");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.connection, menu);
        return true;
    }*/
public void changeLanguage(Locale newLanguage){
    Configuration config = new Configuration(getResources().getConfiguration());
    if(!config.locale.getLanguage().equals(newLanguage.getLanguage())) {
        config.locale = newLanguage;
       // onConfigurationChanged(config);
        getResources().updateConfiguration(config,getResources().getDisplayMetrics());
        setResult(ParameterFragment.RESULT_CHANGE_LANGUE);
        finish();
    }

}



    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }
        return (super.onOptionsItemSelected(menuItem));
    }


    @Override
    public void onListLanguageInteraction(Locale newLanguage) {
        Log.d("test"," click new language: "+newLanguage.getDisplayLanguage() );
        changeLanguage(newLanguage);
        getSupportFragmentManager().popBackStack();
    }
}
