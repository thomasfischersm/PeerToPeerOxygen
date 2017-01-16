package com.playposse.peertopeeroxygen.android.util;

import com.playposse.peertopeeroxygen.android.model.UserBeanParcelable;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.UserBean;

/**
 * A utility class for dealing with Facebook.
 */
public class FacebookUtil {

    private static final String FB_PROFILE_PIC_URL_PATTERN =
            "https://graph.facebook.com/%1$s/picture?type=large";

    /**
     * Returns the URL to the user's Facebook profile.
     *
     * <p>Background: Originally, the AppEngine app used to retrieve a URL to the Facebook profile
     * photo. After a few weeks, that turned out to become a broken link. There is a better way.
     * Facebook has a URL that'll stay constant and always redirects to the current photo.
     */
    public static String getProfilePicture(UserBean userBean) {
        return String.format(FB_PROFILE_PIC_URL_PATTERN, userBean.getFbProfileId());
    }

    public static String getProfilePicture(UserBeanParcelable studentBean) {
        return String.format(FB_PROFILE_PIC_URL_PATTERN, studentBean.getFbProfileId());
    }
}
