package com.playposse.peertopeeroxygen.android.admin;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.data.DataRepository;
import com.playposse.peertopeeroxygen.android.model.ExtraConstants;
import com.playposse.peertopeeroxygen.android.ui.widgets.EditDateTime;
import com.playposse.peertopeeroxygen.android.util.GeoUtil;
import com.playposse.peertopeeroxygen.android.util.HttpGeoCoder;
import com.playposse.peertopeeroxygen.android.util.StringUtil;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.PracticaBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.PracticaUserBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.UserBean;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * An {@link android.app.Activity} that allows an admin to create and edit practicas.
 */
public class AdminEditPracticaActivity extends AdminParentActivity {

    private static final String LOG_CAT = AdminEditPracticaActivity.class.getSimpleName();

    private Long practicaId;
    private PracticaBean practicaBean;

    private EditText nameEditText;
    private EditDateTime startEditDateTime;
    private EditDateTime endEditDateTime;
    private EditText addressEditText;
    private TextView gpsLocationTextView;
    private TextView hostTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_admin_edit_practica);
        super.onCreate(savedInstanceState);

        nameEditText = (EditText) findViewById(R.id.nameEditText);
        startEditDateTime = (EditDateTime) findViewById(R.id.startEditDateTime);
        endEditDateTime = (EditDateTime) findViewById(R.id.endEditDateTime);
        addressEditText = (EditText) findViewById(R.id.addressEditText);
        gpsLocationTextView = (TextView) findViewById(R.id.gpsLocationTextView);
        hostTextView = (TextView) findViewById(R.id.hostTextView);

        try {
            practicaId = getIntent().getLongExtra(ExtraConstants.EXTRA_PRACTICA_ID, -1);
            String practicaBeanStr = getIntent().getStringExtra(ExtraConstants.EXTRA_PRACTICA_BEAN);
            practicaBean = ExtraConstants.fromJson(practicaBeanStr);
        } catch (IOException ex) {
            Log.e(LOG_CAT, "Failed to get practica bean out of the intent.", ex);
        }

        showData();

        addressEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Ignore.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Ignore.
            }

            @Override
            public void afterTextChanged(Editable s) {
                updateGpsLocation();
            }
        });

        gpsLocationTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String gpsLocation = StringUtil.getCleanString(gpsLocationTextView);
                gpsLocation.replaceAll(" ", "");
                if (gpsLocation != null) {
                    Uri gmmIntentUri = Uri.parse("geo:" + gpsLocation + "?q=" + gpsLocation);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    if (mapIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(mapIntent);
                        Log.i(LOG_CAT, "Opened Google maps with: " + gmmIntentUri);
                    }
                }
            }
        });
    }

    private void showData() {
        if (practicaBean == null) {
            // Create new practica.
            nameEditText.setText("");
            startEditDateTime.init(null);
            endEditDateTime.init(null);
            addressEditText.setText("");
            gpsLocationTextView.setText("");
        } else {
            // Edit existing practica.
            PracticaUserBean hostUserBean = practicaBean.getHostUserBean();
            String hostName = hostUserBean.getFirstName() + " " + hostUserBean.getLastName();

            nameEditText.setText(practicaBean.getName());
            startEditDateTime.init(practicaBean.getStart());
            endEditDateTime.init(practicaBean.getEnd());
            addressEditText.setText(practicaBean.getAddress());
            gpsLocationTextView.setText(practicaBean.getGpsLocation());
            hostTextView.setText(hostName);
        }
    }

    @Override
    public void receiveData(DataRepository dataRepository) {
        if (practicaBean == null) {
            UserBean userBean = dataRepository.getUserBean();
            String hostName = userBean.getFirstName() + " " + userBean.getLastName();
            hostTextView.setText(hostName);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (nameEditText.getText().length() == 0) {
            return;
        }

        if (practicaBean == null) {
            practicaBean = new PracticaBean();
        }

        save();
    }

    private void save() {
        practicaBean.setName(nameEditText.getText().toString());
        practicaBean.setStart(startEditDateTime.getCalendar().getTimeInMillis());
        practicaBean.setEnd(endEditDateTime.getCalendar().getTimeInMillis());
        practicaBean.setAddress(addressEditText.getText().toString());
        practicaBean.setGpsLocation(gpsLocationTextView.getText().toString());

        if (practicaBean.getHostUserBean() == null) {
            UserBean userBean =
                    dataServiceConnection.getLocalBinder().getDataRepository().getUserBean();
            practicaBean.setHostUserBean(new PracticaUserBean());
            practicaBean.getHostUserBean().setId(userBean.getId());
        }

        dataServiceConnection.getLocalBinder().save(practicaBean);
    }

    /**
     * Tries to resolve the current address into a GPS location.
     */
    private void updateGpsLocation() {
        String locationName = addressEditText.getText().toString();

        try {
            HttpGeoCoder.geoCode(
                    getApplicationContext(),
                    locationName,
                    new HttpGeoCoder.GeoCodeCallback() {
                        @Override
                        public void onGeoResponse(String gpsCoordinate) {
                            gpsLocationTextView.setText(gpsCoordinate);
                        }
                    });
        } catch (UnsupportedEncodingException ex) {
            Log.e(LOG_CAT, "Failed to geocode: " + locationName, ex);
        }
    }
}
