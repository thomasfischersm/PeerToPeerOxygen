package com.playposse.peertopeeroxygen.android.admin;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.data.DataRepository;

import static com.playposse.peertopeeroxygen.android.data.clientactions.GetPracticaClientAction.PracticaDates.future;
import static com.playposse.peertopeeroxygen.android.data.clientactions.GetPracticaClientAction.PracticaDates.past;

/**
 * An {@link android.app.Activity} that has two tabs to show a list of past and future practicas.
 */
public class AdminShowPracticasActivity extends AdminParentActivity {

    private ViewPager practicaViewPager;
    private Button createPracticaButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_admin_show_practicas);
        super.onCreate(savedInstanceState);

        setTitle(R.string.admin_show_practicas_title);

        practicaViewPager = (ViewPager) findViewById(R.id.practicaViewPager);
        practicaViewPager.setAdapter(new PracticaPagerAdapter(getSupportFragmentManager(), this));

        createPracticaButton = (Button) findViewById(R.id.createPracticaButton);
        createPracticaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AdminEditPracticaActivity.class));
            }
        });
    }

    @Override
    public void receiveData(DataRepository dataRepository) {
        // Nothing to do.
    }

    private static class PracticaPagerAdapter extends FragmentPagerAdapter {

        private final Context context;

        private PracticaPagerAdapter(FragmentManager fm, Context context) {
            super(fm);

            this.context = context;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return AdminShowPracticasFragment.newInstance(past);
                case 1:
                    return AdminShowPracticasFragment.newInstance(future);
                default:
                    throw new RuntimeException("Unexpected fragment requested: " + position);
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return context.getString(R.string.past_practicas_tab_title);
                case 1:
                    return context.getString(R.string.future_practicas_tab_title);
                default:
                    throw new RuntimeException("Unexpected page title requested: " + position);
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
