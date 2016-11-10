package com.playposse.peertopeeroxygen.android.data.clientactions;

import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.PeerToPeerOxygenApi;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.UserBean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A client action that loads all the student data from AppEngine.
 */
public class GetStudentRosterClientAction extends ApiClientAction {

    private final StudentRosterCallback callback;

    public GetStudentRosterClientAction(BinderForActions binder, StudentRosterCallback callback) {
        super(binder, false);

        this.callback = callback;
    }

    @Override
    protected void executeAsync() throws IOException {
        // Access AppEngine for data.
        final List<UserBean> userBeans = getApi()
                .getStudentRoster(getSessionId(), getDomainId())
                .execute()
                .getItems();

        // Fire callback on UI thread.
        callback.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (userBeans != null) {
                    callback.receiveData(userBeans);
                } else {
                    callback.receiveData(new ArrayList<UserBean>(0));
                }
            }
        });
    }

    /**
     * Interface that has to be implemented by the caller of this action to receive the result.
     */
    public interface StudentRosterCallback {

        void receiveData(List<UserBean> userBeans);

        /**
         * Used to switch to the UI Thread of the actitivy implementing this interface.
         *
         * @param runnable
         */
        void runOnUiThread(Runnable runnable);
    }
}
