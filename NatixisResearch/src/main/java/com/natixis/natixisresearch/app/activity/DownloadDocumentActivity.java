package com.natixis.natixisresearch.app.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.natixis.natixisresearch.app.R;
import com.natixis.natixisresearch.app.network.bean.ResearchDocument;
import com.natixis.natixisresearch.app.network.bean.ResearchRequestError;
import com.natixis.natixisresearch.app.network.generic.NatixisObjectMapper;
import com.natixis.natixisresearch.app.network.listener.DocumentRequestListener;
import com.natixis.natixisresearch.app.network.request.DocumentRequest;
import com.octo.android.robospice.exception.NoNetworkException;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;

import java.io.InputStream;


public class DownloadDocumentActivity extends BaseActivity /*implements LoginRequestListener.OnAuthenticationFinishedListener */ {

    public final static String JSON_DOCUMENT = "json";
    public static final int REQUEST_CODE = 1;
    ResearchDocument docToDownload = null;
    DocumentRequest documentRequest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_document);
        setTitle("");

      /*  ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
*/
        Intent myIntent = getIntent();
        String researchDocument = myIntent.getStringExtra(JSON_DOCUMENT);
        if (researchDocument != null) {
            NatixisObjectMapper mapper = new NatixisObjectMapper();
            try {
                docToDownload = mapper.readValue(researchDocument, ResearchDocument.class);

                requestDocument(docToDownload);

            } catch (Exception e) {
                e.printStackTrace();
                setResult(RESULT_CANCELED);
                finish();
            }
        }
    }


    protected void requestDocument(ResearchDocument document) {

        DocumentRequestListener listener = new DocumentRequestListener(this, document, true) {
            @Override
            public void onRequestSuccess(InputStream result) {
                super.onRequestSuccess(result);
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onRequestFailure(SpiceException spiceException, ResearchRequestError error) {
                super.onRequestFailure(spiceException, error);
                if(spiceException instanceof NoNetworkException) {
                    Toast.makeText(DownloadDocumentActivity.this, getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
                }
                setResult(RESULT_CANCELED);
                finish();
            }
        };


        documentRequest = new DocumentRequest(document);
        getSpiceManager().execute(documentRequest, document.getFilename(), DurationInMillis.ALWAYS_RETURNED, listener);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.global, menu);
        return true;
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
    public void onBackPressed() {
        getSpiceManager().cancel(documentRequest);
        super.onBackPressed();
    }
}
