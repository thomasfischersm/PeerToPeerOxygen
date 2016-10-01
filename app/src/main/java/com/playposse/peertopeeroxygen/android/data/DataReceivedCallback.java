package com.playposse.peertopeeroxygen.android.data;

/**
 * A callback interface for activities to implement. The {@link #receiveData(DataRepository)} method is
 * called when the data is first loaded, when the data is refreshed, and when the callback is
 * first registered (if data is available).
 */
public interface DataReceivedCallback {

    /**
     * Called when data is available. It's the job of this method to switch to the UI thread.
     */
    void receiveData(DataRepository dataRepository);

    /**
     * Used to switch to the UI Thread of the actitivy implementing this interface.
     */
    void runOnUiThread(Runnable runnable);
}
