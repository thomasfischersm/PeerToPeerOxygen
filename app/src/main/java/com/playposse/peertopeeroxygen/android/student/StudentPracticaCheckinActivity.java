package com.playposse.peertopeeroxygen.android.student;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.data.DataRepository;
import com.playposse.peertopeeroxygen.android.data.clientactions.GetPracticaByIdClientAction;
import com.playposse.peertopeeroxygen.android.data.missions.MissionDataManager;
import com.playposse.peertopeeroxygen.android.model.ExtraConstants;
import com.playposse.peertopeeroxygen.android.practicamgmt.PracticaManager;
import com.playposse.peertopeeroxygen.android.util.CreateViewUtil;
import com.playposse.peertopeeroxygen.android.util.StringUtil;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.PracticaBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.PracticaUserBean;

/**
 * An {@link android.app.Activity} that lets a student check into an practica. This is triggered
 * by the {@link com.playposse.peertopeeroxygen.android.practicamgmt.PracticaManager} that checks
 * the geo location of the user against practica locations at the start of each activity.
 */
public class StudentPracticaCheckinActivity
        extends StudentParentActivity
        implements GetPracticaByIdClientAction.Callback {

    private static final String LOG_CAT = StudentPracticaCheckinActivity.class.getSimpleName();

    private static final int FB_PROFILE_PHOTO_WIDTH = 200;

    private Long practicaId;
    private PracticaBean practicaBean;

    private TextView practicaNameTextView;
    private TextView practicaHostTextView;
    private TextView practicaTimeTextView;
    private TextView practicaGreetingTextView;
    private GridLayout profilePhotoGridLayout;
    private Button checkinButton;

    public StudentPracticaCheckinActivity() {
        shouldCheckPractica = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_student_practica_checkin_acitivity);
        super.onCreate(savedInstanceState);

        practicaId = getIntent().getLongExtra(ExtraConstants.EXTRA_PRACTICA_ID, -1);

        practicaNameTextView = (TextView) findViewById(R.id.practicaNameTextView);
        practicaHostTextView = (TextView) findViewById(R.id.practicaHostTextView);
        practicaTimeTextView = (TextView) findViewById(R.id.practicaTimeTextView);
        practicaGreetingTextView = (TextView) findViewById(R.id.practicaGreetingTextView);
        profilePhotoGridLayout = (GridLayout) findViewById(R.id.profilePhotoGridLayout);
        checkinButton = (Button) findViewById(R.id.checkinButton);

        checkinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIntoPractica();
            }
        });
    }

    @Override
    public void receiveData(DataRepository dataRepository) {
        practicaBean = dataRepository.getPracticaRepository().getPracticaById(practicaId);

        PracticaUserBean hostUserBean = practicaBean.getHostUserBean();
        String hostName = hostUserBean.getFirstName() + " " + hostUserBean.getLastName();
        String hostStr = getString(R.string.practica_host, hostName);

        String startStr = StringUtil.formatDateTime(practicaBean.getStart());
        String endStr = StringUtil.formatDateTime(practicaBean.getEnd());
        String timeStr = getString(R.string.practica_time, startStr, endStr);

        practicaNameTextView.setText(practicaBean.getName());
        practicaHostTextView.setText(hostStr);
        practicaTimeTextView.setText(timeStr);
        practicaGreetingTextView.setText(practicaBean.getGreeting());

        dataServiceConnection.getLocalBinder().getPracticaById(practicaId, this);
    }

    private void checkIntoPractica() {
        if ((dataServiceConnection.getLocalBinder() != null) && (practicaBean != null)) {
            MissionDataManager.switchToDomainAsync(
                    practicaBean.getDomainId(),
                    getApplicationContext(),
                    dataServiceConnection.getLocalBinder());
            PracticaManager.checkin(practicaBean, dataServiceConnection.getLocalBinder());
            startActivity(new Intent(this, StudentMainActivity.class));
        }
    }

    @Override
    public void onResult(PracticaBean practicaBean) {
        Log.i(LOG_CAT, "Updating view with practica attendees.");
        if ((practicaBean == null) || (practicaBean.getAttendeeUserBeans() == null)) {
            return;
        }

        int attendeeBoxWidth = FB_PROFILE_PHOTO_WIDTH + 2 * CreateViewUtil.DEFAULT_PADDING;
        int columnCount = profilePhotoGridLayout.getWidth() / attendeeBoxWidth;
        profilePhotoGridLayout.setColumnCount(columnCount);

        profilePhotoGridLayout.removeAllViews();
        for (PracticaUserBean practicaUserBean : practicaBean.getAttendeeUserBeans()) {
            // Skip self.
            if (getDataRepository().getUserBean().getId().equals(practicaUserBean.getId())) {
                continue;
            }

            LinearLayout linearLayout =
                    CreateViewUtil.createLinearLayout(this, LinearLayout.VERTICAL);
            linearLayout.getLayoutParams().width =
                    attendeeBoxWidth;
            CreateViewUtil.setDefaultPadding(linearLayout);
            profilePhotoGridLayout.addView(linearLayout);

            String photoUrl = practicaUserBean.getProfilePictureUrl();
            linearLayout.addView(CreateViewUtil.createNetworkImageView(this, photoUrl));
            String attendeeName =
                    practicaUserBean.getFirstName() + " " + practicaUserBean.getLastName();
            linearLayout.addView(CreateViewUtil.createTextView(this, attendeeName));
        }
        Log.i(LOG_CAT, "Should see practica attendee count: "
                + profilePhotoGridLayout.getChildCount());
    }
}
