package com.playposse.peertopeeroxygen.backend.schema.util;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.stringifier.Stringifier;
import com.playposse.peertopeeroxygen.backend.schema.MissionLadder;

/**
 * A {@link Stringifier} that converts {@link Ref}s into strings.
 */
public class MissionLadderRefStringifier implements Stringifier<Ref<MissionLadder>> {

    @Override
    public String toString(Ref<MissionLadder> ref) {
        return "" + ref.getKey().getId();
    }

    @Override
    public Ref<MissionLadder> fromString(String str) {
        return Ref.create(Key.create(MissionLadder.class, Long.parseLong(str)));
    }
}
