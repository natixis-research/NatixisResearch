package com.natixis.natixisresearch.app.activity.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.natixis.natixisresearch.app.NatixisResearchApp;
import com.natixis.natixisresearch.app.R;
import com.natixis.natixisresearch.app.activity.adapter.ResearchUniverseExpandableAdapter;
import com.natixis.natixisresearch.app.activity.adapter.UniverseSpinnerAdapter;
import com.natixis.natixisresearch.app.network.bean.CustomUniverse;
import com.natixis.natixisresearch.app.network.bean.ResearchRequestError;
import com.natixis.natixisresearch.app.network.bean.ResearchUniverse;
import com.natixis.natixisresearch.app.network.bean.UniversesList;
import com.natixis.natixisresearch.app.network.bean.User;
import com.natixis.natixisresearch.app.network.listener.UniverseRequestListener;
import com.natixis.natixisresearch.app.network.request.GetAllUniversesRequest;
import com.natixis.natixisresearch.app.network.request.types.RequestLanguage;
import com.natixis.natixisresearch.app.utils.Utils;
import com.octo.android.robospice.persistence.DurationInMillis;

import java.util.ArrayList;


public class UniverseFragment extends BaseFragment implements IUniverseClickListener, UniverseRequestListener.UniverseFinishListener {


    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";


    private RelativeLayout layoutUserInfo = null;
    private ExpandableListView mDrawerListView;
    //private ImageButton btInfos = null;


    private Button btHome = null;

    private int mCurrentSelectedPosition = 0;
    private TextView tvNomPrenom = null;

    SwipeRefreshLayout refreshLayout = null;
    SwipeRefreshLayout refreshLayoutEmpty = null;

    ProgressBar progressUniverse;
    UniversesList universes;
    ResearchUniverseExpandableAdapter adapterFavorites;


    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;
    Spinner spinnerUniverse;
    UniverseSpinnerAdapter spinnerAdapter;
    TextView emptyView;
    NavigationDrawerFragment.NavigationDrawerCallbacks mCallbacks;

