package com.playposse.peertopeeroxygen.android.data.clientactions;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.playposse.peertopeeroxygen.android.data.DataService;
import com.playposse.peertopeeroxygen.android.data.missions.MissionDataManager;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.DomainBean;

import java.io.IOException;

/**
 * An {@link ApiClientAction} that updates the information of a domain.
 */
public class SaveDomainClientAction extends ApiClientAction {

    private final String domainName;
    private final String domainDescription;
    private final Callback callback;

    private DomainBean domainBean;

    public SaveDomainClientAction(
            BinderForActions binder,
            String domainName,
            String domainDescription,
            Callback callback) {

        super(binder, false);

        this.domainName = domainName;
        this.domainDescription = domainDescription;
        this.callback = callback;
    }

    @Override
    protected void executeAsync() throws IOException {
        try {
        domainBean = getApi()
                .saveDomain(getSessionId(), getDomainId(), domainName, domainDescription)
                .execute();
        } catch (GoogleJsonResponseException ex) {
            if (ex.getStatusCode() == 403) {
                // This error code means that the domain name is already taken.
                domainBean = null;
                return;
            } else {
                // This is an unexpected error.
                throw ex;
            }
        }

        // Save the domain bean.
        getDataRepository().getCompleteMissionDataBean().setDomainBean(domainBean);
        MissionDataManager.saveSync(getContext(), (DataService.LocalBinder) getBinder());
    }

    @Override
    protected void postExecute() {
        if (domainBean != null) {
            callback.onResult(domainBean);
        } else {
            callback.onDuplicateNameError(domainName);
        }
    }

    /**
     * A callback interface to report success or failure of changing the domain information.
     */
    public interface Callback {

        void onResult(DomainBean domainBean);

        void onDuplicateNameError(String domainName);
    }
}
