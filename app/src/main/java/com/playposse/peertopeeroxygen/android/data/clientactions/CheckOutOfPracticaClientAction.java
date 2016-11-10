package com.playposse.peertopeeroxygen.android.data.clientactions;

import java.io.IOException;

/**
 * A client action that removes the user from the practica attendance.
 */
public class CheckOutOfPracticaClientAction extends ApiClientAction {

    private final  Long practicaId;

    public CheckOutOfPracticaClientAction(BinderForActions binder, Long practicaId) {
        super(binder, false);

        this.practicaId = practicaId;
    }

    @Override
    protected void executeAsync() throws IOException {
        getApi()
                .checkOutOfPractica(getSessionId(), practicaId, getDomainId())
                .execute();
    }
}
