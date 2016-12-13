package com.playposse.peertopeeroxygen.backend.serveractions;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.PeerToPeerOxygenApi;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MasterUserBean;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.types.TestUser;

import java.io.IOException;

import javax.annotation.Nullable;

/**
 * A utility for handling Facebook API access to create Endpoint tests.
 */
public class TestUserUtil {

    public static final Long TESTING_DOMAIN_ID = 5670976570261504L;
    public static final String TESTING_DOMAIN_INVITATION_CODE = "JFgABAoeTn";

    public static final String GENERATED_DOMAIN_NAME = "Generated domain for testing";
    public static final String GENERATED_DOMAIN_DESCRIPTION = "Do not use!";

    public static final String TEST_USER_NAME = "James Lucas";

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
}
