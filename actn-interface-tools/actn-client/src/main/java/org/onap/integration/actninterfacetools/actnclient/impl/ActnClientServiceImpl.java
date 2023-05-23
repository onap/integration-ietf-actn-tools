/*
 *   ============LICENSE_START=======================================================
 *   Actn Interface Tools
 *   ================================================================================
 *   Copyright (C) 2023 Huawei Canada Limited.
 *   ================================================================================
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *   ============LICENSE_END=========================================================
 */
package org.onap.integration.actninterfacetools.actnclient.impl;

import com.google.auto.service.AutoService;
import org.onap.integration.actninterfacetools.globalapi.ActnClientService;
import org.onap.integration.actninterfacetools.globalapi.ActnDataConverter;
import org.onap.integration.actninterfacetools.globalapi.PncClient;
import org.onap.integration.actninterfacetools.protocol.restconf.PncInstance;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@AutoService(ActnClientService.class)
public class ActnClientServiceImpl implements ActnClientService {
    private ActnDataConverter actnDataConverter;

    private Map<PncInstance, PncClientInstance> pncClientMap = new ConcurrentHashMap();


    public ActnClientServiceImpl() {
    }
    public ActnClientServiceImpl(ActnDataConverter actnDataConverter) {
        this.actnDataConverter = actnDataConverter;
    }


    @Override
    public void registerDataConverter(ActnDataConverter converter) {
        this.actnDataConverter = converter;
    }

    @Override
    public PncClient getPncClient(PncInstance pncInstance) {
        if(pncClientMap.get(pncInstance)!=null){
            return pncClientMap.get(pncInstance);
        }else{
            PncClientInstance pncClientInstance = new PncClientInstance(pncInstance, actnDataConverter);
            this.pncClientMap.put(pncInstance, pncClientInstance);
            return pncClientInstance;
        }

    }
}
