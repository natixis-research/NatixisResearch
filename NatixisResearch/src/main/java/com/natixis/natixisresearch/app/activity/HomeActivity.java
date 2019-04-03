package com.natixis.natixisresearch.app.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.natixis.natixisresearch.app.NatixisResearchApp;
import com.natixis.natixisresearch.app.R;
import com.natixis.natixisresearch.app.activity.fragment.NavigationDrawerFragment;
import com.natixis.natixisresearch.app.activity.fragment.TimelineFragment;
import com.natixis.natixisresearch.app.activity.fragment.TimelineVideoFragment;
import com.natixis.natixisresearch.app.network.bean.CustomUniverse;
import com.natixis.natixisresearch.app.network.bean.ResearchRequestError;
import com.natixis.natixisresearch.app.network.bean.ResearchUniverse;
import com.natixis.natixisresearch.app.network.bean.SearchUniverse;
import com.natixis.natixisresearch.app.network.bean.User;
import com.natixis.natixisresearch.app.network.listener.JoinNotificationRequestListener;
import com.natixis.natixisresearch.app.network.request.BOJoinNotificationRequest;
import com.natixis.natixisresearch.app.push.GcmRegistrationIntentService;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;

import java.io.InputStream;


public class HomeActivity extends BaseActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    TimelineFragment mTimelineFragment;
    TimelineVideoFragment mTimelineVideoFragment;
    SearchView mSearchView;
    MenuItem mSearchItem;
    ResearchUniverse selectedUniverse = null;
    ResearchUniverse previousSelectedUniverse = null;
    RelativeLayout layoutSearch;
    Switch searchSwitch = null;
    String currentSearchText = "";
    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        if (savedInstanceState != null) {
            // orientation change
            try {
                selectedUniverse = (ResearchUniverse) savedInstanceState.getParcelable("universe");
                mTimelineVideoFragment = (TimelineVideoFragment) getSupportFragmentManager().getFragment(savedInstanceState, "mTimelineVideoFragment");
                mTimelineFragment = (TimelineFragment) getSupportFragmentManager().getFragment(savedInstanceState, "mTimelineFragment");

            } catch (Exception e) {
                selectedUniverse = null;
            }
        }
        if(mTimelineFragment==null) {
            mTimelineFragment = TimelineFragment.newInstance();
        }

        if(mTimelineVideoFragment==null) {
            mTimelineVideoFragment = TimelineVideoFragment.newInstance();
        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout),toolbar);



        //(TimelineFragment)                getSupportFragmentManager().findFragmentById(R.id.timeline_fragment);

        layoutSearch = (RelativeLayout) findViewById(R.id.layout_search);
        searchSwitch = (Switch) findViewById(R.id.switch_extend_search);
        searchSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (currentSearchText.length() > 0) {
                    submitSearch(currentSearchText);
                }
            }
        });




        if (!getNatixisApp().isCGVAccepted()) {
            Handler h = new Handler();
            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(HomeActivity.this, CGVActivity.class);
                    startActivityForResult(intent, CGVActivity.REQUEST_CODE);
                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_bottom);
                }
            }, 1500);
        }

        if(selectedUniverse==null){
            mNavigationDrawerFragment.onClickHome(null);
        }else {
            onNavigationDrawerItemSelected(selectedUniverse);
        }
    }


    @Override
    public void onNavigationDrawerItemSelected(Class fragmentType, boolean saveBackStack) {
        //loadFragment(fragmentType,saveBackStack);
    }

    @Override
    public void onNavigationDrawerItemSelected(ResearchUniverse universe) {

        selectedUniverse = universe;
        if (universe instanceof SearchUniverse) {
            currentSearchText = universe.getTitle(this);
        } else {
            currentSearchText = "";
        }

        FragmentManager fm  =getSupportFragmentManager();
        Fragment frag = fm.findFragmentById(R.id.timeline_container);
        if(universe instanceof CustomUniverse && ((CustomUniverse)universe).getType()==CustomUniverse.UNIVERSE_VIDEOS){
            if(frag==null || !(frag instanceof TimelineVideoFragment)){
                FragmentTransaction ft = fm.beginTransaction();
                ft.add(R.id.timeline_container,mTimelineVideoFragment);
                ft.commit();
            }
            mTimelineVideoFragment.loadUniverse(universe);
        }else{
            if(frag==null || !(frag instanceof TimelineFragment)){
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.timeline_container,mTimelineFragment);
                ft.commit();

            }
            mTimelineFragment.loadUniverse(universe);
        }

    }

    @Override
    public void onUserLoggedIn(User user) {

        Intent intent = new Intent(this, GcmRegistrationIntentService.class);
        startService(intent);
        if(mTimelineFragment!=null && mTimelineFragment.isAdded()){
            mTimelineFragment.onUserLoggedIn();
        }
//joinNotificationService("testtesttesttesttest");

    }

