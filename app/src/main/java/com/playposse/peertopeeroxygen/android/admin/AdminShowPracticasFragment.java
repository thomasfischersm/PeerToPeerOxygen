package com.playposse.peertopeeroxygen.android.admin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.data.DataRepository;
import com.playposse.peertopeeroxygen.android.data.DataServiceParentFragment;
import com.playposse.peertopeeroxygen.android.data.clientactions.GetPracticaClientAction;
import com.playposse.peertopeeroxygen.android.model.ExtraConstants;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.PracticaBean;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

/**
 * A {@link android.app.Fragment} that shows a list of practicas. It can either be future or past
 * practicas.
 */
public class AdminShowPracticasFragment
        extends DataServiceParentFragment
        implements LoaderManager.LoaderCallbacks<List<PracticaBean>> {

    private final static String LOG_CAT = AdminShowPracticasFragment.class.getSimpleName();

    private static final String PRACTICA_DATES = "practicaDates";
    private static final int LOADER_ID = 4;

    private GetPracticaClientAction.PracticaDates practicaDates;

    private ListView practicaListView;
    private PracticaListLoader practicaListLoader;

    public AdminShowPracticasFragment() {
        // Required empty public constructor
    }

    public static AdminShowPracticasFragment newInstance(
            GetPracticaClientAction.PracticaDates practicaDates) {

        AdminShowPracticasFragment fragment = new AdminShowPracticasFragment();
        Bundle args = new Bundle();
        args.putString(PRACTICA_DATES, practicaDates.name());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            String practicaDatesStr = getArguments().getString(PRACTICA_DATES);
            practicaDates = GetPracticaClientAction.PracticaDates.valueOf(practicaDatesStr);

            getLoaderManager().initLoader(LOADER_ID, null, this);
        }
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_admin_show_practicas, container, false);

        practicaListView = (ListView) rootView.findViewById(R.id.practicaListView);

        return rootView;
    }

    @Override
    public void receiveData(DataRepository dataRepository) {
        dataServiceConnection.getLocalBinder().getPractica(
                practicaDates,
                new GetPracticaClientAction.Callback() {
                    @Override
                    public void onResult(List<PracticaBean> practicaBeanList) {
                        practicaListLoader.commitContentChanged();
                        practicaListLoader.deliverResult(practicaBeanList);
                    }
                });
    }

    @Override
    public Loader<List<PracticaBean>> onCreateLoader(int loaderId, Bundle args) {
        if (loaderId == LOADER_ID) {
            if (practicaListLoader == null) {
                practicaListLoader = new PracticaListLoader(getActivity());
            }
            return practicaListLoader;
        } else {
            return null;
        }
    }

    @Override
    public void onLoadFinished(
            Loader<List<PracticaBean>> loader,
            List<PracticaBean> practicaBeanList) {

        if (practicaBeanList != null) {
            PracticaListAdapter practicaListAdapter =
                    new PracticaListAdapter(getContext(), practicaBeanList);
            practicaListView.setAdapter(practicaListAdapter);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<PracticaBean>> loader) {

    }

    /**
     * A {link ListAdapter} that shows on practica per row.
     */
    private class PracticaListAdapter extends ArrayAdapter<PracticaBean> {

        private PracticaListAdapter(Context context, List<PracticaBean> objects) {
            super(context, R.layout.list_item_practica, objects);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity()
                        .getLayoutInflater()
                        .inflate(R.layout.list_item_practica, parent, false);
            }

            final PracticaBean practicaBean = getItem(position);
            if (practicaBean == null) {
                return convertView;
            }

            TextView nameTextView = (TextView) convertView.findViewById(R.id.nameTextView);
            TextView startTextView = (TextView) convertView.findViewById(R.id.startTextView);
            TextView endTextView = (TextView) convertView.findViewById(R.id.endTextView);

            nameTextView.setText(practicaBean.getName());
            startTextView.setText(formatDate(practicaBean.getStart()));
            endTextView.setText(formatDate(practicaBean.getEnd()));

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = ExtraConstants.createIntent(
                                getContext(),
                                AdminEditPracticaActivity.class,
                                practicaBean);
                        startActivity(intent);
                    } catch (IOException ex) {
                        Log.e(LOG_CAT, "Failed to create intent for practica edit activity.", ex);
                    }
                }
            });

            return convertView;
        }

        private String formatDate(long timeInMillis) {
            Date date = new Date(timeInMillis);
            DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getActivity());
            return dateFormat.format(date);
        }
    }

    /**
     * A simple loader that loads a list of practicas.
     */
    private static class PracticaListLoader extends Loader<List<PracticaBean>> {

        private PracticaListLoader(Context context) {
            super(context);
        }
    }
}
