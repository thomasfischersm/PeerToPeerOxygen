package com.playposse.peertopeeroxygen.android.data.clientactions;

import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.PracticaBean;

import java.io.IOException;

import javax.annotation.Nullable;

/**
 * A client action that checks the user into a practica. The call returns the current information
 * about the practica and the attendants as well.
 */
public class CheckIntoPracticaClientAction extends ApiClientAction {

    private final Long practicaId;
    private final Callback callback;

    private PracticaBean resultPracticaBean;

    public CheckIntoPracticaClientAction(
            BinderForActions binder,
            Long practicaId,
            Callback callback) {

        super(binder, false);

        this.practicaId = practicaId;
        this.callback = callback;
    }

    @Override
    protected void executeAsync() throws IOException {
        resultPracticaBean = getApi()
                .checkIntoPractica(getSessionId(), practicaId, getDomainId())
                .execute();
    }

    @Override
    protected void postExecute() {
        callback.onResult(resultPracticaBean);
    }

    /**
     * A callback interface that is called when the API call succeeded.
     */
    public interface Callback {

        void onResult(PracticaBean practicaBean);
    }
}
