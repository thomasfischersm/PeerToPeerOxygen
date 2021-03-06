package com.playposse.peertopeeroxygen.android.model;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.playposse.peertopeeroxygen.android.data.types.PointType;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.UserBean;

import java.util.HashMap;
import java.util.Map;

/**
 * A {@link Parcelable} version of the UserBean, so that it can be passed in between
 * {@link Intent}s.
 */
public class UserBeanParcelable implements Parcelable {

    private Long id;
    private boolean isAdmin;
    private String firebaseToken;
    private String fbProfileId;
    private String firstName;
    private String lastName;
    private String name;
    private Long created;
    private Map<String, Integer> pointsMap = new HashMap<>();

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(id);
        out.writeValue(isAdmin);
        out.writeString(firebaseToken);
        out.writeString(fbProfileId);
        out.writeString(firstName);
        out.writeString(lastName);
        out.writeString(name);
        out.writeLong((created != null) ? created : 0);

        if (pointsMap == null) {
            out.writeInt(0);
        } else {
            out.writeInt(pointsMap.size());
            for (Map.Entry<String, Integer> entry : pointsMap.entrySet()) {
                out.writeString(entry.getKey());
                out.writeInt(entry.getValue());
            }
        }
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
        firebaseToken = in.readString();
        fbProfileId = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        name = in.readString();
        created = in.readLong();

        int pointMapSize = in.readInt();
        pointsMap = new HashMap<>(pointMapSize);
        for (int i = 0; i < pointMapSize; i++) {
            PointType pointType = PointType.valueOf(in.readString());
            int pointCount = in.readInt();
            pointsMap.put(pointType.name(), pointCount);
        }
    }

    private UserBeanParcelable(UserBean userBean) {
        id = userBean.getId();
        isAdmin = userBean.getAdmin();
        firebaseToken = userBean.getFirebaseToken();
        fbProfileId = userBean.getFbProfileId();
        firstName = userBean.getFirstName();
        lastName = userBean.getLastName();
        name = userBean.getName();
        created = userBean.getCreated();

        if (userBean.getPointsMap() != null) {
            pointsMap = new HashMap<>(userBean.getPointsMap().size());
            for (Map.Entry<String, Object> entry : userBean.getPointsMap().entrySet()) {
                PointType pointType = PointType.valueOf(entry.getKey());
                int pointCount = Integer.parseInt(entry.getValue().toString());
                pointsMap.put(pointType.name(), pointCount);
            }
        } else {
            pointsMap = new HashMap<>();
        }
    }

    public static UserBeanParcelable fromJson(String json) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(json, UserBeanParcelable.class);
    }

    public static UserBeanParcelable fromBean(UserBean userBean) {
        return new UserBeanParcelable(userBean);
    }

    public String getFirebaseToken() {
        return firebaseToken;
    }

    public void setFirebaseToken(String firebaseToken) {
        this.firebaseToken = firebaseToken;
    }

    public String getFbProfileId() {
        return fbProfileId;
    }

    public void setFbProfileId(String fbProfileId) {
        this.fbProfileId = fbProfileId;
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

    public Long getCreated() {
        return created;
    }

    public Map<String, Integer> getPointsMap() {
        return pointsMap;
    }
}
