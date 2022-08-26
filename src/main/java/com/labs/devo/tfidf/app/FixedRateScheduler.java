package com.labs.devo.tfidf.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

public class FixedRateScheduler {
    private static final Logger logger = LoggerFactory.getLogger(FixedRateScheduler.class);
    private static final int THREAD_POOL_SIZE = 2;
    private static final int INITIAL_DELAY = 0;

    private final ScheduledExecutorService executor;
    private final int rate;

    public FixedRateScheduler(int rate) {
        this.executor = Executors.newScheduledThreadPool(THREAD_POOL_SIZE);
        this.rate = rate;
    }

    public void scheduleTask(Runnable taskBody) {
        ScheduledFuture newOngoingTask = executor.scheduleAtFixedRate(
                taskBody,
                INITIAL_DELAY,
                rate,
                TimeUnit.SECONDS);
        Executors.newSingleThreadExecutor().execute(() -> checkResult(newOngoingTask));
    }

    private void checkResult(ScheduledFuture newOngoingTask) {
        try {
            newOngoingTask.get();
        } catch (Exception e) {
            logger.error("Error runnning scheduled task", e);
        }
    }
}
