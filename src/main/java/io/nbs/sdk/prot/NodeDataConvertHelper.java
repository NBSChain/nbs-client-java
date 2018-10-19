package io.nbs.sdk.prot;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import io.ipfs.api.JSONParser;
import io.nbs.sdk.beans.NodeBase;

import java.util.Map;

/**
 * Copyright © 2015-2020 NBSChain Holdings Limited.
 * All rights reserved.
 *
 * @project nbs-client4j
 * <p>
 * Author   : lanbery
 * Created  : 2018/10/19
 */
public class NodeDataConvertHelper {

    /**
     * @author      : lanbery
     * @Datetime    : 2018/10/19
     * @Description  :
     *
     */
    public static NodeBase convertFormID(Map data){
        if(data==null)return null;
        String json = JSONParser.toString(data);
        return JSON.parseObject(json,new TypeReference<NodeBase>(){});
    }
}
