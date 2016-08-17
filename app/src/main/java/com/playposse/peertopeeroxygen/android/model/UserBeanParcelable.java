package com.playposse.peertopeeroxygen.android.model;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * A {@link Parcelable} version of the UserBean, so that it can be passed in between
 * {@link Intent}s.
 */
public class UserBeanParcelable implements Parcelable {

    private Long id;
    private boolean isAdmin;
    private String fbProfileId;
    private String firebaseToken;
    private String firstName;
    private String lastName;
    private String name;
    private String profilePictureUrl;

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(id);
        out.writeValue(isAdmin);
        out.writeString(fbProfileId);
        out.writeString(firebaseToken);
        out.writeString(firstName);
        out.writeString(lastName);
        out.writeString(name);
        out.writeString(profilePictureUrl);
    }

    public static final Parcelable.Creator<UserBeanParcelable> CREATOR
            = new Parcelable.Creator<UserBeanParcelable>() {
        public UserBeanParcelable createFromParcel(Parcel in) {
            return new UserBeanParcelable(in);
        }

        public UserBeanParcelable[] newArray(int size) {
            return new UserBeanParcelable[size];
        }
    };

    private UserBeanParcelable(Parcel in) {
        id = in.readLong();
        isAdmin = (Boolean) in.readValue(null);
        fbProfileId = in.readString();
        firebaseToken = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        name = in.readString();
        profilePictureUrl = in.readString();
    }

    public static UserBeanParcelable fromJson(String json) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(json, UserBeanParcelable.class);
    }

    public String getFbProfileId() {
        return fbProfileId;
    }

    public void setFbProfileId(String fbProfileId) {
        this.fbProfileId = fbProfileId;
    }

    public String getFirebaseToken() {
        return firebaseToken;
    }

    public void setFirebaseToken(String firebaseToken) {
        this.firebaseToken = firebaseToken;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }
}
