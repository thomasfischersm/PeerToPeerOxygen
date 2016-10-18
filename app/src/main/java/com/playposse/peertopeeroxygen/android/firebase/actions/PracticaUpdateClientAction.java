package com.playposse.peertopeeroxygen.android.firebase.actions;

import android.util.Log;

import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.data.practicas.PracticaRepository;
import com.playposse.peertopeeroxygen.android.firebase.FirebaseMessage;
import com.playposse.peertopeeroxygen.android.model.UserBeanParcelable;
import com.playposse.peertopeeroxygen.android.util.ToastUtil;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.PracticaBean;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

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
            PracticaRepository practicaRepository = getDataRepository().getPracticaRepository();
            practicaRepository.addPractica(message.getPracticaBean(), getApplicationContext());

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
            practicaBean.setStart(tempPracticaBean.getStart());
            practicaBean.setEnd(tempPracticaBean.getEnd());
            practicaBean.setAddress(tempPracticaBean.getAddress());
            practicaBean.setGpsLocation(tempPracticaBean.getGpsLocation());
            practicaBean.setCreated(tempPracticaBean.getCreated());
            practicaBean.setTimezone(tempPracticaBean.getTimezone());

            return practicaBean;
        }
    }

    /**
     * Stop gap measure until we can figure out the parsing problem.
     */
    private static class TempPracticaBean {
        private Long id;
        private String name;
        private Long start;
        private Long end;
        private String address;
        private String gpsLocation;
//        private PracticaUserBean hostUserBean;
//        private List<PracticaUserBean> attendeeUserBeans = new ArrayList<>();
        private Long created;
        private String timezone;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Long getStart() {
            return start;
        }

        public void setStart(Long start) {
            this.start = start;
        }

        public Long getEnd() {
            return end;
        }

        public void setEnd(Long end) {
            this.end = end;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getGpsLocation() {
            return gpsLocation;
        }

        public void setGpsLocation(String gpsLocation) {
            this.gpsLocation = gpsLocation;
        }

        public Long getCreated() {
            return created;
        }

        public void setCreated(Long created) {
            this.created = created;
        }

        public String getTimezone() {
            return timezone;
        }

        public void setTimezone(String timezone) {
            this.timezone = timezone;
        }
    }
}
