package com.playposse.peertopeeroxygen.android.admin;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.data.DataRepository;
import com.playposse.peertopeeroxygen.android.data.OxygenSharedPreferences;
import com.playposse.peertopeeroxygen.android.data.clientactions.ClientAction;
import com.playposse.peertopeeroxygen.android.data.clientactions.GetAllLoanerDevicesAction;
import com.playposse.peertopeeroxygen.android.ui.dialogs.StringPickerDialog;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.LoanerDeviceBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.UserBean;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

/**
 * An {@link android.app.Activity} that shows loaner devices and allows the current device to be
 * marked as a loaner.
 */
public class AdminLoanerDeviceActivity
        extends AdminParentActivity
        implements LoaderManager.LoaderCallbacks<List<LoanerDeviceBean>>,ClientAction.CompletionCallback {

    private static final String LOG_CAT = AdminLoanerDeviceActivity.class.getSimpleName();

    private static final int LOADER_ID = 1;

    private Button markButton;
    private ListView loanerDevicesListView;

    private LoanerDevicesLoader loanerDevicesLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_admin_loaner_device);
        super.onCreate(savedInstanceState);

        setTitle(R.string.loaner_devices_title);

        markButton = (Button) findViewById(R.id.markButton);
        loanerDevicesListView = (ListView) findViewById(R.id.loanerDevicesListView);

        if (OxygenSharedPreferences.getLoanerDeviceId(this) == null) {
            markButton.setText(R.string.mark_loaner_device);
            markButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    markLoanerDevice();
                }
            });
        } else {
            markButton.setText(R.string.unmark_loaner_device);
            markButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dataServiceConnection.getLocalBinder().unmarkLoanerDevice(
                            AdminLoanerDeviceActivity.this);
                }
            });
        }

        loanerDevicesLoader = new LoanerDevicesLoader(this);
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    private void markLoanerDevice() {
        StringPickerDialog.build(
                this,
                R.string.friendly_device_name_dialog_title,
                new StringPickerDialog.Callback() {
                    @Override
                    public void onResult(String friendlyDeviceName) {
                        dataServiceConnection.getLocalBinder().markLoanerDevice(
                                friendlyDeviceName,
                                AdminLoanerDeviceActivity.this);
                    }
                });
    }

    @Override
    public void receiveData(DataRepository dataRepository) {
        dataServiceConnection.getLocalBinder().getAllLoanerDevices(
                new GetAllLoanerDevicesAction.Callback() {
                    @Override
                    public void onResult(List<LoanerDeviceBean> loanerDeviceBeanList) {
                        Log.i(LOG_CAT, "Received loaner device data.");
                        loanerDevicesLoader.commitContentChanged();
                        loanerDevicesLoader.deliverResult(loanerDeviceBeanList);
                    }
                });
    }

    @Override
    public Loader<List<LoanerDeviceBean>> onCreateLoader(int i, Bundle bundle) {
        return loanerDevicesLoader;
    }

    @Override
    public void onLoadFinished(
            Loader<List<LoanerDeviceBean>> loader,
            List<LoanerDeviceBean> loanerDeviceBeans) {

        if (loanerDeviceBeans != null) {
            LoanerDeviceAdapter loanerDeviceAdapter = new LoanerDeviceAdapter(this, loanerDeviceBeans);
            loanerDevicesListView.setAdapter(loanerDeviceAdapter);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<LoanerDeviceBean>> loader) {
        // Nothing to do. This Loader doesn't listen to dynamic changes in the data.
    }

    /**
     * Receives a call when a device is marked or unmarked as a loaner to refresh the data in the
     * {@link android.app.Activity}.
     */
    @Override
    public void onComplete() {
        finish();
        startActivity(new Intent(this, AdminLoanerDeviceActivity.class));
    }

    private static class LoanerDevicesLoader extends Loader<List<LoanerDeviceBean>> {
        public LoanerDevicesLoader(Context context) {
            super(context);
        }
    }

    private class LoanerDeviceAdapter extends ArrayAdapter<LoanerDeviceBean> {

        public LoanerDeviceAdapter(Context context, List<LoanerDeviceBean> loanerDeviceBeans) {
            super(context, R.layout.list_item_loaner_device, loanerDeviceBeans);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(
                        R.layout.list_item_loaner_device,
                        parent,
                        false);

                viewHolder = new ViewHolder();
                viewHolder.friendlyDeviceNameTextView =
                        (TextView) convertView.findViewById(R.id.friendlyDeviceNameTextView);
                viewHolder.lastLoginDateTextView =
                        (TextView) convertView.findViewById(R.id.lastLoginDateTextView);
                viewHolder.lastLoginUserNameTextView =
                        (TextView) convertView.findViewById(R.id.lastLoginUserNameTextView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            LoanerDeviceBean loanerDeviceBean = getItem(position);
            Date lastLoginDate = new Date(loanerDeviceBean.getLastLogin());
            String lastLoginDateStr =
                    DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                            .format(lastLoginDate);
            UserBean userBean = loanerDeviceBean.getLastUserBean();
            String userName = userBean.getFirstName() + " " + userBean.getLastName();

            viewHolder.friendlyDeviceNameTextView.setText(loanerDeviceBean.getFriendlyName());
            viewHolder.lastLoginDateTextView.setText(lastLoginDateStr);
            viewHolder.lastLoginUserNameTextView.setText(userName);

            return convertView;
        }

        /**
         * Implementation of the ViewHolder pattern for smooth scrolling.
         */
        private class ViewHolder {

            private TextView friendlyDeviceNameTextView;
            private TextView lastLoginDateTextView;
            private TextView lastLoginUserNameTextView;
        }
    }
}
