package com.xulaoyao.ezuploadmanager;

/**
 * 上传接口管理
 * IEzUploaderListener
 * Created by renwoxing on 2018/2/28.
 */
public interface IEzUploaderListener {

    /**
     * 新增上传队列
     * @param request
     * @return
     */
    int add(EzUploaderRequest request);

    /**
     * 放弃上传
     *
     * @param uploadId
     * @return
     */
    int cancel(int uploadId);

    /**
     * 放弃所有上传队列
     */
    void cancelAll();

    /**
     * 暂停上传
     *
     * @param uploadId
     * @return
     */
    int pause(int uploadId);

    /**
     * 暂停所有上传
     */
    void pauseAll();

    /**
     * 请求
     * 返回当前上传状态值
     */
    int query(int uploadId);

    /**
     * 释放
     */
    void release();

    /**
     * @return
     */
    boolean isReleased();
}
