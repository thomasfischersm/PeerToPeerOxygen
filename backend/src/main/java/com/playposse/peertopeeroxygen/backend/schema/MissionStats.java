package com.playposse.peertopeeroxygen.backend.schema;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;

/**
 * An Objectify entity that keeps track of the missionRef stats. The missionRef metadata and stats are
 * shared separately, so that memcache can cache the metadata while the stats are dynamically
 * updated.
 */
@Entity
public class MissionStats {

    @Id private Long id;
    @Index @Load private Ref<Mission> missionRef;
    private int completionCount = 0;
    private int ratingTotal = 0;
    private int ratingCount = 0;

    public MissionStats() {
    }

    public MissionStats(
            int completionCount,
            Ref<Mission> missionRef,
            int ratingCount,
            int ratingTotal) {

        this.completionCount = completionCount;
        this.missionRef = missionRef;
        this.ratingCount = ratingCount;
        this.ratingTotal = ratingTotal;
    }

    public void incrementCompletion() {
        completionCount++;
    }

    public void addRating(int rating) {
        ratingTotal += rating;
        ratingCount++;
    }

    public int getCompletionCount() {
        return completionCount;
    }

    public Long getId() {
        return id;
    }

    public Ref<Mission> getMissionRef() {
        return missionRef;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public int getRatingTotal() {
        return ratingTotal;
    }
}
