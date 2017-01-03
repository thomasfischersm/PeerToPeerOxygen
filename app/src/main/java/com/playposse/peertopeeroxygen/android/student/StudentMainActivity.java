package com.playposse.peertopeeroxygen.android.student;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.data.DataRepository;
import com.playposse.peertopeeroxygen.android.missiondependencies.MissionLadderSorter;
import com.playposse.peertopeeroxygen.android.model.ExtraConstants;
import com.playposse.peertopeeroxygen.android.util.LogoutUtil;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionLadderBean;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * Activity that is the home page for students. It has links to the mission ladders/trees and
 * important pages (e.g. profile).
 */
public class StudentMainActivity extends StudentParentActivity {

    public static final String LOG_TAG = StudentMainActivity.class.getSimpleName();

    private LinearLayout rootView;
    private TextView missionHeadingTextView;
    private Button studentProfileButton;
    private Button viewPracticaButton;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_student_main);
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        rootView = (LinearLayout) findViewById(R.id.rootView);
        missionHeadingTextView = (TextView) findViewById(R.id.missionHeadingTextView);
        studentProfileButton = (Button) findViewById(R.id.studentProfileButton);
        viewPracticaButton = (Button) findViewById(R.id.viewPracticaButton);
        logoutButton = (Button) findViewById(R.id.logoutButton);

        studentProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), StudentProfileActivity.class));
            }
        });

        viewPracticaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =
                        new Intent(getApplicationContext(), StudentViewPracticaActivity.class);
                startActivity(intent);
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogoutUtil.logout(getApplicationContext());
            }
        });

        debug(); // TODO: Remove
    }

    @Override
    public void receiveData(DataRepository dataRepository) {
        List<MissionLadderBean> missionLadderBeans =
                dataRepository.getMissionLadderBeans();
        missionLadderBeans = MissionLadderSorter.sort(missionLadderBeans);

        View afterView = missionHeadingTextView;
        clearButtons(afterView);
        for (MissionLadderBean missionLadderBean : missionLadderBeans) {
            afterView = addMissionLadderButton(missionLadderBean, afterView);
        }

        if (dataRepository.getPracticaRepository().getCurrentPractica() != null) {
            viewPracticaButton.setVisibility(View.VISIBLE);
        } else {
            viewPracticaButton.setVisibility(View.GONE);
        }

        setTitle(dataRepository.getCompleteMissionDataBean().getDomainBean().getName());
    }

    /**
     * Removes all the buttons following a {@link TextView} until a non-Button {@link View} is
     * encountered.
     */
    private void clearButtons(View afterView) {
        int index = rootView.indexOfChild(afterView) + 1;

        while (index < rootView.getChildCount()) {
            if (rootView.getChildAt(index) instanceof Button) {
                rootView.removeViewAt(index);
            } else {
                break;
            }
        }
    }

    private View addMissionLadderButton(final MissionLadderBean missionLadderBean, View afterView) {
        Button button = new Button(this);
        button.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        button.setText(missionLadderBean.getName());

        rootView.addView(button, rootView.indexOfChild(afterView) + 1);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (missionLadderBean.getMissionTreeBeans().size() == 0) {
                    Log.i(LOG_TAG, "The mission ladder doesn't have a mission tree yet.");
                    return;
                }

                Intent intent =
                        new Intent(getApplicationContext(), StudentMissionTreeActivity.class);
                intent.putExtra(
                        ExtraConstants.EXTRA_MISSION_LADDER_ID,
                        missionLadderBean.getId());
                intent.putExtra(
                        ExtraConstants.EXTRA_MISSION_TREE_ID,
                        missionLadderBean.getMissionTreeBeans().get(0).getId());
                startActivity(intent);
            }
        });

        return button;
    }

    public static void debug() {
        outputKey("AE:D6:52:11:CE:E2:FB:AC:3E:A9:ED:AF:C3:4B:DA:04:47:8C:84:74"); // Alienware
        outputKey("68:9A:DB:72:ED:EF:94:06:66:53:15:61:17:25:31:37:85:69:13:06"); // old PC
        outputKey("4E:3F:B0:9E:8B:2D:9E:D9:51:F2:57:3A:3A:9B:2B:63:C8:99:10:00"); // Mitra
    }

    private static void outputKey(String shaStr) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        for (String number : shaStr.split(":")) {
            outputStream.write(Integer.parseInt(number, 16));
        }
        byte[] sha = outputStream.toByteArray();
        String base64 = Base64.encodeToString(sha, 0);
        Log.i(LOG_TAG, shaStr + " -> " + base64);
    }
}
