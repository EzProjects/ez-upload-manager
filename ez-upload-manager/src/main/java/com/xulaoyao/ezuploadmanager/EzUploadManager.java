package com.xulaoyao.ezuploadmanager;

import android.os.Handler;

import java.security.InvalidParameterException;

/**
 * 上传管理器
 * EzUploadManager
 * Created by renwoxing on 2018/2/28.
 */
public class EzUploadManager implements IEzUploaderListener {


    private EzUploaderRequestQueue mUploaderRequestQueue;

    /**
     * 构造
     */
    public EzUploadManager() {
        mUploaderRequestQueue = new EzUploaderRequestQueue();
        mUploaderRequestQueue.start();
    }

    /**
     * Construct with provided callback handler
     *
     * @param callbackHandler - callback handler
     */
    public EzUploadManager(Handler callbackHandler) throws InvalidParameterException {
        mUploaderRequestQueue = new EzUploaderRequestQueue(callbackHandler);
        mUploaderRequestQueue.start();
    }

    /**
     * Constructor taking MAX THREAD POOL SIZE  Allows maximum of 4 threads.
     * Any number higher than four or less than one wont be respected.
     * <p>
     * Deprecated use Default Constructor. As the thread pool size will not respected anymore through this constructor.
     * Thread pool size is determined with the number of available processors on the device.
     **/
    public EzUploadManager(int threadPoolSize) {
        mUploaderRequestQueue = new EzUploaderRequestQueue(threadPoolSize);
        mUploaderRequestQueue.start();
    }

    @Override
    public int add(EzUploaderRequest request) throws IllegalArgumentException {
        checkReleased("add(...) called on a released UploadManager.");
        if (request == null) {
            throw new IllegalArgumentException("UploadRequest cannot be null");
        }
        return mUploaderRequestQueue.add(request);
    }

    @Override
    public int cancel(int uploadId) {
        checkReleased("cancel(...) called on a released UploadManager.");
        return mUploaderRequestQueue.cancel(uploadId);
    }

    @Override
    public void cancelAll() {
        checkReleased("cancelAll(...) called on a released UploadManager.");
        mUploaderRequestQueue.cancelAll();
    }

    @Override
    public int pause(int uploadId) {
        checkReleased("pause(...) called on a released UploadManager.");
        return mUploaderRequestQueue.pause(uploadId);
    }

    @Override
    public void pauseAll() {
        checkReleased("pauseAll(...) called on a released UploadManager.");
        mUploaderRequestQueue.pauseAll();
    }

    @Override
    public int query(int uploadId) {
        checkReleased("query(...) called on a released UploadManager.");
        return mUploaderRequestQueue.query(uploadId);
    }

    @Override
    public void release() {
        if (!isReleased()) {
            mUploaderRequestQueue.release();
            mUploaderRequestQueue = null;
        }
    }

    @Override
    public boolean isReleased() {
        return mUploaderRequestQueue == null;
    }

    /**
     * This is called by methods that want to throw an exception if the EzUploadManager
     * has already been released.
     */
    private void checkReleased(String errorMessage) {
        if (isReleased()) {
            throw new IllegalStateException(errorMessage);
        }
    }
}
