package com.natixis.natixisresearch.app.activity.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StatFs;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.natixis.natixisresearch.app.R;
import com.natixis.natixisresearch.app.activity.BrowserActivity;
import com.natixis.natixisresearch.app.activity.ConnectionActivity;
import com.natixis.natixisresearch.app.network.bean.User;
import com.natixis.natixisresearch.app.network.request.types.RequestLanguage;
import com.natixis.natixisresearch.app.utils.Utils;
import com.natixis.natixisresearch.app.BuildConfig;


import java.util.Locale;


public class ParameterFragment extends BaseFragment implements View.OnClickListener {
    public static final int REQUEST_PARAMETER = 2;
    public static final int RESULT_LOGOUT = 99;
    public static final int RESULT_CLEARCACHE = 98;
    public static final int RESULT_CHANGE_LANGUE=97;
    RelativeLayout layoutUserInfo;
    TextView tvNom = null;
    TextView tvMail = null;
    Button btMentions = null;
    Button btLogout = null;
    Button btSupport = null;
    TextView tv_lang=null;
    RelativeLayout layoutStockage;
    TextView tvDiskUsage = null;
    Button btClearStockage = null;

    public static ParameterFragment newInstance() {
        ParameterFragment fragment = new ParameterFragment();
        return fragment;
    }

    public ParameterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_parameters, container, false);
        layoutUserInfo = (RelativeLayout) v.findViewById(R.id.layout_user_info);
        tvNom = (TextView) v.findViewById(R.id.tv_nom);
        tvMail = (TextView) v.findViewById(R.id.tv_mail);
        btMentions = (Button) v.findViewById(R.id.bt_mentions_legals);

        if (btMentions != null) {
            btMentions.setOnClickListener(this);
        }

        btLogout = (Button) v.findViewById(R.id.bt_logout);

         tv_lang = (TextView) v.findViewById(R.id.tv_lang);
        if(tv_lang!=null) {
            Locale locale = getResources().getConfiguration().locale;
            String lang = locale.getDisplayLanguage(locale);
            StringBuilder displayLanguage = new StringBuilder(lang.toLowerCase());
            displayLanguage.setCharAt(0, Character.toUpperCase(displayLanguage.charAt(0)));

            tv_lang.setText(displayLanguage.toString());
            tv_lang.setOnClickListener(this);
        }

        layoutStockage = (RelativeLayout) v.findViewById(R.id.layout_stockage);
        tvDiskUsage = (TextView) v.findViewById(R.id.tv_disk);
        btClearStockage = (Button) v.findViewById(R.id.bt_clear_stockage);
        btSupport = (Button) v.findViewById(R.id.bt_contact_support);
        if (btSupport != null) {
            btSupport.setOnClickListener(this);
        }

        if (layoutUserInfo != null ) {
            User loggedUser = getNatixisApp().getLoggedUser();
            if (loggedUser == null) {
                layoutUserInfo.setVisibility(View.GONE);
            } else {
                layoutUserInfo.setVisibility(View.VISIBLE);
                tvNom.setText(loggedUser.getFullname());
                tvMail.setText(loggedUser.getEmail());
                btMentions.setOnClickListener(this);
                btLogout.setOnClickListener(this);
            }
        }

        if (layoutStockage != null) {
            btClearStockage.setOnClickListener(this);
            refreshCacheSize();
        }
        return v;
    }

    private void refreshCacheSize() {
        long totalCacheSize = getNatixisApp().getCacheSize();
        float cacheSize = totalCacheSize;
        String unit = "octets";
        if (cacheSize > 1024) {
            cacheSize = cacheSize / 1024F;
            unit = "Ko";
            if (cacheSize > 1024) {
                cacheSize = cacheSize / 1024F;
                unit = "Mo";
                if (cacheSize > 1024) {
                    cacheSize = cacheSize / 1024F;
                    unit = "Go";
                }
            }
        }

        StatFs stat = new StatFs(getNatixisApp().getCacheDir().getAbsolutePath());
        double usedPercent = (totalCacheSize / ((double) stat.getBlockCount() * stat.getBlockSize())) * 100;
        tvDiskUsage.setText(Math.round(cacheSize) + " " + unit + " (" + Math.round(usedPercent) + "%)");
    }

    @Override
    public void onClick(View v) {
        if (v == btMentions ) {
            onClickMentions(v);
        } else if (v == btSupport) {
            onClickSendMail();
        } else if (v == btLogout) {
            onClickLogout();
        }else if (v == btClearStockage) {
            onClickClearStorage();
        }else if(v==tv_lang){
            onClickLanguage();
        }
    }

    private void onClickLanguage() {
        FragmentManager frgManager = getFragmentManager();
        FragmentTransaction frgTransaction = frgManager.beginTransaction();
        ParameterLanguageFragment frgLanguage = ParameterLanguageFragment.newInstance();
        frgTransaction.addToBackStack(null);
        //frgTransaction.hide(ParameterFragment.this);
        frgTransaction.replace(R.id.fragment_param_container, frgLanguage);
        frgTransaction.commit();
    }

    private void onClickSendMail() {
        Utils.sendMail(getBaseActivity(), "support.research.globalmarkets@natixis.com", getString(R.string.technical_issue), "");
    }

    private void onClickLogin() {
        Intent intent = new Intent(getBaseActivity(), ConnectionActivity.class);
        startActivityForResult(intent, ConnectionFragment.REQUEST_LOGIN);

    }

    private void onClickLogout() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getBaseActivity());
        builder.setMessage(getString(R.string.are_you_sure_logout)).setTitle(getString(R.string.logout));
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getNatixisApp().onLogout(getBaseActivity());
                if (getBaseActivity() instanceof NavigationDrawerFragment.NavigationDrawerCallbacks) {
                    ((NavigationDrawerFragment.NavigationDrawerCallbacks) getBaseActivity()).onNavigationDrawerItemSelected(TimelineFragment.class, false);
                }
                getBaseActivity().setResult(RESULT_LOGOUT);
                getBaseActivity().finish();
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void onClickClearStorage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getBaseActivity());
        builder.setMessage(getBaseActivity().getString(R.string.question_clear_storage)).setTitle(getBaseActivity().getString(R.string.title_clear_storage));
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getNatixisApp().clearDownloadCache(getBaseActivity());
                getNatixisApp().clearFavoritesCache();
                refreshCacheSize();
                getBaseActivity().setResult(RESULT_CLEARCACHE);
                getBaseActivity().finish();
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ConnectionFragment.REQUEST_LOGIN) {
            if (resultCode == Activity.RESULT_OK && getBaseActivity().getNatixisApp().isUserLogged()) {
                getBaseActivity().finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void onClickMentions(View v) {
        RequestLanguage lang = getBaseActivity().getResearchRequestLanguage();
        String url = BuildConfig.MENTIONS_URL + lang.toString();
        Intent intent = new Intent(getBaseActivity(), BrowserActivity.class);
        intent.putExtra(BrowserActivity.PARAMETER_URL, url);
        startActivity(intent);
        getBaseActivity().overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_bottom);

    }

}
