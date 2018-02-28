package com.xulaoyao.ezuploadmanager;

/**
 * EzUploaderStatus
 * Created by renwoxing on 2018/2/28.
 */
public interface EzUploaderStatus {

    /**
     * EzUploaderStatus when the upload is currently pending.
     */
    int STATUS_PENDING = 1 << 0;

    /**
     * EzUploaderStatus when the upload is currently pending.
     */
    int STATUS_STARTED = 1 << 1;

    /**
     * EzUploaderStatus when the upload network call is connecting to destination.
     */
    int STATUS_CONNECTING = 1 << 2;

    /**
     * EzUploaderStatus when the upload is currently running.
     */
    int STATUS_RUNNING = 1 << 3;


    /**
     * EzUploaderStatus when the upload has successfully completed.
     */
    int STATUS_SUCCESSFUL = 1 << 4;

    /**
     * EzUploaderStatus when the upload has failed.
     */
    int STATUS_FAILED = 1 << 5;

    /**
     * EzUploaderStatus when the upload has failed due to broken url or invalid upload url
     */
    int STATUS_NOT_FOUND = 1 << 6;

    /**
     * EzUploaderStatus when the upload is attempted for retry due to connection timeouts.
     */
    int STATUS_RETRYING = 1 << 7;

    /**
     * Error code when writing upload content to the destination file.
     */
    int ERROR_FILE_ERROR = 2001;


    /**
     * Error code when upload is cancelled.
     */
    int ERROR_UPLOAD_CANCELLED = 2002;
    
    
}