/*
    protected void joinNotificationService(final String token) {

        JoinNotificationRequestListener listener = new JoinNotificationRequestListener(this, token, true) {
            @Override
            public void onRequestSuccess(String result) {
                super.onRequestSuccess(result);
                NatixisResearchApp.getInstance().setRegistrationToken(token);
            }

            @Override
            public void onRequestFailure(SpiceException spiceException, ResearchRequestError error) {
                super.onRequestFailure(spiceException, error);

            }
        };

       User logged =  getNatixisApp().getLoggedUser();
               if(logged!=null) {

                   BOJoinNotificationRequest joinRequest = new BOJoinNotificationRequest(logged.getEmail(), token);
                   getSpiceManager().execute(joinRequest, "join", DurationInMillis.ALWAYS_EXPIRED, listener);
               }

    }
    */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("universe", selectedUniverse);
        if (mTimelineVideoFragment.isAdded()) {
            getSupportFragmentManager().putFragment(outState, "mTimelineVideoFragment", mTimelineVideoFragment);
        }
        if (mTimelineFragment.isAdded()) {
            getSupportFragmentManager().putFragment(outState, "mTimelineFragment", mTimelineFragment);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void setResearchMode(boolean searchModeEnabled){
        if(searchModeEnabled) {
            if(layoutSearch!=null) {
                layoutSearch.setVisibility(View.VISIBLE);
                layoutSearch.setClickable(true);
            }
            if( getActionBar()!=null) {
                getActionBar().setDisplayShowHomeEnabled(false);
             //  getActionBar().setIcon(null);
            }
        }else{
            if(layoutSearch!=null) {
                layoutSearch.setVisibility(View.GONE);
                layoutSearch.setClickable(false);
            }
            if( getActionBar()!=null) {
                getActionBar().setDisplayShowHomeEnabled(true);
            }
        }
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(menu.size()==0) {
            getMenuInflater().inflate(R.menu.global, menu);
            mSearchItem = menu.findItem(R.id.action_search);
            mSearchView = (SearchView) MenuItemCompat.getActionView( mSearchItem);


            mSearchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    setResearchMode(b);

                }
            });

            MenuItemCompat.setOnActionExpandListener(mSearchItem,new MenuItemCompat.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem menuItem) {
                    setResearchMode(true);
                    return true;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                    setResearchMode(false);

                    return true;
                }
            });

            mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                @Override
                public boolean onQueryTextSubmit(String s) {
                    submitSearch(s);
                    //searchItem.collapseActionView();
                    //layoutSearch.setVisibility(View.VISIBLE);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String s) {

                    return false;
                }

            });


        }
        return super.onPrepareOptionsMenu(menu);
    }

    public void OnClickSearchBackground(View v){
        hideSearchView();
    }
    public void hideSearchView(){
        if(mSearchItem!=null) {
            mSearchItem.collapseActionView();
        }
    }
    private void cancelSearch() {
        if (previousSelectedUniverse != null && selectedUniverse instanceof SearchUniverse) {
            onNavigationDrawerItemSelected(previousSelectedUniverse);
        }
    }


    private void submitSearch(String s) {
        mSearchView.clearFocus(); //dirty hack because triggered twice : http://stackoverflow.com/questions/17874951/searchview-onquerytextsubmit-runs-twice-while-i-pressed-once
        if (!(selectedUniverse instanceof SearchUniverse)) {
            previousSelectedUniverse = selectedUniverse;
        }

        SearchUniverse searchUniverse = new SearchUniverse(s, searchSwitch.isChecked());
        onNavigationDrawerItemSelected(searchUniverse);
        hideSearchView();
    }


    @Override
    public void onBackPressed() {

        if (mNavigationDrawerFragment.isDrawerOpen()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.are_you_sure_exit)
                    .setCancelable(false)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    })
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        } else {
            mNavigationDrawerFragment.openDrawer();
        }
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /*
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_home, container, false);
            return rootView;

        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            //  ((HomeActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onDrawerOpened() {
        super.onDrawerOpened();
        if (mSearchItem != null) {
            mSearchItem.setEnabled(false);
        }
    }

    @Override
    public void onDrawerClosed() {
        super.onDrawerClosed();
        if (mSearchItem != null && !mSearchItem.isEnabled()) {
            mSearchItem.setEnabled(true);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CGVActivity.REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                getNatixisApp().markCGVAccepted();
            } else {
                finish();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

}

