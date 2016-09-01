package com.playposse.peertopeeroxygen.backend.schema.util;

import com.googlecode.objectify.stringifier.Stringifier;

/**
 * {@link Stringifier} that convers a {@link Long} to a string for Objectify.
 */
public class LongStringifier implements Stringifier<Long> {
    @Override
    public String toString(Long obj) {
        return obj.toString();
    }

    @Override
    public Long fromString(String str) {
        return new Long(str);
    }
}
