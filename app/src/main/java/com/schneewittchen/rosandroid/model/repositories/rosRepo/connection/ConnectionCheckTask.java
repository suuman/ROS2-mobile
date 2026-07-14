package com.schneewittchen.rosandroid.model.repositories.rosRepo.connection;

import android.os.Handler;
import android.os.Looper;

import com.schneewittchen.rosandroid.model.entities.MasterEntity;
import com.schneewittchen.rosandroid.utility.Utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Checks if the rosbridge server is reachable before a connection attempt.
 *
 * @author Nico Studt
 * @version 2.0.0
 * @created on 15.04.20
 * @updated on 12.07.2026 (ROS 2 migration, AsyncTask replaced by executor)
 */
public class ConnectionCheckTask {

    private static final int TIMEOUT_TIME = 2 * 1000;

    private final ConnectionListener listener;
    private final ExecutorService executor;
    private final Handler mainHandler;

    public ConnectionCheckTask(ConnectionListener listener) {
        this.listener = listener;
        this.executor = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    public void execute(MasterEntity masterEnt) {
        executor.execute(() -> {
            boolean success = Utils.isHostAvailable(masterEnt.ip, masterEnt.port, TIMEOUT_TIME);

            mainHandler.post(() -> {
                if (success)
                    listener.onSuccess();
                else
                    listener.onFailed();
            });

            executor.shutdown();
        });
    }
}
