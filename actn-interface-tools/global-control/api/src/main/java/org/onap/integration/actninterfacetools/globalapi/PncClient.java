/*
 *   ============LICENSE_START=======================================================
 *   Actn Interface Tools
 *   ================================================================================
 *   Copyright (C) 2022 Huawei Canada Limited.
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

public interface PncClient {
    CustomerOtnTopology getNetworkTopology(String topologyId) throws Exception;
    void createOtnTunnel(CustomerOtnTunnel customerOtnTunnel) throws Exception;
    void updateOtnTunnel(CustomerOtnTunnel customerOtnTunnel, String otnName) throws Exception;
    CustomerOtnTunnel getOtnTunnel(String actnOtnTunnelId) throws Exception;
    void createEthService(CustomerEthService customerEthService) throws Exception;
    void updateEthService(CustomerEthService customerEthService) throws Exception;
    CustomerEthService getEthService(String actnEthServiceId) throws Exception;
    void deleteOtnTunnel(String actnOtnTunnelId) throws Exception;
    void deleteEthService(String actnEthServiceId) throws Exception;
}
