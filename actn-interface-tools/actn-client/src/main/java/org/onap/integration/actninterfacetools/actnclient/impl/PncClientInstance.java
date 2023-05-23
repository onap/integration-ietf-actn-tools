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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.auto.service.AutoService;
import org.onap.integration.actninterfacetools.globalapi.*;
import org.onap.integration.actninterfacetools.protocol.restconf.PncInstance;
import org.onap.integration.actninterfacetools.protocol.restconf.RestConfSBController;
import org.onap.integration.actninterfacetools.protocol.restconf.ctl.RestConfSBControllerImpl;
import org.onap.integration.actninterfacetools.yangutils.CodecConverter;
import org.onap.integration.actninterfacetools.yangutils.YangToolsUtil;
import org.onosproject.yang.gen.v11.ietfethtranservice.rev20210111.ietfethtranservice.DefaultEthtSvc;
import org.onosproject.yang.gen.v11.ietfethtranservice.rev20210111.ietfethtranservice.ethtsvc.DefaultEthtSvcInstances;
import org.onosproject.yang.gen.v11.ietfethtranservice.rev20210111.ietfethtranservice.ethtsvc.EthtSvcInstances;
import org.onosproject.yang.gen.v11.ietfnetwork.rev20180226.ietfnetwork.DefaultNetworks;
import org.onosproject.yang.gen.v11.ietfnetwork.rev20180226.ietfnetwork.NetworkId;
import org.onosproject.yang.gen.v11.ietfnetwork.rev20180226.ietfnetwork.networks.Network;
import org.onosproject.yang.gen.v11.ietfte.rev20210220.ietfte.tunnelsgrouping.tunnels.DefaultTunnel;
import org.onosproject.yang.model.DataNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.onap.integration.actninterfacetools.yangutils.YangToolsUtil.*;
@AutoService(PncClient.class)
public class PncClientInstance implements PncClient {
    private static final Logger log = LoggerFactory.getLogger(PncClientInstance.class);

    private PncInstance pncInstance;
    private ActnDataConverter actnDataConverter;
    private RestConfSBController restConfSBController;

    public PncClientInstance(PncInstance pncInstance, ActnDataConverter actnDataConverter) {
        this.pncInstance = pncInstance;
        this.actnDataConverter = actnDataConverter;
        this.restConfSBController = RestConfSBControllerImpl.getRestConfClient();
        this.restConfSBController.addPncInstance(this.pncInstance);
    }



    public CustomerOtnTopology getNetworkTopology(String topologyId) throws Exception {

        ObjectNode json = restConfSBController.get(pncInstance.pncId(), "ietf-network:networks", "json");
        String s = json.toString();
        String s1 = s.replaceAll("classify-c-vlan", "ietf-eth-tran-types:classify-c-vlan");
        String s2 = s1.replaceAll("classify-s-vlan", "ietf-eth-tran-types:classify-s-vlan");
        ObjectNode jsonNode = new ObjectMapper().readTree(s2).deepCopy();

        DefaultNetworks yangNetworks = YangToolsUtil.convertJsonToYangObj("/", jsonNode, DefaultNetworks.class);

        NetworkId networkId = NetworkId.fromString(topologyId);
        List<Network> networkList = yangNetworks.network();

        Network networkSelected = null;
        if(networkList!=null && !networkList.isEmpty()){
            for(Network network : networkList){
                if(network.networkId().equals(networkId)){
                    networkSelected = network;
                    break;
                }
            }
        }
        CustomerOtnTopology ct = actnDataConverter.convertActnOtnTopology(networkSelected);
        return ct;

    }
    public void createOtnTunnel(CustomerOtnTunnel customerOtnTunnel) throws Exception {
        DefaultTunnel actnOtnTunnel = actnDataConverter.convertActnOtnTunnel(customerOtnTunnel);
        ObjectNode jsonNodes = convertYangTeTunnel2Json(actnOtnTunnel);
        String s = jsonNodes.toString();
        String s1 = s.replaceAll("gfp-n", "ietf-otn-tunnel:gfp-n");
        String s2 = s1.replaceAll("odu-type", "ietf-otn-tunnel:odu-type");
        ObjectNode jsonNode = new ObjectMapper().readTree(s2).deepCopy();
        boolean result = restConfSBController.post(pncInstance.pncId(), "ietf-te:te/tunnels", jsonNode, "json");
        if(!result){
            log.warn("Failed to create otn tunnel");
        }else{
            log.info("Create otn tunnel successfully");
        }

    }

