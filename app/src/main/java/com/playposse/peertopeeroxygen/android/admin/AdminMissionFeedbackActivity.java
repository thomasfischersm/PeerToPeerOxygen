package com.playposse.peertopeeroxygen.android.admin;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.data.DataRepository;
import com.playposse.peertopeeroxygen.android.data.clientactions.GetAllMissionFeedbackAction;
import com.playposse.peertopeeroxygen.android.ui.widgets.StarRatingView;
import com.playposse.peertopeeroxygen.android.util.StringUtil;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionFeedbackBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.UserBean;

import java.util.List;

/**
 * An {@link android.app.Activity} that shows an admin feedback that users have submitted about
 * missions.
 */
public class AdminMissionFeedbackActivity
        extends AdminParentActivity
        implements LoaderManager.LoaderCallbacks<List<MissionFeedbackBean>> {

    private static final int LOADER_ID = 1;

    private ListView feedbackListView;

    private MissionFeedbackLoader missionFeedbackLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_admin_mission_feedback);
        super.onCreate(savedInstanceState);

        feedbackListView = (ListView) findViewById(R.id.feedbackListView);

        missionFeedbackLoader = new MissionFeedbackLoader(this);
        getLoaderManager().initLoader(LOADER_ID, null,  this);
    }

    @Override
    public void receiveData(DataRepository dataRepository) {
        dataServiceConnection.getLocalBinder().getAllMissionFeedback(
                new GetAllMissionFeedbackAction.Callback() {
                    @Override
                    public void onResult(List<MissionFeedbackBean> missionFeedbackBeanList) {
                        missionFeedbackLoader.commitContentChanged();
                        missionFeedbackLoader.deliverResult(missionFeedbackBeanList);
                    }
                });
    }

    @Override
    public Loader<List<MissionFeedbackBean>> onCreateLoader(int loaderId, Bundle bundle) {
        if (loaderId == LOADER_ID) {
            return missionFeedbackLoader;
        } else {
            return null;
        }
    }

    @Override
    public void onLoadFinished(
            Loader<List<MissionFeedbackBean>> loader,
            List<MissionFeedbackBean> missionFeedbackBeanList) {

        if (missionFeedbackBeanList != null) {
            MissionFeedbackArrayAdapter adapter =
                    new MissionFeedbackArrayAdapter(missionFeedbackBeanList);
            feedbackListView.setAdapter(adapter);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<MissionFeedbackBean>> loader) {

    }

    /**
     * Simple loader for the mission feedback data. Because the data service uses a different
     * threading model, the actual loading happens externally to the loader.
     */
    private static class MissionFeedbackLoader extends Loader<List<MissionFeedbackBean>> {

        public MissionFeedbackLoader(Context context) {
            super(context);
        }
    }

    private class MissionFeedbackArrayAdapter extends ArrayAdapter<MissionFeedbackBean> {

        public MissionFeedbackArrayAdapter(List<MissionFeedbackBean> missionFeedbackBeanList) {
            super(
                    AdminMissionFeedbackActivity.this,
                    R.layout.list_item_mission_feedback,
                    missionFeedbackBeanList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;

            if (convertView == null) {
                convertView = getLayoutInflater().inflate(
                        R.layout.list_item_mission_feedback,
                        parent,
                        false);

                viewHolder = new ViewHolder();
                viewHolder.profilePhotoImageView =
                        (ImageView) convertView.findViewById(R.id.profilePhotoImageView);
                viewHolder.starRatingView =
                        (StarRatingView) convertView.findViewById(R.id.starRatingView);
                viewHolder.studentNameTextView =
                        (TextView) convertView.findViewById(R.id.studentNameTextView);
                viewHolder.commentTextView =
                        (TextView) convertView.findViewById(R.id.commentTextView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            MissionFeedbackBean missionFeedbackBean = getItem(position);
            UserBean userBean = missionFeedbackBean.getUserBean();
            String studentName = userBean.getFirstName() + " " + userBean.getLastName();
            String comment = StringUtil.getCleanString(missionFeedbackBean.getComment());

            loadProfilePhoto(
                    viewHolder.profilePhotoImageView,
                    userBean.getFbProfileId(),
                    userBean.getProfilePictureUrl());
            viewHolder.starRatingView.setRating(missionFeedbackBean.getRating());
            viewHolder.studentNameTextView.setText(studentName);
            if (comment != null) {
                viewHolder.commentTextView.setVisibility(View.VISIBLE);
                viewHolder.commentTextView.setText(comment);
            } else {
                viewHolder.commentTextView.setVisibility(View.GONE);
            }

            return convertView;
        }
    }

    /**
     * ViewHolder for the {@link View}s in the layout of the {@link ListView}.
     */
    private static class ViewHolder {
        private ImageView profilePhotoImageView;
        private StarRatingView starRatingView;
        private TextView studentNameTextView;
        private TextView commentTextView;
    }
}
