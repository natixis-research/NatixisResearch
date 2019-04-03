package com.natixis.natixisresearch.app.activity.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.natixis.natixisresearch.app.R;
import com.natixis.natixisresearch.app.activity.BrowserActivity;
import com.natixis.natixisresearch.app.network.bean.ResearchRequestError;
import com.natixis.natixisresearch.app.network.bean.User;
import com.natixis.natixisresearch.app.network.bean.UserLogin;
import com.natixis.natixisresearch.app.network.listener.LoginRequestListener;
import com.natixis.natixisresearch.app.network.request.LoginRequest;
import com.natixis.natixisresearch.app.network.request.RetryPolicyNone;
import com.natixis.natixisresearch.app.network.request.types.RequestLanguage;
import com.natixis.natixisresearch.app.utils.Utils;
import com.natixis.natixisresearch.app.BuildConfig;

import com.octo.android.robospice.persistence.DurationInMillis;


public class ConnectionFragment extends BaseFragment implements LoginRequestListener.OnAuthenticationFinishedListener {

    public static final int REQUEST_LOGIN = 1;
    private LoginRequestListener.OnAuthenticationFinishedListener mListener;

    EditText txtLogin = null;
    EditText txtPassword = null;
    private TextView tvConnection = null;
    private TextView tvForgotPassword = null;

    Button btLogin =null;
    Button  btSignup =null;
    ProgressBar progressLogin = null;


    public static ConnectionFragment newInstance() {
        ConnectionFragment fragment = new ConnectionFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_connection, container, false);

      /*  btConnection = (Button) v.findViewById(R.id.bt_connexion);
        if (btConnection != null) btConnection.setOnClickListener(this);
*/

        txtLogin = (EditText) v.findViewById(R.id.txt_login);

        txtPassword = (EditText) v.findViewById(R.id.txt_password);
        btLogin = (Button) v.findViewById(R.id.bt_connexion);
        if(btLogin!=null){
            btLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickBtConnection(view);
                }
            });
        }
        btSignup = (Button) v.findViewById(R.id.bt_signup);
        if(btSignup!=null){
            btSignup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickSignup(view);
                }
            });
        }
        progressLogin = (ProgressBar) v.findViewById(R.id.progress_login);
        tvForgotPassword = (TextView) v.findViewById(R.id.txtForgotPasword);
        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickForgotPassword(view);
            }
        });

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (tvConnection != null) {
                    if (txtPassword.getText().length() > 0 && txtLogin.getText().length() > 0) {
                        tvConnection.setEnabled(true);
                    } else {
                        tvConnection.setEnabled(false);
                    }
                }
            }
        };


        txtPassword.addTextChangedListener(watcher);
        txtLogin.addTextChangedListener(watcher);

        txtPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    onClickBtConnection(null);
                }
                return true;
            }
        });



       ImageView ivLogin= (ImageView) v.findViewById(R.id.iv_login);
        ivLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavigationDrawerFragment frag =  ((NavigationDrawerFragment) getParentFragment());
                if(frag!=null){
                    frag.onClickHome(view);
                }

            }
        });


        return v;
    }

    private void onClickSignup(View view) {
        Utils.sendMail(getBaseActivity(), "research.globalmarkets@natixis.com", getString(R.string.commercial_request), "");
    }


    private void onClickBtConnection(View v) {

        if ((txtLogin != null && Utils.isNotNullOrEmpty(txtLogin.getText().toString())) &&
                (txtPassword != null && Utils.isNotNullOrEmpty(txtPassword.getText().toString()))) {
            UserLogin userLogin = new UserLogin();
            userLogin.setLogin(txtLogin.getText().toString());
            userLogin.setPassword(txtPassword.getText().toString());

            refreshBtConnection(true, false);
            LoginRequest request = new LoginRequest(userLogin);
            request.setRetryPolicy(new RetryPolicyNone());

            getSpiceManager().execute(request, "login", DurationInMillis.ALWAYS_EXPIRED, new LoginRequestListener(this));

        }
    }


    private void refreshBtConnection(boolean progress, boolean success) {
        if (progress) {
            progressLogin.setVisibility(View.VISIBLE);
        } else if (success) {
            progressLogin.setVisibility(View.GONE);
        } else {
            progressLogin.setVisibility(View.GONE);
        }
    }

    public void onAuthenticationSuccess(final User user) {
        refreshBtConnection(false, true);

        getNatixisApp().onAuthenticationSuccess(getBaseActivity(), user);


        NavigationDrawerFragment frag =  ((NavigationDrawerFragment) getParentFragment());
        if(frag!=null){
            frag.onLoginSucceeded();
        }

        // Check if no view has focus:
        if(this.getActivity()!=null && this.isAdded()) {
            View view = this.getActivity().getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    public void onAuthenticationFailed(ResearchRequestError error) {
        refreshBtConnection(false, false);
        final Context context = getBaseActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.login_error_message).setTitle(R.string.login_error_title);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }



    private void onClickForgotPassword(View v) {
        RequestLanguage lang = getBaseActivity().getResearchRequestLanguage();
        String url = BuildConfig.RESET_PWD_URL+lang+"/ResetPassword?referer";
        Intent intent = new Intent(getBaseActivity(), BrowserActivity.class);
        intent.putExtra(BrowserActivity.PARAMETER_URL, url);
        startActivity(intent);
        getBaseActivity().overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_bottom);

    }
}
