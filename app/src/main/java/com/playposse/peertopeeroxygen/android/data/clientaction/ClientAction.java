package com.playposse.peertopeeroxygen.android.data.clientaction;

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
}
