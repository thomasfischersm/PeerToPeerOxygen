package com.playposse.peertopeeroxygen.android.admin;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.data.DataRepository;
import com.playposse.peertopeeroxygen.android.data.clientactions.GetAllMissionStatsClientAction;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionStatsBean;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * {@link android.app.Activity} that shows an admin usage statistics of the missions.
 */
public class AdminMissionStatsActivity
        extends AdminParentActivity
        implements LoaderManager.LoaderCallbacks<List<MissionStatsBean>> {

    private static final String LOG_CAT = AdminMissionStatsActivity.class.getSimpleName();

    private static final int LOADER_ID = 1;

    private ListView statsListView;

    private MissionStatsLoader missionStatsLoader;
    private MissionStatsByRatingComparator missionStatsByRatingComparator
            = new MissionStatsByRatingComparator();;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_admin_mission_stats);
        super.onCreate(savedInstanceState);

        statsListView = (ListView) findViewById(R.id.statsListView);

        missionStatsLoader = new MissionStatsLoader(this);
        getLoaderManager().initLoader(LOADER_ID, null,  this);
        Log.i(LOG_CAT, "onCreate admin stats activity finished");
    }

    @Override
    public void receiveData(DataRepository dataRepository) {
        Log.i(LOG_CAT, "receiveData is called");

        dataServiceConnection.getLocalBinder().getAllMissionStats(
                new GetAllMissionStatsClientAction.Callback() {
                    @Override
                    public void onResult(List<MissionStatsBean> missionStatsBeanList) {
                        Log.i(LOG_CAT, "stats callback is called.");
                        Collections.sort(missionStatsBeanList, missionStatsByRatingComparator);
                        missionStatsLoader.commitContentChanged();
                        missionStatsLoader.deliverResult(missionStatsBeanList);
                    }
                });
    }

    @Override
    public Loader<List<MissionStatsBean>> onCreateLoader(int loaderId, Bundle bundle) {
        if (loaderId == LOADER_ID) {
            return missionStatsLoader;
        } else {
            return null;
        }
    }

    @Override
    public void onLoadFinished(
            Loader<List<MissionStatsBean>> loader,
            List<MissionStatsBean> missionStatsBeanList) {

        Log.i(LOG_CAT, "onLoadFinished " + missionStatsBeanList);
        if (missionStatsBeanList != null) {
            MissionStatsArrayAdapter adapter =
                    new MissionStatsArrayAdapter(missionStatsBeanList);
            statsListView.setAdapter(adapter);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<MissionStatsBean>> loader) {

    }

    /**
     * Simple loader for the mission stats data. Because the data service uses a different
     * threading model, the actual loading happens externally to the loader.
     */
    private static class MissionStatsLoader extends Loader<List<MissionStatsBean>> {

        public MissionStatsLoader(Context context) {
            super(context);
        }
    }

    private class MissionStatsArrayAdapter extends ArrayAdapter<MissionStatsBean> {

        public MissionStatsArrayAdapter(List<MissionStatsBean> missionStatsBeanList) {
            super(
                    AdminMissionStatsActivity.this,
                    R.layout.list_item_mission_stats,
                    missionStatsBeanList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;

            if (convertView == null) {
                convertView = getLayoutInflater().inflate(
                        R.layout.list_item_mission_stats,
                        parent,
                        false);

                viewHolder = new ViewHolder();
                viewHolder.missionNameTextView =
                        (TextView) convertView.findViewById(R.id.missionNameTextView);
                viewHolder.completionCountTextView =
                        (TextView) convertView.findViewById(R.id.completionCountTextView);
                viewHolder.averageRatingTextView =
                        (TextView) convertView.findViewById(R.id.averageRatingTextView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            MissionStatsBean missionStatsBean = getItem(position);
            Integer ratingCount = missionStatsBean.getRatingCount();
            final String ratingStr;
            if ((ratingCount != null) && (ratingCount > 0)) {
                double ratingTotal = missionStatsBean.getRatingTotal();
                ratingStr = String.format("%.1f", ratingTotal / ratingCount);
            } else {
                ratingStr = "";
            }

            viewHolder.missionNameTextView.setText(
                    missionStatsBean.getMissionBean().getName().trim());
            viewHolder.completionCountTextView.setText("" + missionStatsBean.getCompletionCount());
            viewHolder.averageRatingTextView.setText(ratingStr);


            return convertView;
        }
    }

    /**
     * ViewHolder for the {@link View}s in the layout of the {@link ListView}.
     */
    private static class ViewHolder {
        private TextView missionNameTextView;
        private TextView completionCountTextView;
        private TextView averageRatingTextView;
    }

    private static class MissionStatsByRatingComparator implements Comparator<MissionStatsBean> {

        @Override
        public int compare(MissionStatsBean s0, MissionStatsBean s1) {
            if ((s0 == null) && (s1 == null)) {
                return 0;
            } else if (s0 == null) {
                return 1;
            } else if (s1 == null) {
                return -1;
            } else {
                double rating0 = ((double) s0.getRatingTotal()) / s0.getRatingCount();
                double rating1 = ((double) s1.getRatingTotal()) / s1.getRatingCount();
                return Double.compare(rating1, rating0);
            }
        }
    }
}