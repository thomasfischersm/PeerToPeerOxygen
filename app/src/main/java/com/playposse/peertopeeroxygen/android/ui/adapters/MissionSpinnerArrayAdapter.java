package com.playposse.peertopeeroxygen.android.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionBean;

import java.util.List;

/**
 * {@link ArrayAdapter} for the spinner to select a mission.
 */
public class MissionSpinnerArrayAdapter
        extends ArrayAdapter<MissionBean>
        implements SpinnerAdapter {

    public MissionSpinnerArrayAdapter(
            Context context,
            int resource,
            List<MissionBean> missionBeans) {

        super(context, resource, missionBeans);

        setDropDownViewResource(R.layout.list_item_text_view); // remove!
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_text_view, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.textView.setText(getItem(position).getName());
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    private static class ViewHolder {
        private TextView textView;
    }
}
