package com.playposse.peertopeeroxygen.android.data.clientactions;

import java.io.IOException;

import javax.annotation.Nullable;

/**
 * A client action that promotes a student to the admin role.
 */
public class PromoteToAdminClientAction extends ApiClientAction {

    private Long studentId;
    private boolean isAdmin;

    public PromoteToAdminClientAction(
            Long studentId,
            boolean isAdmin,
            BinderForActions binder,
            @Nullable CompletionCallback completionCallback) {

        super(binder, false, completionCallback);

        this.studentId = studentId;
        this.isAdmin = isAdmin;
    }

    @Override
    protected void executeAsync() throws IOException {
        getApi().promoteToAdmin(getSessionId(), studentId, getDomainId(), isAdmin).execute();
    }
}
