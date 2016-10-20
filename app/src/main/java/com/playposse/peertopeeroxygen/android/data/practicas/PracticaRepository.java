package com.playposse.peertopeeroxygen.android.data.practicas;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.playposse.peertopeeroxygen.android.data.DataService;
import com.playposse.peertopeeroxygen.android.data.clientactions.GetPracticaClientAction;
import com.playposse.peertopeeroxygen.android.util.StreamUtil;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.CompleteMissionDataBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.PracticaBean;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * A client repository that stores meta information about practicas.
 * <p>
 * <p>This repository saves to a cache file each time it receives a data update. When it starts, it
 * tries to read from that cache file.
 */
public class PracticaRepository {

    private static final String LOG_CAT = PracticaRepository.class.getSimpleName();

    private static final String CACHE_FILE = File.separator + "practicaCache.json";

    private List<PracticaBean> practicaBeanList = new ArrayList<>();
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
                    public void onResult(List<PracticaBean> practicaBeans) {
                        if (practicaBeans == null) {
                            practicaBeanList = new ArrayList<PracticaBean>();
                        } else {
                            practicaBeanList = practicaBeans;
                        }
                        Log.i(LOG_CAT, "Loaded practicas from file server: "
                                + practicaBeanList.size());
                        save(context);
                    }
                });
    }

    public void addPractica(PracticaBean practicaBean, Context context) {
        PracticaBean existingPracticaBean = getPracticaById(practicaBean.getId());
        if (existingPracticaBean != null) {
            practicaBeanList.remove(existingPracticaBean);
        }

        practicaBeanList.add(practicaBean);

        save(context);
    }

    @Nullable
    private PracticaBean getPracticaById(Long practicaId) {
        for (PracticaBean practicaBean : practicaBeanList) {
            if (practicaId.equals(practicaBean.getId())) {
                return practicaBean;
            }
        }
        return null;
    }

    public List<PracticaBean> getActivePracticas() {
        List<PracticaBean> result = new ArrayList<>();
        long currentMillis = System.currentTimeMillis();
        for (PracticaBean practicaBean : practicaBeanList) {
            if ((practicaBean.getStart() <= currentMillis)
                    && (practicaBean.getEnd() >= currentMillis)) {
                result.add(practicaBean);
            }
        }
        return result;
    }

    private void save(Context context) {
        try {
            final String json;
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
//        file.delete();
        if (!file.exists()) {
            return false;
        }

        String json = StreamUtil.readTextStream(file);
        JsonObjectParser jsonParser = new JacksonFactory().createJsonObjectParser();
        practicaBeanList = (List<PracticaBean>) jsonParser.parseAndClose(new StringReader(json), new TypeToken<List<PracticaBean>>() {}.getType());
        if (practicaBeanList == null) {
            practicaBeanList = new ArrayList<>();
        }
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
}
