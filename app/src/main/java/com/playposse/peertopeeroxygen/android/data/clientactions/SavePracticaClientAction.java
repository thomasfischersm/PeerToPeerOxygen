package com.playposse.peertopeeroxygen.android.data.clientactions;

import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.PracticaBean;

import java.io.IOException;

import javax.annotation.Nullable;

/**
 * Client action that saves a practica.
 */
public class SavePracticaClientAction extends ApiClientAction {

    private final PracticaBean practicaBean;

    public SavePracticaClientAction(BinderForActions binder, PracticaBean practicaBean) {
        super(binder, false);

        this.practicaBean = practicaBean;
    }

    @Override
    protected void executeAsync() throws IOException {
        getApi()
                .savePractica(getSessionId(), getDomainId(), practicaBean)
                .execute();
    }
}
