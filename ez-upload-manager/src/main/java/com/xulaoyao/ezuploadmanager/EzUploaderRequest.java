package com.xulaoyao.ezuploadmanager;

/**
 * 上传对象
 * EzUploaderRequest
 * Created by renwoxing on 2018/2/28.
 */
public class EzUploaderRequest {


    /**
     * Tells the current upload state of this request
     */
    private int mUploadState;

    /**
     * upload key assigned to this request
     */
    private int mUploadId;

    /**
     * The file path resource that this request is to upload
     */
    private String mFilePath;

    /**
     * Whether or not this request has been canceled.
     */
    private boolean mCancelled = false;

    private boolean mDeleteDestinationFileOnFailure = true;

    private EzUploaderRequestQueue mRequestQueue;

    private IEzUploaderResponseListener mEzUploaderResponseListener;

    private boolean isUploadResumable = false;
    /**
     * 传递一些有用的对象
     * 比如文件名,key etc.
     */
    private Object mUploadContext;

    public EzUploaderRequest(String filePath) {
        if (filePath == null) {
            throw new NullPointerException();
        }
        this.mFilePath = filePath;
        mUploadState = EzUploaderStatus.STATUS_PENDING;

//        String scheme = uri.getScheme();
//        if (scheme == null || (!scheme.equals("http") && !scheme.equals("https"))) {
//            throw new IllegalArgumentException("Can only upload HTTP/HTTPS URIs: " + uri);
//        }
//        mCustomHeader = new HashMap<>();
//        mDestinationURI = uri;
    }


    /**
     * Associates this request with the given queue. The request queue will be notified when this
     * request has finished.
     */
    void setUploaderRequestQueue(EzUploaderRequestQueue uploadQueue) {
        mRequestQueue = uploadQueue;
    }

//    public IEzUploaderRetryPolicy getRetryPolicy() {
//        return mRetryPolicy == null ? new EzUploaderDefaultRetryPolicy() : mRetryPolicy;
//    }
//
//    public EzUploaderRequest setRetryPolicy(IEzUploaderRetryPolicy mRetryPolicy) {
//        this.mRetryPolicy = mRetryPolicy;
//        return this;
//    }

    /**
     * Gets the upload id.
     *
     * @return the upload id
     */
    public final int getUploadId() {
        return mUploadId;
    }

    /**
     * Sets the upload Id of this request.  Used by {@link EzUploaderRequestQueue}.
     */
    final void setUploadId(int uploadId) {
        mUploadId = uploadId;
    }

    int getUploadState() {
        return mUploadState;
    }

    void setUploadState(int mUploadState) {
        this.mUploadState = mUploadState;
    }

    public Object getUploadContext() {
        return mUploadContext;
    }

    public EzUploaderRequest setUploadContext(Object uploadContext) {
        mUploadContext = uploadContext;
        return this;
    }


    /**
     * Gets the status listener. For internal use.
     *
     * @return the status listener
     */
    IEzUploaderResponseListener getUploaderResponseListener() {
        return mEzUploaderResponseListener;
    }

    /**
     * Sets the status listener for this upload request. upload manager sends progress,
     * failure and completion updates to this listener for this upload request.
     *
     * @param uploaderResponseListener the status listener for this upload
     */
    public EzUploaderRequest setUploaderResponseListener(IEzUploaderResponseListener uploaderResponseListener) {
        mEzUploaderResponseListener = uploaderResponseListener;
        return this;
    }

    public String getmFilePath() {
        return mFilePath;
    }

    public void setmFilePath(String mFilePath) {
        this.mFilePath = mFilePath;
    }

    public boolean getDeleteDestinationFileOnFailure() {
        return mDeleteDestinationFileOnFailure;
    }

    /**
     * It marks the request with resumable feature and It is an optional feature
     *
     * @param isUploadResumable - It enables resumable feature for this request
     * @return - current {@link EzUploaderRequest}
     */
    public EzUploaderRequest setUploadResumable(boolean isUploadResumable) {
        this.isUploadResumable = isUploadResumable;
        setDeleteDestinationFileOnFailure(false); // If resumable feature enabled, upload file should not be deleted.
        return this;
    }

    public boolean isResumable() {
        return isUploadResumable;
    }

    /**
     * Set if destination file should be deleted on upload failure.
     * Use is optional: default is to delete.
     */
    public EzUploaderRequest setDeleteDestinationFileOnFailure(boolean deleteOnFailure) {
        this.mDeleteDestinationFileOnFailure = deleteOnFailure;
        return this;
    }

    /**
     * Mark this request as canceled.  No callback will be delivered.
     */
    public void cancel() {
        mCancelled = true;
    }

    //Package-private methods.

    /**
     * Returns true if this request has been canceled.
     */
    public boolean isCancelled() {
        return mCancelled;
    }


    /**
     * Marked the request as canceled is aborted.
     */
    public void abortCancel() {
        mCancelled = false;
    }

    void finish() {
        mRequestQueue.finish(this);
    }

}
