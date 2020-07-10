/*
 * Copyright 2019 The FATE Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.webank.ai.fate.serving.federatedml.model;

import com.webank.ai.fate.api.networking.proxy.Proxy;
import com.webank.ai.fate.serving.common.cache.Cache;
import com.webank.ai.fate.serving.common.model.LocalInferenceAware;
import com.webank.ai.fate.serving.common.rpc.core.ErrorMessageUtil;
import com.webank.ai.fate.serving.common.rpc.core.FederatedRpcInvoker;
import com.webank.ai.fate.serving.core.bean.Dict;
import com.webank.ai.fate.serving.core.constant.StatusCode;
import com.webank.ai.fate.serving.core.utils.ProtobufUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;

public abstract class BaseComponent implements LocalInferenceAware {

    protected static final int OK = 0;
    protected static final int UNKNOWNERROR = 1;
    protected static final int PARAMERROR = 2;
    protected static final int ILLEGALDATA = 3;
    protected static final int NOMODEL = 4;
    protected static final int NOTME = 5;
    protected static final int FEDERATEDERROR = 6;
    protected static final int TIMEOUT = -1;
    protected static final int NOFILE = -2;
    protected static final int NETWORKERROR = -3;
    protected static final int IOERROR = -4;
    protected static final int RUNTIMEERROR = -5;
    private static final Logger logger = LoggerFactory.getLogger(BaseComponent.class);
    static ForkJoinPool forkJoinPool = new ForkJoinPool();
    protected String componentName;
    protected String shortName;
    protected int index;
    protected FederatedRpcInvoker<Proxy.Packet> federatedRpcInvoker;
    protected Cache cache;

    public abstract int initModel(byte[] protoMeta, byte[] protoParam);

    protected <T> T parseModel(com.google.protobuf.Parser<T> protoParser, byte[] protoString) throws com.google.protobuf.InvalidProtocolBufferException {
        return ProtobufUtils.parseProtoObject(protoParser, protoString);
    }

    protected Map<String, Object> handleRemoteReturnData(Map<String, Object> hostData) {
        Map<String, Object> result = new HashMap<>(8);
        result.put(Dict.RET_CODE, StatusCode.SUCCESS);
        hostData.forEach((partId, partyDataObject) -> {
            Map partyData = (Map) partyDataObject;
            result.put(Dict.MESSAGE, partyData.get(Dict.MESSAGE));
            if (partyData.get(Dict.RET_CODE) != null && !StatusCode.SUCCESS.equals(partyData.get(Dict.RET_CODE))) {
                String remoteCode = partyData.get(Dict.RET_CODE).toString();
                String remoteMsg = partyData.get(Dict.MESSAGE) != null ? partyData.get(Dict.MESSAGE).toString() : "";
                String errorMsg = ErrorMessageUtil.buildRemoteRpcErrorMsg(remoteCode, remoteMsg);
                String retcode = ErrorMessageUtil.transformRemoteErrorCode(remoteCode);
                result.put(Dict.RET_CODE, retcode);
                result.put(Dict.MESSAGE, errorMsg);
            }
        });
        return result;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public FederatedRpcInvoker getFederatedRpcInvoker() {
        return federatedRpcInvoker;
    }

    public void setFederatedRpcInvoker(FederatedRpcInvoker federatedRpcInvoker) {
        this.federatedRpcInvoker = federatedRpcInvoker;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Cache getCache() {
        return cache;
    }

    public void setCache(Cache cache) {
        this.cache = cache;
    }

}
