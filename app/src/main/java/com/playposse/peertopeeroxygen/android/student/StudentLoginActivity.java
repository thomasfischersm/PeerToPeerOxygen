package com.playposse.peertopeeroxygen.android.student;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.data.DataRepository;
import com.playposse.peertopeeroxygen.android.data.DataService;
import com.playposse.peertopeeroxygen.android.data.DataServiceParentActivity;
import com.playposse.peertopeeroxygen.android.data.OxygenSharedPreferences;

public class StudentLoginActivity extends DataServiceParentActivity {

    private static final String LOG_CAT = StudentLoginActivity.class.getSimpleName();

    private LoginButton loginButton;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.shouldAutoInit = false;
        this.shouldRegisterCallback = false;

        Log.i(LOG_CAT, "Before Facebook SDK init");
        FacebookSdk.sdkInitialize(getApplicationContext());
        Log.i(LOG_CAT, "After Facebook SDK init");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_login);

        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("public_profile", "email");

        callbackManager = CallbackManager.Factory.create();

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i(LOG_CAT, "Facebook login successful: " + loginResult.getAccessToken());
                Log.i(LOG_CAT, "app id " + loginResult.getAccessToken().getApplicationId());
                Log.i(LOG_CAT, "token " + loginResult.getAccessToken().getToken().length());

                dataServiceConnection.getLocalBinder().registerOrLogin(
                        loginResult.getAccessToken().getToken(),
                        new DataService.SignInSuccessCallback() {
                            @Override
                            public void onSuccess() {
                                Intent intent = new Intent(
                                        getApplicationContext(),
                                        StudentMainActivity.class);
                                startActivity(intent);
                            }
                        });
            }

            @Override
            public void onCancel() {
                Log.e(LOG_CAT, "Facebook login was canceled.");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e(LOG_CAT, "Facebook login failed: " + error.getMessage());
            }
        });
        Log.i(LOG_CAT, "Facebook callback registered.");

        // Check if we already have a session id.
        if (OxygenSharedPreferences.getSessionId(this) != -1) {
            startActivity(new Intent(this, StudentMainActivity.class));
        }

        // Apparently, the session ID is dead or something else requires trying to login again if
        // there is an access token but no valid session id.
        if (AccessToken.getCurrentAccessToken() != null) {
            LoginManager.getInstance().logOut();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        Log.i(LOG_CAT, "StudentLoginActivity.onActivityResult has been called.");
    }

    @Override
    public void receiveData(DataRepository dataRepository) {
        // This should actually never be called because this activity disables the initilization
        // call.
    }
}
