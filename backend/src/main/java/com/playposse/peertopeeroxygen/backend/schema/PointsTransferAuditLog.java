package com.playposse.peertopeeroxygen.backend.schema;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.OnLoad;
import com.playposse.peertopeeroxygen.backend.schema.util.MigrationConstants;
import com.playposse.peertopeeroxygen.backend.schema.util.RefUtil;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * Objectify entity that records a transfer of points for auditing purposes.
 */
@Entity
public class PointsTransferAuditLog {

    public enum PointsTransferType {
        teachMission,
        awardedByAdmin,
    }

    @Id private Long id;
    private PointsTransferType pointsTransferType;
    @Index private Ref<OxygenUser> recipientId;
    @Index private Ref<OxygenUser> partnerId;
    private UserPoints.PointType pointType;
    private int pointCount;
    @Index private Long date = System.currentTimeMillis();
    @Index private Ref<Domain> domainRef;

    /**
     * Default constructor for Objectify.
     */
    public PointsTransferAuditLog() {
    }

    /**
     * Constructor to create a new entry.
     */
    public PointsTransferAuditLog(
            PointsTransferType pointsTransferType,
            Ref<OxygenUser> recipientId,
            Ref<OxygenUser> partnerId,
            UserPoints.PointType pointType,
            int pointCount,
            Ref<Domain> domainRef) {

        this.pointsTransferType = pointsTransferType;
        this.partnerId = partnerId;
        this.pointCount = pointCount;
        this.pointType = pointType;
        this.recipientId = recipientId;
        this.domainRef = domainRef;
    }

    public PointsTransferType getPointsTransferType() {
        return pointsTransferType;
    }

    public Long getDate() {
        return date;
    }

    public Long getId() {
        return id;
    }

    public Ref<OxygenUser> getPartnerId() {
        return partnerId;
    }

    public int getPointCount() {
        return pointCount;
    }

    public UserPoints.PointType getPointType() {
        return pointType;
    }

    public Ref<OxygenUser> getRecipientId() {
        return recipientId;
    }

    public Ref<Domain> getDomainRef() {
        return domainRef;
    }

    @OnLoad
    public void migrateToMultiDomainSupport() {
        if ((domainRef == null) && (MigrationConstants.DEFAULT_DOMAIN != null)) {
            domainRef = RefUtil.createDomainRef(MigrationConstants.DEFAULT_DOMAIN);
            ofy().save().entity(this).now();
        }
    }
}
