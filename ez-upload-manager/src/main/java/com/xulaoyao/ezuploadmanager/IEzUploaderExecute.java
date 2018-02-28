package com.xulaoyao.ezuploadmanager;

/**
 * 上传具体操作接口
 * 需要外部调用时实现
 * IEzUploaderExecute
 * Created by renwoxing on 2018/2/28.
 */
public interface IEzUploaderExecute {
    void upload(EzUploaderRequest request, IEzUploaderResponseListener responseListener);
}
