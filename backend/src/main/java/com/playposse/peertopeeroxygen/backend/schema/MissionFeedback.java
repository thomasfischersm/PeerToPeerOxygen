package com.playposse.peertopeeroxygen.backend.schema;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;

import javax.annotation.Nullable;

/**
 * An Objectify entity that stores missionRef feedback from the userRef;
 */
@Entity
public class MissionFeedback {

    @Id private Long id;
    @Load private Ref<OxygenUser> userRef;
    @Index @Load private Ref<Mission> missionRef;
    private int rating;
    private Long date = System.currentTimeMillis();
    @Nullable private String comment;

    public MissionFeedback() {
    }

    public MissionFeedback(
            Ref<Mission> missionRef,
            Ref<OxygenUser> userRef,
            int rating,
            @Nullable String comment) {

        this.missionRef = missionRef;
        this.userRef = userRef;
        this.rating = rating;
        this.comment = comment;
    }

    @Nullable
    public String getComment() {
        return comment;
    }

    public Long getDate() {
        return date;
    }

    public Long getId() {
        return id;
    }

    public Ref<Mission> getMissionRef() {
        return missionRef;
    }

    public int getRating() {
        return rating;
    }

    public Ref<OxygenUser> getUserRef() {
        return userRef;
    }
}
