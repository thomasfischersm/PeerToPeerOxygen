package com.playposse.peertopeeroxygen.backend.schema.util;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.playposse.peertopeeroxygen.backend.schema.Domain;
import com.playposse.peertopeeroxygen.backend.schema.MasterUser;
import com.playposse.peertopeeroxygen.backend.schema.Mission;
import com.playposse.peertopeeroxygen.backend.schema.OxygenUser;
import com.playposse.peertopeeroxygen.backend.schema.Practica;

import javax.annotation.Nullable;

/**
 * A utility class that makes creating references less verbose.
 */
public class RefUtil {

    public static Ref<Domain> createDomainRef(Long domainId) {
        return Ref.create(Key.create(Domain.class, domainId));
    }

    public static Ref<OxygenUser> createOxygenUserRef(OxygenUser user) {
        return createOxygenUserRef(user.getId());
    }

    public static Ref<OxygenUser> createOxygenUserRef(Long userId) {
        return  Ref.create(Key.create(OxygenUser.class, userId));
    }

    public static Ref<Mission> createMissionRef(Long missionId) {
        return Ref.create(Key.create(Mission.class, missionId));
    }

    public static long getDomainId(Practica practica) {
        return practica.getDomainRef().getKey().getId();
    }

    public static long getDomainId(OxygenUser oxygenUser) {
        return oxygenUser.getDomainRef().getKey().getId();
    }

    public static Ref<MasterUser> createMasterUserRef(MasterUser masterUser) {
        if (masterUser.getId() == null) {
            throw new IllegalArgumentException(
                    "Id of MasterUser is null " + masterUser.getFirstName());
        }
        return Ref.create(Key.create(MasterUser.class, masterUser.getId()));
    }
}