    public void updateOtnTunnel(CustomerOtnTunnel customerOtnTunnel, String otnName) throws Exception {
        DefaultTunnel actnOtnTunnel = actnDataConverter.convertActnOtnTunnel(customerOtnTunnel);
        ObjectNode jsonNodes = convertYangTeTunnel2Json(actnOtnTunnel);
        boolean result = restConfSBController.patch(pncInstance.pncId(), "ietf-te:te/tunnels/tunnel="+otnName, jsonNodes, "json");
        if(!result){
            log.warn("Failed to update otn tunnel");
        }else{
            log.info("Update otn tunnel successfully");
        }

    }
    public CustomerOtnTunnel getOtnTunnel(String actnOtnTunnelId) throws Exception {
        ObjectNode jsonNode = restConfSBController.get(pncInstance.pncId(), "ietf-te:te/tunnels/tunnel="+actnOtnTunnelId, "json");
        DefaultTunnel actnOtnTunnel = YangToolsUtil.convertJsonToYangObj("ietf-te:te/tunnels", jsonNode, DefaultTunnel.class);
        CustomerOtnTunnel customerOtnTunnel = actnDataConverter.convertActnOtnTunnel(actnOtnTunnel);
        return customerOtnTunnel;
    }




    public void createEthService(CustomerEthService customerEthService) throws Exception {
        DefaultEthtSvc actnEthService = actnDataConverter.convertActnEthService(customerEthService);
        ObjectNode jsonNodes = convertYangEthSvc2Json(actnEthService);
        boolean result = restConfSBController.patch(pncInstance.pncId(), "ietf-eth-tran-service:etht-svc/etht-svc-instances", jsonNodes, "json");
        if(!result){
            log.warn("Failed to create ethernet service");
        }else{
            log.info("Create ethernet service successfully");
        }

    }
    public void updateEthService(CustomerEthService customerEthService) throws Exception {
        DefaultEthtSvc actnEthService = actnDataConverter.convertActnEthService(customerEthService);
        ObjectNode jsonNodes = convertYangEthSvc2Json(actnEthService);
        boolean result = restConfSBController.patch(pncInstance.pncId(), "ietf-eth-tran-service:etht-svc/etht-svc-instances", jsonNodes, "json");
        if(!result){
            log.warn("Failed to update ethernet service");
        }else{
            log.info("Update ethernet service successfully");
        }

    }


    public CustomerEthService getEthService(String actnEthServiceId) throws Exception {
        ObjectNode jsonNode = restConfSBController.get(pncInstance.pncId(), "ietf-eth-tran-service:etht-svc/etht-svc-instances="+actnEthServiceId, "json");
        DefaultEthtSvcInstances actnEthSvcInstance = YangToolsUtil.convertJsonToYangObj("ietf-eth-tran-service:etht-svc", jsonNode, DefaultEthtSvcInstances.class);
        DefaultEthtSvc actnEthSvc = new DefaultEthtSvc();
        actnEthSvc.addToEthtSvcInstances(actnEthSvcInstance);

        return actnDataConverter.convertActnEthService(actnEthSvc);
    }
    public void deleteOtnTunnel(String actnOtnTunnelId) throws Exception{
        boolean result = restConfSBController.delete(pncInstance.pncId(), "ietf-te:te/tunnels/tunnel=" + actnOtnTunnelId, "json");
        if(!result){
            log.warn("Failed to delete otn tunnel");
        }else{
            log.info("Delete otn tunnel successfully");
        }
    }
    public void deleteEthService(String actnEthServiceId) throws Exception{
        boolean result = restConfSBController.delete(pncInstance.pncId(), "ietf-eth-tran-service:etht-svc/etht-svc-instances=" + actnEthServiceId, "json");
        if(!result){
            log.warn("Failed to delete ethernet service");
        }else{
            log.info("Delete ethernet service successfully");
        }
    }
    public static ObjectNode convertYangTeTunnel2Json(DefaultTunnel yangTeTunnel){
        String tunnelName = yangTeTunnel.name();

        DataNode dataNode = yangPojo2DataNode(getModIdForTeTunnels(), yangTeTunnel);

        return CodecConverter.dataNodeToObjectNode(getRidForTeTunnel(tunnelName), dataNode);
    }
    public static ObjectNode convertYangEthSvc2Json(DefaultEthtSvc yangEthSvc){
        EthtSvcInstances ethtSvcInstances = yangEthSvc.ethtSvcInstances().get(0);
        String ethSvcName = ethtSvcInstances.ethtSvcName();

        DataNode dataNode = yangPojo2DataNode(getModIdForEthSvc(), (DefaultEthtSvcInstances)ethtSvcInstances);

        return CodecConverter.dataNodeToObjectNode(getRIdForEthSvcInstances(ethSvcName), dataNode);
    }
}
