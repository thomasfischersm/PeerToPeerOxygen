package com.playposse.peertopeeroxygen.android.data.practicas;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.gson.reflect.TypeToken;
import com.playposse.peertopeeroxygen.android.data.DataService;
import com.playposse.peertopeeroxygen.android.data.clientactions.GetPracticaClientAction;
import com.playposse.peertopeeroxygen.android.util.StreamUtil;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.PracticaBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.PracticaUserBean;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A client repository that stores meta information about practicas.
 * <p>
 * <p>This repository saves to a cache file each time it receives a data update. When it starts, it
 * tries to read from that cache file.
 */
public class PracticaRepository {

    private static final String LOG_CAT = PracticaRepository.class.getSimpleName();

    private static final String CACHE_FILE = File.separator + "practicaCache.json";

    private Map<Long, PracticaBean> practicaBeans = new HashMap<>();
    private PracticaBean currentPractica;

    public PracticaRepository(final Context context, DataService.LocalBinder localBinder) {
        // Try to load from JSON encoded cache file.
        try {
            if (load(context)) {
                return;
            }
        } catch (IOException ex) {
            Log.e(LOG_CAT, "Failed to load practica repository from cache file.", ex);
        }

        // Couldn't load from cache. Try to retrieve the practica data from the cloud in a separate
        // thread.
        localBinder.getPractica(
                GetPracticaClientAction.PracticaDates.future,
                new GetPracticaClientAction.Callback() {
                    @Override
                    public void onResult(List<PracticaBean> result) {
                        if (practicaBeans == null) {
                            practicaBeans = new HashMap<Long, PracticaBean>();
                        } else {
                            replaceInternalData(result);
                        }
                        Log.i(LOG_CAT, "Loaded practicas from file server: "
                                + practicaBeans.size());
                        save(context);
                    }
                });
    }

    private void replaceInternalData(List<PracticaBean> result) {
        practicaBeans.clear();
        for (PracticaBean practicaBean : result) {
            practicaBeans.put(practicaBean.getId(), practicaBean);
        }
    }

    public void updatePracticaNotAttendees(PracticaBean practicaBean, Context context) {
        PracticaBean existingPracticaBean = practicaBeans.get(practicaBean.getId());
        if (existingPracticaBean != null) {
            practicaBean.setAttendeeUserBeans(existingPracticaBean.getAttendeeUserBeans());
        }

        practicaBeans.put(practicaBean.getId(), practicaBean);

        save(context);
    }

    public void updateAttendee(Long practicaId, PracticaUserBean practicaUserBean) {
        PracticaBean practicaBean = practicaBeans.get(practicaId);
        if (practicaBean != null) {
            List<PracticaUserBean> attendeeUserBeans = practicaBean.getAttendeeUserBeans();
            for (int i = 0; i < attendeeUserBeans.size(); i++) {
                PracticaUserBean otherUserBean = attendeeUserBeans.get(i);
                if (otherUserBean.getId().equals(practicaUserBean.getId())) {
                    attendeeUserBeans.set(i, practicaUserBean);
                    return;
                }
            }
        }

        // TODO: Consider refreshing because something went wrong.
    }

    @Nullable
    public PracticaBean getPracticaById(Long practicaId) {
        return practicaBeans.get(practicaId);
    }

    public List<PracticaBean> getActivePracticas() {
        List<PracticaBean> result = new ArrayList<>();
        long currentMillis = System.currentTimeMillis();
        for (PracticaBean practicaBean : practicaBeans.values()) {
            if ((practicaBean.getStart() <= currentMillis)
                    && (practicaBean.getEnd() >= currentMillis)) {
                result.add(practicaBean);
            }
        }
        Log.i(LOG_CAT, "Found active practicas: " + result.size());
        return result;
    }

    private void save(Context context) {
        try {
            List<PracticaBean> practicaBeanList = new ArrayList<>(practicaBeans.values());
            String json;
            if (practicaBeanList.size() > 0) {
                json = practicaBeanList.get(0).getFactory().toString(practicaBeanList);
            } else {
                json = "";
            }

            StreamUtil.writeTextStream(getCacheFile(context), json);
            Log.i(LOG_CAT, "Saved practica cache to disk:\n" + json);
        } catch (IOException ex) {
            Log.e(LOG_CAT, "Failed to save practica cache.", ex);
        }
    }

    private boolean load(Context context) throws IOException {
        File file = getCacheFile(context);
        if (!file.exists()) {
            return false;
        }

        String json = StreamUtil.readTextStream(file);
        JsonObjectParser jsonParser = new JacksonFactory().createJsonObjectParser();
        List<PracticaBean> practicaBeanList = (List<PracticaBean>) jsonParser.parseAndClose(new StringReader(json), new TypeToken<List<PracticaBean>>() {
        }.getType());
        if (practicaBeanList == null) {
            practicaBeanList = new ArrayList<>();
        }

        replaceInternalData(practicaBeanList);
        Log.i(LOG_CAT, "Loaded practicas from file cache: " + practicaBeanList.size());
        return true;
    }

    private File getCacheFile(Context context) {
        return new File(context.getCacheDir() + CACHE_FILE);
    }

    public PracticaBean getCurrentPractica() {
        return currentPractica;
    }

    public void setCurrentPractica(PracticaBean currentPractica) {
        this.currentPractica = currentPractica;
    }

    public void replacePractica(PracticaBean practicaBean) {
        practicaBeans.put(practicaBean.getId(), practicaBean);
    }
}