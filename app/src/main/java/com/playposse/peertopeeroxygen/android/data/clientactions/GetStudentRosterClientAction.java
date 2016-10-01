package com.playposse.peertopeeroxygen.android.data.clientactions;

import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.PeerToPeerOxygenApi;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.UserBean;

import java.io.IOException;
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
        Long sessionId = getBinder().getSessionId();
        PeerToPeerOxygenApi api = getBinder().getApi();
        final List<UserBean> userBeans = api.getStudentRoster(sessionId).execute().getItems();

        // Fire callback on UI thread.
        callback.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                callback.receiveData(userBeans);
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
         * @param runnable
         */
        void runOnUiThread(Runnable runnable);
    }
}
