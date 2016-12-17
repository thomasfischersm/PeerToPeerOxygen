package com.playposse.peertopeeroxygen.android.firebase.actions;

import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.data.OxygenSharedPreferences;
import com.playposse.peertopeeroxygen.android.data.practicas.PracticaRepository;
import com.playposse.peertopeeroxygen.android.firebase.FirebaseMessage;
import com.playposse.peertopeeroxygen.android.firebase.actions.data.TempPracticaBean;
import com.playposse.peertopeeroxygen.android.firebase.actions.data.TempPracticaUserBean;
import com.playposse.peertopeeroxygen.android.util.ToastUtil;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.PracticaBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.PracticaUserBean;

import java.io.IOException;
import java.util.Set;

/**
 * Firebase client action that processes updates to practica meta information.
 */
public class PracticaUpdateClientAction extends FirebaseClientAction {

    private final String LOG_CAT = PracticaUpdateClientAction.class.getSimpleName();

    private static final String PRACTICA_BEAN = "practicaBean";

    public PracticaUpdateClientAction(RemoteMessage remoteMessage) {
        super(remoteMessage);
    }

    @Override
    protected void execute(RemoteMessage remoteMessage) {
        try {
            PracticaUpdateMessage message = new PracticaUpdateMessage(remoteMessage);

            Set<Long> subscribedDomainIds =
                    OxygenSharedPreferences.getSubscribedDomainIds(getApplicationContext());
            Long domainId = message.getPracticaBean().getDomainId();
            if (!subscribedDomainIds.contains(domainId)) {
                // Skip because it's an unrelated domain.
                // TODO: Improve this to only send Firebase messages for subscribed domains!
                return;
            }

            PracticaRepository practicaRepository = getDataRepository().getPracticaRepository();
            practicaRepository.updatePracticaNotAttendees(message.getPracticaBean(), getApplicationContext());

            if (getDataRepository().getUserBean().getAdmin()) {
                ToastUtil.sendToast(getApplicationContext(), R.string.received_practica_update);
            }
        } catch (IOException ex) {
            Log.e(LOG_CAT, "Failed to process update of practica information.", ex);
        }
    }

    private static final class PracticaUpdateMessage extends FirebaseMessage {

        private PracticaUpdateMessage(RemoteMessage message) {
            super(message);
        }

        private PracticaBean getPracticaBean() throws IOException {
            String practicaJson = data.get(PRACTICA_BEAN);

            TempPracticaBean tempPracticaBean = new Gson().fromJson(practicaJson, TempPracticaBean.class);
            PracticaBean practicaBean = new PracticaBean();
            practicaBean.setId(tempPracticaBean.getId());
            practicaBean.setName(tempPracticaBean.getName());
            practicaBean.setGreeting(tempPracticaBean.getGreeting());
            practicaBean.setStart(tempPracticaBean.getStart());
            practicaBean.setEnd(tempPracticaBean.getEnd());
            practicaBean.setAddress(tempPracticaBean.getAddress());
            practicaBean.setGpsLocation(tempPracticaBean.getGpsLocation());
            practicaBean.setCreated(tempPracticaBean.getCreated());
            practicaBean.setTimezone(tempPracticaBean.getTimezone());
            practicaBean.setDomainId(tempPracticaBean.getDomainId());

            if (tempPracticaBean.getHostUserBean() != null) {
                TempPracticaUserBean tempHostBean = tempPracticaBean.getHostUserBean();
                PracticaUserBean hostBean = new PracticaUserBean();
                hostBean.setId(tempHostBean.getId());
                hostBean.setAdmin(tempHostBean.isAdmin());
                hostBean.setFirstName(tempHostBean.getFirstName());
                hostBean.setLastName(tempHostBean.getLastName());
                hostBean.setName(tempHostBean.getName());
                hostBean.setProfilePictureUrl(tempHostBean.getProfilePictureUrl());
                hostBean.setStudiedMissions(tempHostBean.getStudiedMissions());
                hostBean.setCompletedLevels(tempHostBean.getCompletedLevels());
                practicaBean.setHostUserBean(hostBean);
            }

            return practicaBean;
        }
    }
}
