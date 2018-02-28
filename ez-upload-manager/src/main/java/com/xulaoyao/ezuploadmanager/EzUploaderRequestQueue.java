package com.xulaoyao.ezuploadmanager;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.security.InvalidParameterException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 上传队列
 * 核心处理部分
 * 理论上线程数 以 CPU 个数为最大值
 * EzUploaderRequestQueue
 * Created by renwoxing on 2018/2/28.
 */
public class EzUploaderRequestQueue {

    private static final String TAG = EzUploaderRequestQueue.class.getSimpleName();

    //上传请求对象集合
    private Set<EzUploaderRequest> mCurrentRequests = new HashSet<>();

    //上传队列
    private PriorityBlockingQueue<EzUploaderRequest> mUploaderQueue = new PriorityBlockingQueue<>();
    //上传处理器
    private EzUploaderDispatcher[] mUploadDispatchers;
    //回调
    private CallBackDelivery mDelivery;

    //获取队列 id
    private AtomicInteger mSequenceGenerator = new AtomicInteger();

    /**
     * Default constructor.
     */
    public EzUploaderRequestQueue() {
        initialize(new Handler(Looper.getMainLooper()));
    }

    /**
     * Creates the upload dispatchers workers pool.
     * <p>
     * Deprecated:
     */
    public EzUploaderRequestQueue(int threadPoolSize) {
        initialize(new Handler(Looper.getMainLooper()), threadPoolSize);
    }

    /**
     * Construct with provided callback handler.
     *
     * @param callbackHandler
     */
    public EzUploaderRequestQueue(Handler callbackHandler) throws InvalidParameterException {
        if (callbackHandler == null) {
            throw new InvalidParameterException("callbackHandler must not be null");
        }

        initialize(callbackHandler);
    }


    public void start() {
        stop();  //确保所有线程中的任务全部退出

        // 创建上传线程池
        for (int i = 0; i < mUploadDispatchers.length; i++) {
            EzUploaderDispatcher uploaderDispatcher = new EzUploaderDispatcher(mUploaderQueue, mDelivery);
            mUploadDispatchers[i] = uploaderDispatcher;
            uploaderDispatcher.start();
        }
    }


    // Package-Private methods.

    /**
     * Generates a upload id for the request and adds the upload request to the upload request queue for the dispatchers pool to act on immediately.
     *
     * @param request
     * @return uploadId
     */
    int add(EzUploaderRequest request) {
        int uploadId = getUploadId();
        // Tag the request as belonging to this queue and add it to the set of current requests.
        request.setUploaderRequestQueue(this);

        synchronized (mCurrentRequests) {
            mCurrentRequests.add(request);
        }

        // Process requests in the order they are added.
        request.setUploadId(uploadId);
        mUploaderQueue.add(request);

        return uploadId;
    }

    /**
     * Returns the current upload state for a upload request.
     *
     * @param uploadId
     * @return
     */
    int query(int uploadId) {
        synchronized (mCurrentRequests) {
            for (EzUploaderRequest request : mCurrentRequests) {
                if (request.getUploadId() == uploadId) {
                    return request.getUploadState();
                }
            }
        }
        return EzUploaderStatus.STATUS_NOT_FOUND;
    }

    /**
     * Cancel all the dispatchers in work and also stops the dispatchers.
     */
    void cancelAll() {

        synchronized (mCurrentRequests) {
            for (EzUploaderRequest request : mCurrentRequests) {
                request.cancel();
            }

            // Remove all the requests from the queue.
            mCurrentRequests.clear();
        }
    }

    /**
     * Cancel a particular upload in progress. Returns 1 if the upload Id is found else returns 0.
     *
     * @param uploadId
     * @return int
     */
    int cancel(int uploadId) {
        synchronized (mCurrentRequests) {
            for (EzUploaderRequest request : mCurrentRequests) {
                if (request.getUploadId() == uploadId) {
                    request.cancel();
                    return 1;
                }
            }
        }

        return 0;
    }

    /**
     * Pause a particular upload in progress.
     *
     * @param uploadId - selected upload request Id
     * @return It will return 1 if the upload Id is found else returns 0.
     */
    int pause(int uploadId) {
        checkResumableUploadEnabled(uploadId);
        return this.cancel(uploadId);
    }

