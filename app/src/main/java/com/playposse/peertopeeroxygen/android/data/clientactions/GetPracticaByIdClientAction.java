package com.playposse.peertopeeroxygen.android.data.clientactions;

import com.playposse.peertopeeroxygen.android.data.practicas.PracticaRepository;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.PracticaBean;

import java.io.IOException;

/**
 * A client action that retrieves practica data about a specific practica.
 *
 * <p>Note: This {@link ApiClientAction} has the side effect of saving the data to the
 * {@link PracticaRepository} as it provides the most up-to-date truth.
 */
public class GetPracticaByIdClientAction extends ApiClientAction {

    private final Long practicaId;
    private final Callback callback;

    private PracticaBean practicaBean;

    public GetPracticaByIdClientAction(
            BinderForActions binder,
            Long practicaId,
            Callback callback) {

        super(binder, false);

        this.practicaId = practicaId;
        this.callback = callback;
    }

    @Override
    protected void executeAsync() throws IOException {
        practicaBean = getApi().getPracticaById(getSessionId(), practicaId, getDomainId()).execute();
    }

    @Override
    protected void postExecute() {
        getDataRepository().getPracticaRepository().replacePractica(practicaBean);

        callback.onResult(practicaBean);
    }

    /**
     * A callback that provides the practica from the server.
     */
    public interface Callback {
        void onResult(PracticaBean practicaBean);
    }
}
