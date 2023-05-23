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
package org.onap.integration.actninterfacetools.globalapi;


import org.onosproject.yang.runtime.impl.DefaultModelConverter;
import org.onosproject.yang.runtime.impl.DefaultYangModelRegistry;
import org.onosproject.yang.runtime.impl.DefaultYangRuntimeHandler;
import org.onosproject.yang.runtime.impl.DefaultYangSerializerRegistry;
import org.onosproject.yang.runtime.impl.DefaultSchemaContextProvider;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.ServiceLoader;

public interface GlobalService {
    String getServiceName();
    void initialize() throws URISyntaxException;
    ActnClientService getActnClientService(ActnDataConverter actnDataConverter) throws URISyntaxException;
    DefaultYangModelRegistry getModelRegistry();
    DefaultYangSerializerRegistry getSerializerRegistry();
    DefaultYangRuntimeHandler getRuntimeService();
    DefaultModelConverter getModelConverter();
    DefaultSchemaContextProvider getSchemaContextProvider();

    Map<String, ClassLoader> getClassLoaders();
    static ActnClientService getActnClientSvc(ActnDataConverter actnDataConverter) throws URISyntaxException {
        ServiceLoader<GlobalService> serviceLoader = ServiceLoader.load(GlobalService.class);
        GlobalService globalService = serviceLoader.iterator().next();
        return globalService.getActnClientService(actnDataConverter);
    }

}
