package com.playposse.peertopeeroxygen.android.firebase.actions;

import com.google.firebase.messaging.RemoteMessage;

/**
 * A Firebase client action that receives metadata of other attendees who check in and graduate
 * missions.
 */
public class PracticaUserUpdateClientAction extends FirebaseClientAction {

    public PracticaUserUpdateClientAction(RemoteMessage remoteMessage) {
        super(remoteMessage);
    }

    @Override
    protected void execute(RemoteMessage remoteMessage) {

    }
}
