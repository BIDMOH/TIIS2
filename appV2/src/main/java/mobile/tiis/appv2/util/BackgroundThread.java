package mobile.tiis.appv2.util;

import android.os.HandlerThread;
import static android.os.Process.THREAD_PRIORITY_BACKGROUND;

public class BackgroundThread extends HandlerThread {
        public BackgroundThread() {
            super("SchedulerSample-BackgroundThread", THREAD_PRIORITY_BACKGROUND);
        }
}

//Observable.defer(new Func0<Observable<Boolean>>() {
//@Override
//public Observable<Boolean> call() {
//        // Do some long running operation
//
//        return Observable.just(true);
//        }
//        }).subscribeOn(AndroidSchedulers.from(backgroundLooper))
//        // Be notified on the main thread
//        .observeOn(AndroidSchedulers.mainThread())
//        .subscribe(new Subscriber<Boolean>() {
//@Override
//public void onCompleted() {
//        Log.d(TAG, "onCompleted()");
//
//
//        }
//
//@Override
//public void onError(Throwable e) {
//        Log.e(TAG, "onError()", e);
//        }
//
//@Override
//public void onNext(Boolean string) {
//        Log.d(TAG, "onNext(" + string + ")");
//        }
//        });