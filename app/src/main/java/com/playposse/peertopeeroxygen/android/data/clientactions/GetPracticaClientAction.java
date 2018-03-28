package com.playposse.peertopeeroxygen.android.data.clientactions;

import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.PracticaBean;

import java.io.IOException;
import java.util.List;

/**
 * A client action to get a list of practicas.
 */
public class GetPracticaClientAction extends ApiClientAction {

    /**
     * An enum that describes that past or future practicas are requested. Current practicas are
     * part of the future list.
     */
    public enum PracticaDates {
        past,
        future,
    }

    private final PracticaDates practicaDates;
    private final List<Long> domainIds;
    private final Callback callback;

    private List<PracticaBean> practicaBeans;

    public GetPracticaClientAction(
            BinderForActions binder,
            PracticaDates practicaDates,
            List<Long> domainIds,
            Callback callback) {

        super(binder, false);

        this.practicaDates = practicaDates;
        this.domainIds = domainIds;
        this.callback = callback;
    }

    @Override
    protected void executeAsync() throws IOException {
        practicaBeans = getApi()
                .getPractica(getSessionId(), domainIds, practicaDates.name())
                .execute()
                .getItems();
    }

    @Override
    protected void postExecute() {
        callback.onResult(practicaBeans);
    }

    /**
     * A callback that provides list of practicas.
     */
    public interface Callback {
        void onResult(List<PracticaBean> practicaBeans);
    }
}
