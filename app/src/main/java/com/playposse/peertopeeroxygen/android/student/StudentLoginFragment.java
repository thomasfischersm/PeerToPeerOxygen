package com.playposse.peertopeeroxygen.android.student;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.PeerToPeerOxygenApi;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.FacebookBean;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class StudentLoginFragment extends Fragment {

    public static final String LOG_CAT = StudentLoginFragment.class.getSimpleName();

    private LoginButton loginButton;
    private CallbackManager callbackManager;

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        loginButton = (LoginButton) view.findViewById(R.id.login_button);
        loginButton.setFragment(this);
        loginButton.setReadPermissions("public_profile", "email");

        callbackManager = CallbackManager.Factory.create();

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i(LOG_CAT, "Facebook login successful: " + loginResult.getAccessToken());
                Log.i(LOG_CAT, "app id " + loginResult.getAccessToken().getApplicationId());
                Log.i(LOG_CAT, "token " + loginResult.getAccessToken().getToken().length());
//                debugGetUserName();
//
//                debugAppEngine(loginResult.getAccessToken());

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

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        Log.i(LOG_CAT, "StudentLoginFragment.onActivityResult has been called.");
    }

    private void debugGetUserName() {
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject json, GraphResponse response) {
                        try {
                            Log.i(LOG_CAT, json.toString());
                            Log.i(LOG_CAT, "Got graph response: "
                                    + json.get("first_name") + " "
                                    + json.get("last_name"));
                            Log.i(LOG_CAT, "Profile pic: "
                                    + json.getJSONObject("picture").getJSONObject("data").getString("url"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,first_name, last_name,cover,picture.type(large)");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void debugAppEngine(final AccessToken accessToken) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    PeerToPeerOxygenApi oxygenApi = new PeerToPeerOxygenApi.Builder(
                            AndroidHttp.newCompatibleTransport(),
                            new AndroidJsonFactory(),
                            null)
                            .setApplicationName("PeerToPeerOxygen")
                            .setRootUrl("https://peertopeeroxygen.appspot.com/_ah/api/")
                            .build();
                    FacebookBean facebookBean = oxygenApi.testFacebook(accessToken.getToken()).execute();
                    Log.i(LOG_CAT, "Got response from server: " + facebookBean.getName());
                    Log.i(LOG_CAT, "Server Pic: " + facebookBean.getProfilePicUrl());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
