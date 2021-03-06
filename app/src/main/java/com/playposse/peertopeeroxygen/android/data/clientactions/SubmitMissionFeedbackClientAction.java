package com.playposse.peertopeeroxygen.android.data.clientactions;

import java.io.IOException;

import javax.annotation.Nullable;

/**
 * A client action that submits mission feedback from the user.
 */
public class SubmitMissionFeedbackClientAction extends ApiClientAction {

    private final Long missionId;
    private final int rating;
    @Nullable private final String comment;

    public SubmitMissionFeedbackClientAction(
            BinderForActions binder,
            Long missionId,
            int rating,
            @Nullable String comment) {

        super(binder, false);

        this.missionId = missionId;
        this.rating = rating;
        this.comment = comment;
    }

    @Override
    protected void executeAsync() throws IOException {
        getApi()
                .submitMissionFeedback(getSessionId(), missionId, rating, getDomainId())
                .setComment(comment)
                .execute();
    }
}
