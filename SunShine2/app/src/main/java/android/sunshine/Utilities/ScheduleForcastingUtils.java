package android.sunshine.Utilities;


import android.content.Context;
import android.sunshine.Services.WeatherForcastFirebaseJobService;
import android.support.annotation.NonNull;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

public class ScheduleForcastingUtils {
    private final static int INTERVAL_HOURS = 3;
    private final static int INTERVAL_SECONDS =
            (int) TimeUnit.HOURS.toSeconds(INTERVAL_HOURS);
    private final static int SYNC_FLEXTIME_SECONDS = INTERVAL_SECONDS / 3;

    private final static String JOB_TAG = "job-scheduling";

    private static boolean job_initialized;

    synchronized public static void schedule_forcasting(@NonNull final Context context){
        if (job_initialized)
            return;

        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);
        Job job_constraint = dispatcher.newJobBuilder()
                .setService(WeatherForcastFirebaseJobService.class)
                .setTag(JOB_TAG)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(
                        INTERVAL_SECONDS,
                        INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS
                ))
                .setReplaceCurrent(true)
                .build();
        dispatcher.schedule(job_constraint);
        job_initialized = true;
    }
}