    public static UniverseFragment newInstance() {
        UniverseFragment fragment = new UniverseFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }


    }

    @Override
    public void onStart() {
        super.onStart();

        downloadUniverses(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.fragment_universe, container, false);

      /*  btConnection = (Button) v.findViewById(R.id.bt_connexion);
        if (btConnection != null) btConnection.setOnClickListener(this);
*/


        mDrawerListView = (ExpandableListView) layout.findViewById(R.id.list_universes);


        adapterFavorites = new ResearchUniverseExpandableAdapter(getBaseActivity().getBaseContext(), this);
        mDrawerListView.setAdapter(adapterFavorites);


        mDrawerListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                onChildUniverseClick((ResearchUniverse) adapterFavorites.getChild(groupPosition, childPosition));
                return true;
            }
        });

        mDrawerListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                ResearchUniverse universe = (ResearchUniverse) adapterFavorites.getGroup(i);
                if (universe.getSubUniverses().size() <= 0) {
                    onChildUniverseClick((ResearchUniverse) adapterFavorites.getGroup(i));
                    return true;
                }
                return false;

            }
        });

        mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);


        layoutUserInfo = (RelativeLayout) layout.findViewById(R.id.relative_user_layout);


        tvNomPrenom = (TextView) layout.findViewById(R.id.tv_nom_prenom);

        progressUniverse = (ProgressBar) layout.findViewById(R.id.progress_universes);

        btHome = (Button) layout.findViewById(R.id.bt_home);
        if (btHome != null) {
            btHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickBtHome(view);
                }
            });
        }

        refreshLayoutEmpty = (SwipeRefreshLayout) layout.findViewById(R.id.swipe_layout_empty);
        refreshLayoutEmpty.setEnabled(false);
        refreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.swipe_layout);
        refreshLayout.setEnabled(false);

        emptyView = (TextView) layout.findViewById(R.id.emptyView);

        mDrawerListView.setEmptyView(refreshLayoutEmpty);

        refreshUserinfo();

        TextView tvConsult = (TextView) layout.findViewById(R.id.tvConsult);
        tvConsult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickConsultHelp(v);
            }
        });
        TextView tvSelection = (TextView) layout.findViewById(R.id.tvSelection);
        tvSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSelectionHelp(v);
            }
        });


        adapterFavorites = new ResearchUniverseExpandableAdapter(getBaseActivity().getBaseContext(), this);
        mDrawerListView.setAdapter(adapterFavorites);

        spinnerUniverse = (Spinner) layout.findViewById(R.id.universe_spinner);
        spinnerAdapter = new UniverseSpinnerAdapter(getActivity(), R.layout.universe_spinner_item, new ArrayList<ResearchUniverse>());
        spinnerAdapter.setDropDownViewResource(R.layout.universe_spinner_dropdown_item);
        spinnerUniverse.setAdapter(spinnerAdapter);
        spinnerUniverse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (mCallbacks != null) {
                    if (i > 0) {
                        ResearchUniverse universe = spinnerAdapter.getItem(i);
                        onUniverseSelected(universe);
                    } else {
                        // onClickBtHome(view);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return layout;
    }

    private void onClickBtHome(View view) {
        ((NavigationDrawerFragment) getParentFragment()).onClickHome(view);
    }
public void onCustomUniverseSelected(){
    spinnerUniverse.setSelection(0);
}
    public void onUniverseSelected(ResearchUniverse universe) {
        refreshFavoritesSelection(universe);

        mCallbacks.onNavigationDrawerItemSelected(universe);
        ((NavigationDrawerFragment) getParentFragment()).closeDrawer();
    }

    public void onClickConsultHelp(View v) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
        builder1.setMessage(getActivity().getString(R.string.consultation_help));
        builder1.setCancelable(true);

        builder1.setNeutralButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public void onClickSelectionHelp(View v) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
        builder1.setMessage(getActivity().getString(R.string.selection_help));
        builder1.setCancelable(true);

        builder1.setNeutralButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onChildUniverseClick(ResearchUniverse universe) {
        if (spinnerUniverse != null) {
            int pos = spinnerAdapter.getPosition(universe);
            spinnerUniverse.setSelection(pos);
        }

    }

    @Override
    public void onAttach(Activity ctx) {
        super.onAttach(ctx);
        try {
            mCallbacks = (NavigationDrawerFragment.NavigationDrawerCallbacks) ctx;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void OnUniverseRetrieved(UniversesList result) {
        loadCategories(result);
        showProgressBar(false);
    }

    @Override
    public void OnUniverseDownloadFailed(ResearchRequestError error) {
        showProgressBar(false);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    public void refreshUserinfo() {
        if (getNatixisApp().isUserLogged()) {

            User user = getNatixisApp().getLoggedUser();
            if (tvNomPrenom != null) {
                tvNomPrenom.setText(user.getFullname());
            }

        } else {
            if (tvNomPrenom != null) {
                tvNomPrenom.setText(R.string.not_authentified);
            }

        }

    }


    boolean progressBarCancelled = false;

    public void showProgressBar(boolean visible) {
        if (visible) {
            emptyView.setVisibility(View.GONE);
            progressBarCancelled = false;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!progressBarCancelled) {
                        progressUniverse.setVisibility(View.VISIBLE);
                    }
                }
            }, 600);
        } else {
            progressBarCancelled = true;
            progressUniverse.setVisibility(View.GONE);
            refreshLayout.setRefreshing(false);
            refreshLayoutEmpty.setRefreshing(false);
            emptyView.setVisibility(View.VISIBLE);
        }
    }

    private void downloadUniverses(boolean force) {

        RequestLanguage lang = getBaseActivity().getResearchRequestLanguage();
        String token = null;
        if (getNatixisApp().isUserLogged()) {
            token = getNatixisApp().getLoggedUser().getToken();
        }

        showProgressBar(true);

        GetAllUniversesRequest request = new GetAllUniversesRequest(token, lang);
        // request.setRetryPolicy(new RetryPolicyNone());

        String cacheKey = "universes_" + lang;
        long expire = Utils.calculateMaxAge();
        if (force) {
            expire = DurationInMillis.ALWAYS_EXPIRED;
        }
        //getSpiceManager().execute(request, cacheKey, expire, new UniverseRequestListener(this, lang));

        getSpiceManager().getFromCacheAndLoadFromNetworkIfExpired(request, cacheKey, expire, new UniverseRequestListener(this, lang));


    }


    private void loadCategories(UniversesList universes) {
            refreshFavorites();
        if (getContext() != null) {
            CustomUniverse custUniv = new CustomUniverse(CustomUniverse.UNIVERSE_ALL_PUBLICATIONS, getString(R.string.select_universe));
            if (universes.size() > 0 && !universes.get(0).equals(custUniv)) {
                universes.add(0, custUniv);
                spinnerAdapter.refreshUniverses(universes);
                // loadStaticCategories();
            }
        }
    }

    public void refreshFavoritesSelection(ResearchUniverse selectedUniverse) {
        adapterFavorites.refreshSelectedUniverse(selectedUniverse);
    }

    public void refreshFavorites() {
        UniversesList favorites = NatixisResearchApp.getInstance().getFavorites();
        ResearchUniverse selectedUniverse = (ResearchUniverse) spinnerUniverse.getSelectedItem();

        adapterFavorites.refreshUniverses(favorites, selectedUniverse);


    }
}
