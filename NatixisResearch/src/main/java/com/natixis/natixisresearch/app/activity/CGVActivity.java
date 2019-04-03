package com.natixis.natixisresearch.app.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.MailTo;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ZoomButtonsController;

import com.natixis.natixisresearch.app.R;
import com.natixis.natixisresearch.app.utils.Utils;


public class CGVActivity extends BaseActivity {


    public static final int REQUEST_CODE = 1000;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cgv);
		ActionBar mActionBar = getActionBar();
        if(mActionBar!=null) {
            mActionBar.setIcon(android.R.color.transparent);

            mActionBar.setDisplayHomeAsUpEnabled(false);
            mActionBar.setDisplayShowHomeEnabled(false);
            mActionBar.setHomeButtonEnabled(false);
        }
		setTitle(getString(R.string.cgv_title));

	}

    public void onClickRefuse(View v){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.cgv_refused)).setTitle(R.string.cgv_title);
        builder.setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               dialog.dismiss();
            }
        });
        builder.setCancelable(true);

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public void onClickAccept(View v){
        setResult(RESULT_OK);
        finish();
    }
}
