package com.appspace.update.listener;

import com.appspace.update.entity.UpdateEntity;

/**
 * 异步解析的回调
 *
 * @author treexi
 * @since 2020-02-15 17:23
 */
public interface IUpdateParseCallback {

    /**
     * 解析结果
     *
     * @param updateEntity 版本更新信息实体
     */
    void onParseResult(UpdateEntity updateEntity);

}
