package com.playposse.peertopeeroxygen.android.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.data.DataRepository;
import com.playposse.peertopeeroxygen.android.data.clientaction.GetStudentRosterAction;
import com.playposse.peertopeeroxygen.android.data.facebook.FacebookProfilePhotoCache;
import com.playposse.peertopeeroxygen.android.data.types.PointType;
import com.playposse.peertopeeroxygen.android.model.ExtraConstants;
import com.playposse.peertopeeroxygen.android.model.UserBeanParcelable;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.UserBean;

import java.util.List;

/**
 * An activity that shows the list of all students (actually all users).
 */
public class AdminStudentRosterActivity
        extends AdminParentActivity
        implements GetStudentRosterAction.StudentRosterCallback {

    private static final String LOG_CAT = AdminStudentRosterActivity.class.getSimpleName();

    private ListView studentListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_admin_student_roster);
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.student_roster_title));

        studentListView = (ListView) findViewById(R.id.studentListView);
    }

    @Override
    public void receiveData(DataRepository dataRepository) {
        dataServiceConnection.getLocalBinder().getStudentRoster(this);
    }

    @Override
    public void receiveData(List<UserBean> userBeans) {
        studentListView.setAdapter(new StudentArrayAdapter(userBeans));
    }

    /**
     * An adapter that shows a student on each row of the {@link ListView}.
     */
    private class StudentArrayAdapter extends ArrayAdapter<UserBean> {

        public StudentArrayAdapter(List<UserBean> objects) {
            super(getApplicationContext(), R.layout.list_item_student, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Create list_item view if needed.
            final ViewHolder viewHolder;
            if (convertView == null) {
                convertView =
                        getLayoutInflater().inflate(R.layout.list_item_student, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            // Populate values
            final UserBean student = getItem(position);
            int practicePoints = DataRepository.getPointByType(student, PointType.practice);
            int teachPoints = DataRepository.getPointByType(student, PointType.teach);
            int heartPoints = DataRepository.getPointByType(student, PointType.heart);

            viewHolder.firstNameTextView.setText(student.getFirstName());
            viewHolder.lastNameTextView.setText(student.getLastName());
            viewHolder.teachPointsTextView.setText("" + teachPoints);
            viewHolder.practicePointsTextView.setText("" + practicePoints);
            viewHolder.heartPointsTextView.setText("" + heartPoints);

            // Add click handler to open student detail activity.
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), AdminStudentDetailActivity.class);
                    intent.putExtra(
                            ExtraConstants.EXTRA_STUDENT_BEAN,
                            UserBeanParcelable.fromBean(student));
                    startActivity(intent);
                }
            });

            // Schedule profile photo for loading in a separate thread.
            FacebookProfilePhotoCache photoCache = dataServiceConnection
                    .getLocalBinder()
                    .getDataRepository()
                    .getFacebookProfilePhotoCache();
            photoCache.loadImage(
                    getContext(),
                    viewHolder.profilePhotoImageView,
                    student.getFbProfileId(),
                    student.getProfilePictureUrl());

            return convertView;
        }

        private class ViewHolder {
            private final ImageView profilePhotoImageView;
            private final TextView firstNameTextView;
            private final TextView lastNameTextView;
            private final TextView teachPointsTextView;
            private final TextView practicePointsTextView;
            private final TextView heartPointsTextView;

            private ViewHolder(View view) {
                profilePhotoImageView = (ImageView) view.findViewById(R.id.profilePhotoImageView);
                firstNameTextView = (TextView) view.findViewById(R.id.firstNameTextView);
                lastNameTextView = (TextView) view.findViewById(R.id.lastNameTextView);
                teachPointsTextView = (TextView) view.findViewById(R.id.teachPointsTextView);
                practicePointsTextView = (TextView) view.findViewById(R.id.practicePointsTextView);
                heartPointsTextView = (TextView) view.findViewById(R.id.heartPointsTextView);
            }
        }
    }
}
