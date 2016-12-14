package com.playposse.peertopeeroxygen.backend.serveractions.util;

import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.PeerToPeerOxygenApi;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.DomainBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MasterUserBean;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.json.JsonArray;
import com.restfb.json.JsonObject;
import com.restfb.types.TestUser;
import com.restfb.types.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

/**
 * A utility for handling Facebook API access to create Endpoint tests.
 */
public class ApiTestUtil {

    private static final String LOG_CAT = ApiTestUtil.class.getSimpleName();

    public static final Long TESTING_DOMAIN_ID = 5670976570261504L;
    public static final String TESTING_DOMAIN_INVITATION_CODE = "JFgABAoeTn";

    public static final String GENERATED_DOMAIN_NAME = "Generated domain for testing";
    public static final String GENERATED_DOMAIN_DESCRIPTION = "Do not use!";

    public static final String TEST_USER_NAME = "James Lucas";

    private static final Long CLEAN_DATA_PASS_CODE = 389275931L;

    public static TestUser createFbTestUser(String name) {
        FacebookClient facebookClient = new DefaultFacebookClient(
                FacebookSecrets.APP_TOKEN,
                FacebookSecrets.APP_SECRET,
                Version.VERSION_2_8);
        return facebookClient.publish(
                "1584198245207052/accounts/test-users",
                TestUser.class,
                Parameter.with("installed", "true"),
                Parameter.with("name", name));
    }

    public static MasterUserBean registerOrLoginUser(
            TestUser testUser,
            String firebaseToken,
            @Nullable Long domainId) throws IOException {

        return instantiateApi()
                .registerOrLogin(testUser.getAccessToken(), firebaseToken)
                .setDomainId(domainId)
                .execute();
    }

    public static PeerToPeerOxygenApi instantiateApi() {
        return new PeerToPeerOxygenApi.Builder(
                AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(),
                null)
                .setApplicationName("PeerToPeerOxygen")
                .setRootUrl("https://peertopeeroxygen.appspot.com/_ah/api/")
                .build();
    }

    public static void cleanTestData(PeerToPeerOxygenApi api) throws IOException {
        api.cleanTestData(CLEAN_DATA_PASS_CODE).execute();
        cleanFBTestUsers();
    }

    public static void cleanFBTestUsers() {
        // Create FB client.
        FacebookClient facebookClient = new DefaultFacebookClient(
                FacebookSecrets.APP_TOKEN,
                FacebookSecrets.APP_SECRET,
                Version.VERSION_2_8);

        // Query FB test user ids.
        JsonObject testUsersResponse = facebookClient.fetchObject(
                "1584198245207052/accounts/test-users",
                JsonObject.class,
                Parameter.with("limit", 100));

        // Query FB test users individually.
        JsonArray dataArray = testUsersResponse.getJsonArray("data");
        List<String> fbUserIds = new ArrayList<>(dataArray.length());
        for (int i = 0; i < dataArray.length(); i++) {
            String fbUserId = dataArray.getJsonObject(i).getString("id");
            fbUserIds.add(fbUserId);
        }

        // Iterate of FB test users
        RuntimeException exception = null;
        for (String fbUserId : fbUserIds) {
            try {
                User user = facebookClient.fetchObject(
                        fbUserId,
                        User.class);
                if (TEST_USER_NAME.equals(user.getName())) {
                    boolean result = facebookClient.deleteObject(fbUserId);
                    Log.i(LOG_CAT, "Deleted FB user " + fbUserId + ". Result: " + result);
                }
            } catch (RuntimeException ex) {
                Log.e(LOG_CAT, "Failed to handle this particular fb user. " + fbUserId);
                exception = ex;
            }
        }

        if (exception != null) {
            throw exception;
        }

        System.out.println("bla");

    }

    public static DomainBean createPrivateTestDomain(
            PeerToPeerOxygenApi api,
            MasterUserBean masterUserBean) throws IOException {

        return api.createPrivateDomain(
                masterUserBean.getSessionId(),
                ApiTestUtil.GENERATED_DOMAIN_NAME + new Random().nextLong(),
                ApiTestUtil.GENERATED_DOMAIN_DESCRIPTION)
                .execute();
    }
}
