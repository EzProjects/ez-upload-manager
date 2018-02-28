package com.xulaoyao.ezuploadmanager;

/**
 * 单个文件上传回调
 * IEzUploaderResponseListener
 * Created by renwoxing on 2018/2/28.
 */
public interface IEzUploaderResponseListener {

    /**
     * This method is invoked when upload is complete.
     *
     * @param uploaderRequest the upload request provided by the client
     */
    void onUploadComplete(EzUploaderRequest uploaderRequest);


    /**
     * This method is invoked when upload has failed.
     *
     * @param uploaderRequest the upload request provided by the client
     * @param errorCode       the upload error code
     * @param errorMessage    the error message
     */
    void onUploadFailed(EzUploaderRequest uploaderRequest, int errorCode, String errorMessage);

    /**
     * This method is invoked on a progress update.
     *
     * @param uploaderRequest the upload request provided by the client
     * @param totalBytes      the total bytes
     * @param uploadBytes     bytes uploaded till now
     * @param progress        the progress of upload
     */
    void onProgress(EzUploaderRequest uploaderRequest, long totalBytes, long uploadBytes, int progress);
}
