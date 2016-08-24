package com.playposse.peertopeeroxygen.backend.schema;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * Objectify Entity that represents how many points of a certain type a particular user has.
 */
@Entity
public class UserPoints {

    public enum PointType {
        teach,
        practice,
        heart,
    }

    @Id private Long id;
    private PointType type;
    private int count;

    public UserPoints() {
    }

    public UserPoints(PointType type, int count) {
        this.type = type;
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public PointType getType() {
        return type;
    }
}
