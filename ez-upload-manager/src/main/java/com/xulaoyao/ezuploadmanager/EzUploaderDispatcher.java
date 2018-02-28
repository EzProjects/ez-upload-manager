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

    private IEzUploaderExecute mUploadExecute;

    /**
     * Used to tell the dispatcher to die.
     */
    private volatile boolean mQuit = false;

    EzUploaderDispatcher(BlockingQueue<EzUploaderRequest> queue,
                         EzUploaderRequestQueue.CallBackDelivery delivery,
                         IEzUploaderExecute ezUploaderExecute) {
        this.mQueue = queue;
        this.mDelivery = delivery;
        this.mUploadExecute = ezUploaderExecute;
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
                updateUploadState(request, EzUploaderStatus.STATUS_STARTED);
                mUploadExecute.upload(request, new IEzUploaderResponseListener() {
                    @Override
                    public void onUploadComplete(EzUploaderRequest uploaderRequest) {
                        updateUploadComplete(uploaderRequest);
                    }

                    @Override
                    public void onUploadFailed(EzUploaderRequest uploaderRequest, int errorCode, String errorMessage) {
                        updateUploadFailed(uploaderRequest, errorCode, errorMessage);
                    }

                    @Override
                    public void onProgress(EzUploaderRequest uploaderRequest, long totalBytes, long uploadBytes, int progress) {
                        updateUploadProgress(uploaderRequest, totalBytes, uploadBytes, progress);
                    }
                });
            } catch (Exception e) {
                if (mQuit) {
                    if (request != null) {
                        request.finish();
                        // don't remove files that have been uploaded sucessfully.
                        if (request.getUploadState() != EzUploaderStatus.STATUS_SUCCESSFUL) {
                            updateUploadFailed(request, EzUploaderStatus.ERROR_UPLOAD_CANCELLED, "upload cancelled");
                        }
                    }
                    return;
                }
            }
        }
    }

    private void updateUploadState(EzUploaderRequest request, int state) {
        request.setUploadState(state);
    }

    private void updateUploadComplete(EzUploaderRequest request) {
        mDelivery.postUploadComplete(request);
        request.setUploadState(EzUploaderStatus.STATUS_SUCCESSFUL);
        request.finish();
    }

    private void updateUploadFailed(EzUploaderRequest request, int errorCode, String errorMsg) {
        request.setUploadState(EzUploaderStatus.STATUS_FAILED);
        mDelivery.postUploadFailed(request, errorCode, errorMsg);
        request.finish();
    }

    private void updateUploadProgress(EzUploaderRequest request, long totalBytes, long uploadBytes, int progress) {
        mDelivery.postProgressUpdate(request, totalBytes, uploadBytes, progress);
    }

    void quit() {
        mQuit = true;
        interrupt();
    }
}
