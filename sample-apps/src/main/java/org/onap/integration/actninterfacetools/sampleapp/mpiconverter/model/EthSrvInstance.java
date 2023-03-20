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

import java.util.List;

public class EthSrvInstance {
    private final String ethSrvName;
    private final String ethSrvDescr;
    private final long providerId;
    private final long clientId;
    private final String topologyId;
    private final List<TsTunnel> otnTunnels;
    private final List<EthSrvEndPoint> ethSrvEndPoints;

    public EthSrvInstance(String ethSrvName, String ethSrvDescr, long providerId, long clientId, String topologyId, List<TsTunnel> otnTunnels, List<EthSrvEndPoint> ethSrvEndPoints) {
        this.ethSrvName = ethSrvName;
        this.ethSrvDescr = ethSrvDescr;
        this.providerId = providerId;
        this.clientId = clientId;
        this.topologyId = topologyId;
        this.otnTunnels = otnTunnels;
        this.ethSrvEndPoints = ethSrvEndPoints;
    }
    public String getEthSrvName(){
        return this.ethSrvName;
    }
    public String getEthSrvDescr(){
        return this.ethSrvDescr;
    }
    public long getProviderId(){
        return this.providerId;
    }
    public long getClientId(){
        return this.clientId;
    }
    public String getTopologyId(){
        return this.topologyId;
    }
    public List<TsTunnel> getOtnTunnels(){
        return this.otnTunnels;
    }
    public List<EthSrvEndPoint> getEthSrvEndPoints(){
        return this.ethSrvEndPoints;
    }
}
