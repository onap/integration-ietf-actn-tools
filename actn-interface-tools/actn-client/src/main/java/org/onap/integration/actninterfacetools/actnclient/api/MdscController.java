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

package org.onap.integration.actninterfacetools.actnclient.api;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.onosproject.yang.gen.v11.ietfnetworkstate.rev20180226.ietfnetworkstate.networks.DefaultNetwork;
import org.onosproject.yang.gen.v11.ietfte.rev20210220.ietfte.tunnelsgrouping.tunnels.DefaultTunnel;

import java.util.concurrent.Future;

public interface MdscController {

    /**
     * Retrieve network topology from network controller, return java pojo.
     * @param networkId
     * @return
     */
    DefaultNetwork getNetworkTopology(String networkId);

    /**
     * Retrieve network topology from network controller, return json representation.
     * @param networkId
     * @return
     */
    ObjectNode getNetworkTopologyAsJson(String networkId);

    /**
     * Retrieve otn tunnel info and status, return java pojo.
     * @param tunnelId
     * @return
     */
    DefaultTunnel getOtnTunnel(String tunnelId);

    /**
     * Retrieve otn tunnel info and status, return json representation.
     * @param tunnelId
     * @return
     */
    ObjectNode getOtnTunnelAsJson(String tunnelId);

    /**
     * Create otn tunnel
     * @param defaultTunnel
     * @return
     */
    Future<OssMessage> createOtnTunnel(DefaultTunnel defaultTunnel);

    /**
     * Create OTN TE tunnel on the phsical network
     * @param tunnelObject OTN Tunnel configuration encoded in JSON
     * @return
     */
    Future<OssMessage> createOtnTunnel(ObjectNode tunnelObject);
}
