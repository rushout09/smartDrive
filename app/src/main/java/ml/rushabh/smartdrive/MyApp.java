package ml.rushabh.smartdrive;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //To retain data on OS reboot and across Application Lifecycle.
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

    }
}