    /**
     * Pause all the dispatchers in work and also cancel and stops the dispatchers.
     */
    void pauseAll() {
        checkResumableUploadEnabled(-1); // Error code -1 handle for cancelAll()
        this.cancelAll();
    }


    /**
     * This is called by methods that want to throw an exception if the {@link EzUploaderRequest}
     * hasn't enable isResumable feature.
     */
    private void checkResumableUploadEnabled(int uploadId) {
        synchronized (mCurrentRequests) {
            for (EzUploaderRequest request : mCurrentRequests) {
                if (uploadId == -1 && !request.isResumable()) {
                    Log.e(TAG, String.format(Locale.getDefault(), "This request has not enabled resume feature hence request will be cancelled. Request Id: %d", request.getUploadId()));
                } else if ((request.getUploadId() == uploadId && !request.isResumable())) {
                    throw new IllegalStateException("You cannot pause the upload, unless you have enabled Resume feature in EzUploaderRequest.");
                } else {
                    //ignored, It can not be a scenario to happen.
                }
            }
        }
    }

    public void finish(EzUploaderRequest request) {
        if (mCurrentRequests != null) {
            synchronized (mCurrentRequests) {
                mCurrentRequests.remove(request);
            }
        }
    }


    /**
     * Cancels all the pending & running requests and releases all the dispatchers.
     */
    void release() {
        if (mCurrentRequests != null) {
            synchronized (mCurrentRequests) {
                mCurrentRequests.clear();
                mCurrentRequests = null;
            }
        }

        if (mUploaderQueue != null) {
            mUploaderQueue = null;
        }

        if (mUploadDispatchers != null) {
            stop();

            for (int i = 0; i < mUploadDispatchers.length; i++) {
                mUploadDispatchers[i] = null;
            }
            mUploadDispatchers = null;
        }

    }


    // Private methods.

    /**
     * Perform construction.
     *
     * @param callbackHandler
     */
    private void initialize(Handler callbackHandler) {
        int processors = Runtime.getRuntime().availableProcessors();
        mUploadDispatchers = new EzUploaderDispatcher[processors];
        mDelivery = new CallBackDelivery(callbackHandler);
    }

    /**
     * Perform construction with custom thread pool size.
     */
    private void initialize(Handler callbackHandler, int threadPoolSize) {
        mUploadDispatchers = new EzUploaderDispatcher[threadPoolSize];
        mDelivery = new CallBackDelivery(callbackHandler);
    }

    /**
     * Stops upload dispatchers.
     */
    private void stop() {
        for (int i = 0; i < mUploadDispatchers.length; i++) {
            if (mUploadDispatchers[i] != null) {
                mUploadDispatchers[i].quit();
            }
        }
    }

    /**
     * Gets a upload sequence id.
     */
    private int getUploadId() {
        return mSequenceGenerator.incrementAndGet();
    }


    // 内部类

    /**
     * Delivery class to delivery the call back to call back registrar in main thread.
     */
    class CallBackDelivery {

        /**
         * Used for posting responses, typically to the main thread.
         */
        private final Executor mCallBackExecutor;

        /**
         * Constructor taking a handler to main thread.
         */
        public CallBackDelivery(final Handler handler) {
            // Make an Executor that just wraps the handler.
            mCallBackExecutor = new Executor() {
                @Override
                public void execute(Runnable command) {
                    handler.post(command);
                }
            };
        }

        public void postUploadComplete(final EzUploaderRequest request) {
            mCallBackExecutor.execute(new Runnable() {
                public void run() {
                    if (request.getUploaderResponseListener() != null) {
                        request.getUploaderResponseListener().onUploadComplete(request);
                    }
                }
            });
        }

        public void postUploadFailed(final EzUploaderRequest request, final int errorCode, final String errorMsg) {
            mCallBackExecutor.execute(new Runnable() {
                public void run() {
                    if (request.getUploaderResponseListener() != null) {
                        request.getUploaderResponseListener().onUploadFailed(request, errorCode, errorMsg);
                    }
                }
            });
        }

        public void postProgressUpdate(final EzUploaderRequest request, final long totalBytes, final long uploadBytes, final int progress) {
            mCallBackExecutor.execute(new Runnable() {
                public void run() {
                    if (request.getUploaderResponseListener() != null) {
                        request.getUploaderResponseListener().onProgress(request, totalBytes, uploadBytes, progress);
                    }
                }
            });
        }
    }
}
