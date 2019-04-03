package com.natixis.natixisresearch.app.activity.fragment;


import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.natixis.natixisresearch.app.R;
import com.natixis.natixisresearch.app.activity.ConnectionActivity;
import com.natixis.natixisresearch.app.activity.ParameterActivity;
import com.natixis.natixisresearch.app.network.bean.CustomUniverse;
import com.natixis.natixisresearch.app.network.bean.ResearchUniverse;
import com.natixis.natixisresearch.app.network.bean.User;
import com.natixis.natixisresearch.app.utils.Utils;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends BaseFragment implements View.OnClickListener {

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String UNIVERSE_FRAGMENT = "UNIVERSE_FRAGMENT";
    private static final String LOGIN_FRAGMENT = "LOGIN_FRAGMENT";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;


    private View mFragmentContainerView;

    private DrawerLayout mDrawerLayout;

    private ImageView btParameters = null;


    private ImageButton btDownloads = null;
    private ImageButton btVideos = null;
    private ImageButton btPublications = null;
    private ImageButton btSendMail = null;
    private TextView tvBadgeDownloads = null;


    private CustomUniverse universeAllPublications;
    private CustomUniverse universeDownloads;
    private CustomUniverse universeVideos;
    // private CustomUniverse universeFavorites;


    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Select either the default item (0) or the last selected item.
        loadStaticCategories();
        //onClickHome(null);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }


    private void loadStaticCategories() {
        universeAllPublications = new CustomUniverse(CustomUniverse.UNIVERSE_ALL_PUBLICATIONS, R.string.all_publications);

      //  universeDownloads = new CustomUniverse(CustomUniverse.UNIVERSE_DOWNLOADS, R.string.downloads);

        universeDownloads = new CustomUniverse(CustomUniverse.UNIVERSE_DOWNLOADSFAVORITES, R.string.downloads);
        universeVideos = new CustomUniverse(CustomUniverse.UNIVERSE_VIDEOS, R.string.videos);
        // universeFavorites = new CustomUniverse(CustomUniverse.UNIVERSE_FAVORITES, R.string.favorites);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RelativeLayout layout = (RelativeLayout) inflater.inflate(
                R.layout.fragment_navigation_drawer, container, false);

        layout.setEnabled(false);
        btDownloads = (ImageButton) layout.findViewById(R.id.bt_downloads);
        if (btDownloads != null) btDownloads.setOnClickListener(this);


        btParameters = (ImageView) layout.findViewById(R.id.bt_parameter);
        if (btParameters != null) btParameters.setOnClickListener(this);

        btVideos = (ImageButton) layout.findViewById(R.id.bt_videos);
        if (btVideos != null) btVideos.setOnClickListener(this);

        btPublications = (ImageButton) layout.findViewById(R.id.bt_publications);
        if (btPublications != null) btPublications.setOnClickListener(this);

        btSendMail = (ImageButton) layout.findViewById(R.id.bt_contact);
        if (btSendMail != null) btSendMail.setOnClickListener(this);

        tvBadgeDownloads = (TextView) layout.findViewById(R.id.tvBadgeDownloads);



        //  downloadUniverses(false);

        if (!isUserLogged()) {

            setLoginFragment();
        } else {
            setUniverseFragment();
        }
        return layout;
    }

    private void setUniverseFragment() {
        new Handler().post(new Runnable() {
            public void run() {
                Fragment universeFrag = UniverseFragment.newInstance();
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, universeFrag, UNIVERSE_FRAGMENT).commit();

            }
        });
        btVideos.setVisibility(View.VISIBLE);
        btPublications.setVisibility(View.GONE);

    }


    private void setLoginFragment() {
        new Handler().post(new Runnable() {
            public void run() {

                Fragment connectionFrag = ConnectionFragment.newInstance();
                if(!getChildFragmentManager().isDestroyed()) {
                    FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, connectionFrag, LOGIN_FRAGMENT).commit();
                }
            }
        });


        btVideos.setVisibility(View.GONE);
        btPublications.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public boolean isDrawerOpen() {
        return isOpened;
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout, Toolbar toolbar) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        if (mDrawerLayout != null) {
            // set a custom shadow that overlays the main content when the drawer opens
            mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
            mDrawerLayout.setStatusBarBackgroundColor(Color.RED);


            // ActionBarDrawerToggle ties together the the proper interactions
            // between the navigation drawer and the action bar app icon.
            mDrawerToggle = new ActionBarDrawerToggle(
                    getActivity(),                    /* host Activity */
                    mDrawerLayout,                    /* DrawerLayout object */

                    toolbar,
                    R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                    R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
            ) {
                @Override
                public void onDrawerClosed(View drawerView) {
                    super.onDrawerClosed(drawerView);
                    if (!isAdded()) {
                        return;
                    }

                    getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
                    getBaseActivity().onDrawerClosed();
                }

                @Override
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    if (!isAdded()) {
                        return;
                    }
                    if (getUniverseFragment() != null) {
                        getUniverseFragment().refreshFavorites();
                    }

                    getBaseActivity().onDrawerOpened();
                    refreshDownloadCount();
                }
            };


            // Defer code dependent on restoration of previous instance state.
            mDrawerLayout.post(new Runnable() {
                @Override
                public void run() {
                    if (mDrawerToggle != null) {
                        mDrawerToggle.syncState();
                    }
                }
            });

            mDrawerLayout.setDrawerListener(mDrawerToggle);

        } else {
            UniverseFragment frag = getUniverseFragment();
            if (frag != null) {
                frag.refreshUserinfo();

            }
        }


        refreshDownloadCount();
    }

    private void refreshDownloadCount() {
        if(tvBadgeDownloads!=null){
            if(getNatixisApp().getFavoritesCache()!=null && getNatixisApp().getFavoritesCache().size()>0) {
                int count = getNatixisApp().getFavoritesCache().size();
                tvBadgeDownloads.setText(count+"");
                tvBadgeDownloads.setVisibility(View.VISIBLE);
            }else{
                tvBadgeDownloads.setVisibility(View.INVISIBLE);
            }
        }
    }

    boolean isOpened = false;

    public void openDrawer() {
        isOpened = true;
        if (mDrawerLayout != null) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }
    }

    @Override
    public void onAttach(Activity ctx) {
        super.onAttach(ctx);
        try {
            mCallbacks = (NavigationDrawerCallbacks) ctx;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }


    public void onClickParameter(View v) {
        closeDrawer();
        if (mCallbacks != null) {

            Intent intent = new Intent(getBaseActivity(), ParameterActivity.class);
            startActivityForResult(intent, ParameterFragment.REQUEST_PARAMETER);

        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mDrawerToggle != null) {
            mDrawerToggle.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        if (mDrawerLayout != null && isDrawerOpen()) {
            inflater.inflate(R.menu.global, menu);
            showGlobalContextActionBar();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle != null && mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        //actionBar.setTitle(R.string.menu);
    }

    private ActionBar getSupportActionBar() {
        return getBaseActivity().getSupportActionBar();
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if (v == btParameters) {
            onClickParameter(v);
        } else if (v == btDownloads) {
            onClickDownloads(v);
        } else if (v == btVideos) {
            onClickVideos(v);
        }else if (v == btPublications) {
            onClickHome(v);
        } else if (v == btSendMail) {
            onClickSendMail(v);
        } /*else if (v == btInfos) {
            onClickInfos(v);
        }*/

    }

    private void onClickSendMail(View v) {
        Utils.sendMail(getBaseActivity(), "research.globalmarkets@natixis.com", getString(R.string.commercial_request), "");
    }

    private void onClickVideos(View v) {
        closeDrawer();
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(universeVideos);
        }

        if(getUniverseFragment()!=null) {
            getUniverseFragment().onCustomUniverseSelected();
        }
    }
/*
    private void onClickFavorites(View v) {
        closeDrawer();
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(universeFavorites);
        }
    }*/

    public void closeDrawer() {
        isOpened = false;
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
    }


    private void onClickDownloads(View v) {
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(universeDownloads);
        }
        if(getUniverseFragment()!=null) {
            getUniverseFragment().onCustomUniverseSelected();
        }
    }

    /*
        public void onClickLogin(View v) {
            if (mDrawerLayout != null) {
                mDrawerLayout.closeDrawer(mFragmentContainerView);
            }
            if (mCallbacks != null) {
                if (getNatixisApp().getLoggedUser() != null) {
                    Intent intent = new Intent(getBaseActivity(), ParameterActivity.class);
                    startActivityForResult(intent, ParameterFragment.REQUEST_PARAMETER);
                } else {
                    Intent intent = new Intent(getBaseActivity(), ConnectionActivity.class);
                    startActivityForResult(intent, ConnectionFragment.REQUEST_LOGIN);
                }
            }
        }
    */
    public boolean isUserLogged() {
        return getBaseActivity().getNatixisApp().isUserLogged();
    }

    public UniverseFragment getUniverseFragment() {
        UniverseFragment fragment = null;
        if (isUserLogged()) {
            fragment = (UniverseFragment) getChildFragmentManager().findFragmentByTag(UNIVERSE_FRAGMENT);
        }
        return fragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ParameterFragment.REQUEST_PARAMETER) {
            if (resultCode == ParameterFragment.RESULT_LOGOUT || resultCode == ParameterFragment.RESULT_CLEARCACHE) {
              /*  downloadUniverses(true);
                onClickHome(null);
                refreshUserinfo();*/
                setLoginFragment();
            } else if (resultCode == ParameterFragment.RESULT_CHANGE_LANGUE ) {
                getActivity().recreate();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public void onClickHome(View v) {

        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(universeAllPublications);
        }

        if(getUniverseFragment()!=null) {
            getUniverseFragment().onCustomUniverseSelected();
        }
    }

    public void onLoginSucceeded() {

        setUniverseFragment();
    }
/*
    public void setSelectedUniverse(ResearchUniverse universe) {
        if(universe!=null && getUniverseFragment()!=null){
            getUniverseFragment().onChildUniverseClick();
        }
    }
*/

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface NavigationDrawerCallbacks {

        void onNavigationDrawerItemSelected(Class fragmentType, boolean saveBackStack);

        void onNavigationDrawerItemSelected(ResearchUniverse universe);

        void onUserLoggedIn(User user);
    }

}
