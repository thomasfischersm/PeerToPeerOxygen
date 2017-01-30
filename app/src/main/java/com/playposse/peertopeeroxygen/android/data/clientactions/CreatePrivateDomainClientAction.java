package com.playposse.peertopeeroxygen.android.data.clientactions;

import android.app.Application;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.util.AnalyticsUtil;
import com.playposse.peertopeeroxygen.android.util.ToastUtil;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.DomainBean;

import java.io.IOException;

import static com.playposse.peertopeeroxygen.android.util.AnalyticsUtil.AnalyticsCategory.privateDomainCreation;

/**
 * An {@link ApiClientAction} that calls the cloud end point to create a private domain.
 */
public class CreatePrivateDomainClientAction extends ApiClientAction {

    private final String domainName;
    private final String domainDescription;
    private final Callback callback;

    private DomainBean domainBean;

    public CreatePrivateDomainClientAction(
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
                    .createPrivateDomain(getSessionId(), domainName, domainDescription)
                    .execute();

            reportToAnlytics();
        } catch (GoogleJsonResponseException ex) {
            if (ex.getStatusCode() == 403) {
                // A server check determined that the name is already used.
                String msg = String.format(
                        getContext().getString(R.string.duplicate_private_domain_name_toast),
                        ex.getDetails().getMessage());
                ToastUtil.sendToast(getContext(), msg);
            } else {
                // The error is something else. Throw it back.
                throw ex;
            }
        }
    }

    @Override
    protected void postExecute() {
        if ((callback != null) && (domainBean != null)) {
            callback.onResult(domainBean);
        }
    }

    private void reportToAnlytics() {
        Application app = (Application) getContext().getApplicationContext();
        AnalyticsUtil.reportEvent(app, privateDomainCreation, domainName);
    }

    /**
     * A call back interface that notifies when the private domain has been created.
     */
    public interface Callback {

        void onResult(DomainBean domainBean);
    }
}
