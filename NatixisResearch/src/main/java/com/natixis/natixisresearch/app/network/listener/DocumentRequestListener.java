package com.natixis.natixisresearch.app.network.listener;

import android.util.Log;
import android.widget.Toast;

import com.natixis.natixisresearch.app.activity.BaseActivity;
import com.natixis.natixisresearch.app.network.bean.ResearchDocument;
import com.natixis.natixisresearch.app.network.bean.ResearchRequestError;
import com.natixis.natixisresearch.app.utils.Utils;
import com.octo.android.robospice.persistence.exception.SpiceException;

import java.io.InputStream;

/**
 * Created by Thibaud on 14/04/2017.
 */
public class DocumentRequestListener extends BaseRequestListener<InputStream> {

    ResearchDocument mDocToFetch = null;
    boolean mOpen = false;
    BaseActivity mContext = null;


    public DocumentRequestListener(BaseActivity context, ResearchDocument docToFetch, boolean open) {
        this.mContext = context;
        this.mDocToFetch = docToFetch;
        this.mOpen = open;
    }

    @Override
    public void onRequestFailure(SpiceException spiceException, ResearchRequestError error) {
        Log.d("NATIXIS", "error");
    }

    @Override
    public void onRequestSuccess(InputStream result) {
        if (result != null) {
                mContext.getNatixisApp().getDocumentCache().addDocumentToCache(mDocToFetch);
        }
        if (mOpen) {
            Log.i("NATIXIS", "Opening PDF file : " + mDocToFetch.getFilename());
            Utils.openDocument(mContext, mDocToFetch);
        }
    }
/*
    private void saveDocument(ResearchDocument doc, InputStream stream) {
        OutputStream output = null;
        try {
            final File file = new File(mContext.getCacheDir(), doc.getFilename());
            if (file.exists()) {
                file.delete();
            }
            output = new FileOutputStream(file);

            try {
                final byte[] buffer = new byte[1024];
                int read;

                while ((read = stream.read(buffer)) != -1)
                    output.write(buffer, 0, read);

                output.flush();
            } finally {
                output.close();
            }
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }*/

}
