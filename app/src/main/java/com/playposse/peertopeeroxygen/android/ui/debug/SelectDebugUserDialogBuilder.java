package com.playposse.peertopeeroxygen.android.ui.debug;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.playposse.peertopeeroxygen.android.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A builder for a dialog with a spinner. The spinner shows a list of debug users to pick from.
 */
public class SelectDebugUserDialogBuilder {

    private static Map<String, Long> userMap = new HashMap<String, Long>() {{
        put("Jeremy Doe", Long.valueOf("5639221461123072"));
        put("April Doe", Long.valueOf("5706163895140352"));
        put("Brad Doe", Long.valueOf("5757487680585728"));
        put("Sharon Doe", Long.valueOf("5712536552865792"));
        put("Thomas Fischer", Long.valueOf("5685265389584384"));
        put("Mitra Martin", Long.valueOf("5730082031140864"));
        put("Liz Doe", Long.valueOf("6329546185900032"));
    }};

    private static List<String> userNames = new ArrayList<>(userMap.keySet());

    public static void build(Context context, final DebugUserPickerDialogCallback callback) {

        Collections.sort(userNames);
        ArrayAdapter<String> userNameAdapter =
                new ArrayAdapter<>(context, R.layout.list_item_text_view, userNames);
        final Spinner userSpinner = new Spinner(context);
        userSpinner.setAdapter(userNameAdapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(userSpinner);

        builder.setPositiveButton(
                R.string.confirm_dialog_continue_button,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                        String userName = userSpinner.getSelectedItem().toString();
                        Long userId = userMap.get(userName);
                        callback.onPickedDebugUser(userId);
                    }
                });

        builder.setNegativeButton(
                R.string.cancel_button_label,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.cancel();
                    }
                });

        builder.show();
    }

    public interface DebugUserPickerDialogCallback {
        void onPickedDebugUser(long userId);
    }
}
