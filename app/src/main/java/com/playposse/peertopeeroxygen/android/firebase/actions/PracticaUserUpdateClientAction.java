package com.playposse.peertopeeroxygen.android.firebase.actions;

import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.playposse.peertopeeroxygen.android.data.practicas.PracticaRepository;
import com.playposse.peertopeeroxygen.android.firebase.FirebaseMessage;
import com.playposse.peertopeeroxygen.android.firebase.actions.data.TempPracticaUserBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.PracticaUserBean;

import java.io.IOException;

/**
 * A Firebase client action that receives metadata of other attendees who check in and graduate
 * missions.
 */
public class PracticaUserUpdateClientAction extends FirebaseClientAction {

    private static final String LOG_CAT = PracticaUserUpdateClientAction.class.getSimpleName();

    private static final String PRACTICA_USER_BEAN = "practicaUserBean";
    private static final String PRACTICA_ID_KEY = "practicaId";

    public PracticaUserUpdateClientAction(RemoteMessage remoteMessage) {
        super(remoteMessage);
    }

    @Override
    protected void execute(RemoteMessage remoteMessage) {
        try {
            PracticaUpdateMessage message = new PracticaUpdateMessage(remoteMessage);

            PracticaRepository practicaRepository = getDataRepository().getPracticaRepository();
            practicaRepository.updateAttendee(message.getPracticaId(), message.getPracticaUserBean());
        } catch (IOException ex) {
            Log.e(LOG_CAT, "Failed to update metadata about practica attendee.", ex);
            // TODO: Consider trying to update through the API.
        }
    }

    private static final class PracticaUpdateMessage extends FirebaseMessage {

        private PracticaUpdateMessage(RemoteMessage message) {
            super(message);
        }

        private PracticaUserBean getPracticaUserBean() throws IOException {
            String practicaUserJson = data.get(PRACTICA_USER_BEAN);

            TempPracticaUserBean tempPracticaUserBean =
                    new Gson().fromJson(practicaUserJson, TempPracticaUserBean.class);
            PracticaUserBean practicaUserBean = new PracticaUserBean();

            practicaUserBean.setId(tempPracticaUserBean.getId());
            practicaUserBean.setAdmin(tempPracticaUserBean.isAdmin());
            practicaUserBean.setFirstName(tempPracticaUserBean.getFirstName());
            practicaUserBean.setLastName(tempPracticaUserBean.getLastName());
            practicaUserBean.setName(tempPracticaUserBean.getName());
            practicaUserBean.setProfilePictureUrl(tempPracticaUserBean.getProfilePictureUrl());
            practicaUserBean.setStudiedMissions(tempPracticaUserBean.getStudiedMissions());
            practicaUserBean.setCompletedLevels(tempPracticaUserBean.getCompletedLevels());

            return practicaUserBean;
        }

        public Long getPracticaId() {
            return Long.valueOf(data.get(PRACTICA_ID_KEY));
        }
    }
}
