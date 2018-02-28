package com.xulaoyao.ezuploadmanager;

import android.os.Process;
import android.util.Log;

import java.util.concurrent.BlockingQueue;

/**
 * 上传 操作类
 * EzUploaderDispatcher
 * Created by renwoxing on 2018/2/28.
 */
class EzUploaderDispatcher extends Thread {

    private static final String TAG = EzUploaderDispatcher.class.getSimpleName();

    private final BlockingQueue<EzUploaderRequest> mQueue;

    private EzUploaderRequestQueue.CallBackDelivery mDelivery;

    /**
     * Used to tell the dispatcher to die.
     */
    private volatile boolean mQuit = false;

    EzUploaderDispatcher(BlockingQueue<EzUploaderRequest> queue,
                         EzUploaderRequestQueue.CallBackDelivery delivery) {
        this.mQueue = queue;
        this.mDelivery = delivery;
    }


    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        while (true) {
            EzUploaderRequest request = null;
            try {
                //取出上传队列中的第一条对象
                request = mQueue.take();
                Log.d(TAG, "upload initiated for " + request.getUploadId());
            } catch (Exception e) {
                if (mQuit) {
                    if (request != null) {
                        request.finish();
                    }
                    return;
                }
            }
        }
    }


    void quit() {
        mQuit = true;
        interrupt();
    }
}
