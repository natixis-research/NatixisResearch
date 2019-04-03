package com.natixis.natixisresearch.app.activity;

import android.media.MediaPlayer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.natixis.natixisresearch.app.R;


public class VideoPlayerActivity extends BaseActivity implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener {

    public static final java.lang.String PARAMETER_FILENAME = "filename";
    public static final java.lang.String PARAMETER_TITLE = "title";
    ProgressBar progressBar;
    String filename = "";
    String title = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        progressBar.setVisibility(View.VISIBLE);

        Bundle extras = getIntent().getExtras();


        if (extras != null) {
            filename = extras.getString(PARAMETER_FILENAME);
            title = extras.getString(PARAMETER_TITLE);

            Log.d("CrisisCare", "Opening video " + title + " at " + filename);
        }
        if(filename!=null) {
            getInit();
        }
        else{
            finish();
        }
    }


    VideoView video_player_view;
    MediaController media_Controller;

    public void getInit() {
        Log.d("CrisisCare", "Video player starting : "+ filename);
        video_player_view = (VideoView) findViewById(R.id.video_player_view);
        media_Controller = new MediaController(this);
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int height = dm.heightPixels;
        int width = dm.widthPixels;
        video_player_view.setMinimumWidth(width);
        video_player_view.setMinimumHeight(height);
        video_player_view.setMediaController(media_Controller);
        video_player_view.setVideoPath(filename);

        video_player_view.setOnCompletionListener(this);
        video_player_view.setOnPreparedListener(this);
        //  video_player_view.setOnTouchListener(this);
        video_player_view.start();


    }

    public void stopPlaying() {
        video_player_view.stopPlayback();
        this.finish();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        finish();
    }

    /* @Override
     public boolean onTouch(View v, MotionEvent event) {
         stopPlaying();
         return true;
     }
 */
    @Override
    public void onPrepared(MediaPlayer mp) {
        //mp.setLooping(true);
        progressBar.setVisibility(View.GONE);
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        //we use onSaveInstanceState in order to store the video playback position for orientation change
        savedInstanceState.putInt("Position", video_player_view.getCurrentPosition());
        video_player_view.pause();
        //Rotation blackscreen lag :http://stackoverflow.com/questions/2967962/android-buffering-lag-with-videoview
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //we use onRestoreInstanceState in order to play the video playback from the stored position
       int  position = savedInstanceState.getInt("Position");
        video_player_view.seekTo(position);
        video_player_view.start();
    }

}
