package mobile.tiis.appv2.util;

import android.os.HandlerThread;
import static android.os.Process.THREAD_PRIORITY_BACKGROUND;

public class BackgroundThread extends HandlerThread {
        public BackgroundThread() {
            super("SchedulerSample-BackgroundThread", THREAD_PRIORITY_BACKGROUND);
        }
}