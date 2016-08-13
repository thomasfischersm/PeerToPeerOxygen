package com.playposse.peertopeeroxygen.android.student;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.playposse.peertopeeroxygen.android.ExtraConstants;
import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.CompleteMissionDataBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionLadderBean;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Activity that is the home page for students. It has links to the mission ladders/trees and
 * important pages (e.g. profile).
 */
public class StudentMainActivity extends StudentParentActivity {

    public static final String LOG_TAG = StudentMainActivity.class.getSimpleName();

    private GridView studentHomeGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_student_main);
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        studentHomeGridView = (GridView) findViewById(R.id.studentHomeGridView);
    }

    @Override
    public void receiveData(final CompleteMissionDataBean completeMissionDataBean) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                List<MissionLadderBean> missionLadderBeans =
                        completeMissionDataBean.getMissionLadderBeans();
                studentHomeGridView.setAdapter(
                        new MissionLadderBeansArrayAdapter(missionLadderBeans));
            }
        });
    }

    private final class MissionLadderBeansArrayAdapter extends ArrayAdapter<MissionLadderBean> {

        public MissionLadderBeansArrayAdapter(List<MissionLadderBean> missionLadderBeans) {
            super(StudentMainActivity.this, R.layout.list_item_student_home_grid, missionLadderBeans);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) StudentMainActivity.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.list_item_student_home_grid, parent, false);

            TextView missionLadderNameLink =
                    (TextView) rowView.findViewById(R.id.missionLadderNameLink);
            final MissionLadderBean missionLadderBean = getItem(position);
            missionLadderNameLink.setText(missionLadderBean.getName());

            missionLadderNameLink.setOnClickListener(new View.OnClickListener() {
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
            return rowView;
        }
    }
}
