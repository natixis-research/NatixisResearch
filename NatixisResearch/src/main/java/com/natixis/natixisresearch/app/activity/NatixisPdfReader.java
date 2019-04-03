package com.natixis.natixisresearch.app.activity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.artifex.mupdfdemo.MuPDFActivity;
import com.natixis.natixisresearch.app.NatixisResearchApp;
import com.natixis.natixisresearch.app.R;
import com.natixis.natixisresearch.app.network.bean.ResearchDocument;
import com.natixis.natixisresearch.app.network.generic.NatixisObjectMapper;
import com.natixis.natixisresearch.app.utils.Utils;

/**
 * Created by Thibaud on 06/04/2017.
 */
public class NatixisPdfReader extends MuPDFActivity {
    MenuItem favoritesMenuItem;

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }
        return (super.onOptionsItemSelected(menuItem));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pdf_reader, menu);
        favoritesMenuItem = menu.findItem(R.id.action_favorites);
        refreshFavoriteIcon();

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (item.getItemId() == R.id.action_favorites) {
            onClickBtFavorite();
        } else if (item.getItemId() == R.id.action_print) {
            printDoc();
        } else if (item.getItemId() == R.id.action_mail) {
            Utils.sendDocument(this, document);
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public void onBackPressed() {
        if (core != null && core.hasChanges()) {
            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (which == AlertDialog.BUTTON_POSITIVE)
                        core.save();

                    finish();
                }
            };
            AlertDialog alert = mAlertBuilder.create();


            alert.setMessage(getString(com.artifex.mupdfdemo.R.string.document_has_changes_save_them_));
            alert.setButton(AlertDialog.BUTTON_POSITIVE, getString(com.artifex.mupdfdemo.R.string.yes), listener);
            alert.setButton(AlertDialog.BUTTON_NEGATIVE, getString(com.artifex.mupdfdemo.R.string.no), listener);
            alert.show();
        } else {
            finish();
        }
    }

    public final static String JSON_DOCUMENT = "json";
    public final static String THEME_COLOR = "theme_color";
    ResearchDocument document = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mToolbarEnabled = false;
        setTitle("");

        Intent myIntent = getIntent();


        ActionBar actionBar = getActionBar();
        if(actionBar!=null) {
            actionBar.setHomeButtonEnabled(true);

            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);

            // actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.line_theme)));

            actionBar.setIcon(null);
            actionBar.setDisplayUseLogoEnabled(false);
        }
        setTitle(getString(R.string.done));
        int actionBarTitleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
        if (actionBarTitleId > 0) {
            TextView title = (TextView) findViewById(actionBarTitleId);
            if (title != null) {
                title.setTextColor(Color.WHITE);
            }
        }


        String researchDocument = myIntent.getStringExtra(JSON_DOCUMENT);
        if (researchDocument != null) {
            NatixisObjectMapper mapper = new NatixisObjectMapper();
            try {
                document = mapper.readValue(researchDocument, ResearchDocument.class);
                if (document != null) {
                    getApp().getReadCache().addDocumentToRead(document);
                }

               /* if(actionBar!=null && NatixisResearchApp.getInstance().getFavoritesCache().isInFavorites(document)) {
                    actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.natixis_purple)));
                }*/

                }catch(Exception e){
                    e.printStackTrace();
                    setResult(RESULT_CANCELED);
                    finish();
                }
            }
        }


    private NatixisResearchApp getApp() {
        return (NatixisResearchApp) getApplication();
    }

    ImageButton mFavoriteButton;

    @Override
    public void createUI(Bundle savedInstanceState) {
        super.createUI(savedInstanceState);
    }

    public void onClickBtFavorite() {
        if (!NatixisResearchApp.getInstance().getFavoritesCache().isInFavorites(document)) {
            NatixisResearchApp.getInstance().getFavoritesCache().addDocumentToFavorites(document);
            Toast.makeText(this, getString(R.string.doc_added_to_favorites), Toast.LENGTH_SHORT).show();
        } else {
            NatixisResearchApp.getInstance().getFavoritesCache().removeDocumentFromFavorites(document);
            Toast.makeText(this, getString(R.string.doc_removed_from_favorites), Toast.LENGTH_SHORT).show();
        }

        refreshFavoriteIcon();
    }

    public void OnShareButtonClick(View v) {
        Utils.sendDocument(this, document);
    }


    public void refreshFavoriteIcon() {
        if (favoritesMenuItem != null && document != null) {
            if (!NatixisResearchApp.getInstance().getFavoritesCache().isInFavorites(document)) {
                favoritesMenuItem.setIcon(getResources().getDrawable(R.drawable.reader_favorite_off));
            } else {
                favoritesMenuItem.setIcon(getResources().getDrawable(R.drawable.reader_favorite_on));
            }
        }

    }
}


