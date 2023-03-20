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
package org.onap.integration.actninterfacetools.sampleapp.mpiconverter.model;

import org.onap.integration.actninterfacetools.actnclient.api.CustomerEthService;

import java.util.List;

public class TsEthSrv extends CustomerEthService implements EthSrv{
    private List<EthSrvInstance> ethSrvInstances;

    public TsEthSrv(List<EthSrvInstance> ethSrvInstances) {
        this.ethSrvInstances = ethSrvInstances;
    }

    @Override
    public List<EthSrvInstance> getEthSrvInstances(){
        return this.ethSrvInstances;
    }
}
