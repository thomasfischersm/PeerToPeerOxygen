package com.playposse.peertopeeroxygen.android.admin;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.data.DataRepository;
import com.playposse.peertopeeroxygen.android.data.facebook.FacebookProfilePhotoCache;
import com.playposse.peertopeeroxygen.android.data.types.PointType;
import com.playposse.peertopeeroxygen.android.model.ExtraConstants;
import com.playposse.peertopeeroxygen.android.model.UserBeanParcelable;

import java.text.DateFormat;
import java.util.Date;

/**
 * An activity that shows an admin the details of a student.
 */
public class AdminStudentDetailActivity extends AdminParentActivity {

    private static final String LOG_CAT = AdminStudentDetailActivity.class.getSimpleName();

    private UserBeanParcelable studentBean;

    private ImageView profilePhotoImageView;
    private TextView nameTextView;
    private TextView signupDateTextView;
    private TextView teachPointsTextView;
    private TextView practicePointsTextView;
    private TextView heartPointsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_admin_student_detail);
        super.onCreate(savedInstanceState);

        studentBean = getIntent().getParcelableExtra(ExtraConstants.EXTRA_STUDENT_BEAN);

        String studentName = studentBean.getFirstName() + " " + studentBean.getLastName();
        setTitle(studentName);

        // Find View references.
        profilePhotoImageView = (ImageView) findViewById(R.id.profilePhotoImageView);
        nameTextView = (TextView) findViewById(R.id.nameTextView);
        signupDateTextView = (TextView) findViewById(R.id.signupDateTextView);
        teachPointsTextView = (TextView) findViewById(R.id.teachPointsTextView);
        practicePointsTextView = (TextView) findViewById(R.id.practicePointsTextView);
        heartPointsTextView = (TextView) findViewById(R.id.heartPointsTextView);

        // Format date.
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
        String signupDate = dateFormat.format(new Date(studentBean.getCreated()));
        String signupDateLabel = String.format(getString(R.string.sign_up_date_label), signupDate);

        // Show data.
        nameTextView.setText(studentName);
        signupDateTextView.setText(signupDateLabel);
        showPoints(teachPointsTextView, PointType.teach, R.string.teach_points_label);
        showPoints(practicePointsTextView, PointType.practice, R.string.practice_points_label);
        showPoints(heartPointsTextView, PointType.heart, R.string.heart_points_label);

        // Load profile photo asynchronously.
        String photoUrlString = studentBean.getProfilePictureUrl();
//        new LoadProfilePhotoAsyncTask(profilePhotoImageView, photoUrlString).execute();
    }

    private void showPoints(TextView textView, PointType pointType, int messageId) {
        Integer points = studentBean.getPointMap().get(pointType);
        if (points == null) {
            points = 0;
        }
        String pointsString = String.format(getString(messageId), points);
        textView.setText(pointsString);
    }

    @Override
    public void receiveData(DataRepository dataRepository) {
        FacebookProfilePhotoCache photoCache = dataServiceConnection
                .getLocalBinder()
                .getDataRepository()
                .getFacebookProfilePhotoCache();
        photoCache.loadImage(
                this,
                profilePhotoImageView,
                studentBean.getFbProfileId(),
                studentBean.getProfilePictureUrl());
    }

//    private class LoadProfilePhotoAsyncTask extends AsyncTask<Void, Void, Bitmap> {
//
//        private final ImageView imageView;
//        private final String photoUrlString;
//
//        public LoadProfilePhotoAsyncTask(ImageView imageView, String photoUrlString) {
//            this.imageView = imageView;
//            this.photoUrlString = photoUrlString;
//        }
//
//        @Override
//        protected Bitmap doInBackground(Void... voids) {
//            final Bitmap photoBitmap;
//            try {
//                URL photoUrl = new URL(photoUrlString);
//                InputStream inputStream = photoUrl.openConnection().getInputStream();
//                photoBitmap = BitmapFactory.decodeStream(inputStream);
//                inputStream.close();
//                return photoBitmap;
//            } catch (IOException ex) {
//                Log.e(LOG_CAT, "Failed to load student profile photo." + ex);
//                return null;
//            }
//        }
//
//        @Override
//        protected void onPostExecute(Bitmap photoBitmap) {
//            imageView.setImageBitmap(photoBitmap);
//        }
//    }
}
