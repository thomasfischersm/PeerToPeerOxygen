package com.playposse.peertopeeroxygen.android.data.clientaction;

import com.playposse.peertopeeroxygen.android.data.DataRepository;

/**
 * A base class for client actions to implement. It provides useful methods.
 */
public abstract class ClientAction {

    private final BinderForActions binder;

    public ClientAction(BinderForActions binder) {
        this.binder = binder;
    }

    protected BinderForActions getBinder() {
        return binder;
    }

    protected DataRepository getDataRepository() {
        return binder.getDataRepository();
    }
}
