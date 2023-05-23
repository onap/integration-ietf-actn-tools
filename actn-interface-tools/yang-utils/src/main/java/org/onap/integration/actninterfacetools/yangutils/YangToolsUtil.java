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

package org.onap.integration.actninterfacetools.yangutils;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.onap.integration.actninterfacetools.globalapi.GlobalService;
import org.onosproject.yang.gen.v11.ietfethtranservice.rev20210111.ietfethtranservice.DefaultEthtSvc;
import org.onosproject.yang.gen.v11.ietfethtranservice.rev20210111.ietfethtranservice.ethtsvc.DefaultEthtSvcInstances;
import org.onosproject.yang.gen.v11.ietfethtranservice.rev20210111.ietfethtranservice.ethtsvc.EthtSvcInstancesKeys;
import org.onosproject.yang.gen.v11.ietfnetwork.rev20180226.ietfnetwork.DefaultNetworks;
import org.onosproject.yang.gen.v11.ietfnetwork.rev20180226.ietfnetwork.NetworkId;
import org.onosproject.yang.gen.v11.ietfnetwork.rev20180226.ietfnetwork.NodeId;
import org.onosproject.yang.gen.v11.ietfnetwork.rev20180226.ietfnetwork.networks.DefaultNetwork;
import org.onosproject.yang.gen.v11.ietfnetwork.rev20180226.ietfnetwork.networks.NetworkKeys;
import org.onosproject.yang.gen.v11.ietfnetwork.rev20180226.ietfnetwork.networks.network.DefaultNode;
import org.onosproject.yang.gen.v11.ietfnetwork.rev20180226.ietfnetwork.networks.network.NodeKeys;
import org.onosproject.yang.gen.v11.ietfnetworktopology.rev20180226.ietfnetworktopology.LinkId;
import org.onosproject.yang.gen.v11.ietfnetworktopology.rev20180226.ietfnetworktopology.networks.network.augmentednwnetwork.DefaultLink;
import org.onosproject.yang.gen.v11.ietfnetworktopology.rev20180226.ietfnetworktopology.networks.network.augmentednwnetwork.LinkKeys;
import org.onosproject.yang.gen.v11.ietfte.rev20210220.ietfte.DefaultTe;
import org.onosproject.yang.gen.v11.ietfte.rev20210220.ietfte.globalsgrouping.DefaultGlobals;
import org.onosproject.yang.gen.v11.ietfte.rev20210220.ietfte.lspsgrouping.DefaultLsps;
import org.onosproject.yang.gen.v11.ietfte.rev20210220.ietfte.lspsgrouping.lsps.DefaultLsp;
import org.onosproject.yang.gen.v11.ietfte.rev20210220.ietfte.lspsgrouping.lsps.LspKeys;
import org.onosproject.yang.gen.v11.ietfte.rev20210220.ietfte.namedpathconstraints.DefaultNamedPathConstraints;
import org.onosproject.yang.gen.v11.ietfte.rev20210220.ietfte.namedpathconstraints.namedpathconstraints.DefaultNamedPathConstraint;
import org.onosproject.yang.gen.v11.ietfte.rev20210220.ietfte.namedpathconstraints.namedpathconstraints.NamedPathConstraintKeys;
import org.onosproject.yang.gen.v11.ietfte.rev20210220.ietfte.tunnelsgrouping.DefaultTunnels;
import org.onosproject.yang.gen.v11.ietfte.rev20210220.ietfte.tunnelsgrouping.tunnels.DefaultTunnel;
import org.onosproject.yang.gen.v11.ietfte.rev20210220.ietfte.tunnelsgrouping.tunnels.TunnelKeys;
import org.onosproject.yang.gen.v11.ietftetopology.rev20200806.ietftetopology.telinkconfigattributes.DefaultTeLinkAttributes;
import org.onosproject.yang.gen.v11.ietftetopology.rev20200806.ietftetopology.telinkconfigattributes.telinkattributes.DefaultUnderlay;
import org.onosproject.yang.gen.v11.ietftransclientservice.rev20210111.ietftransclientservice.DefaultClientSvc;
import org.onosproject.yang.model.ModelConverter;
import org.onosproject.yang.model.ModelObjectId;
import org.onosproject.yang.model.ResourceData;
import org.onosproject.yang.model.ResourceId;
import org.onosproject.yang.model.DefaultResourceData;
import org.onosproject.yang.model.DataNode;
import org.onosproject.yang.model.DefaultModelObjectData;
import org.onosproject.yang.model.ModelObjectData;
import org.onosproject.yang.model.ModelObject;
import org.onosproject.yang.model.InnerModelObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * The class services as a placeholder for utilities related to
 * generic YangTools operations.
 */
public final class YangToolsUtil {

    private static final Logger log = LoggerFactory.getLogger(YangToolsUtil.class);
    private static ModelConverter MODEL_CONVERTER = null;
    public static void active(){
        ServiceLoader<GlobalService> serviceLoader = ServiceLoader.load(GlobalService.class);
        for (GlobalService service : serviceLoader) {
            MODEL_CONVERTER = service.getModelConverter();
        }
    }
    public YangToolsUtil() {

    }


    /**
     * Returns the model object id for networks container.
     *
     * @return model object id
     */
    public static ModelObjectId getModIdForRoot() {
        return ModelObjectId.builder().build();
    }

    public static ModelObjectId getModIdForLink(String networkIdStr, String linkIdStr) {
        NetworkId nid = NetworkId.fromString(networkIdStr);
        NetworkKeys nkeys = new NetworkKeys();
        nkeys.networkId(nid);
        LinkId lid = LinkId.fromString(linkIdStr);
        LinkKeys lkeys = new LinkKeys();
        lkeys.linkId(lid);
        return ModelObjectId.builder()
                .addChild(DefaultNetworks.class)
                .addChild(DefaultNetwork.class, nkeys)
                .addChild(DefaultLink.class, lkeys)
                .build();
    }

    /**
     * Returns the model object id for networks container.
     *
     * @return model object id
     */
    public static ModelObjectId getModIdForNetworks() {
        return ModelObjectId.builder()
                .addChild(DefaultNetworks.class)
                .build();
    }

    public static ModelObjectId getModIdForNetwork(String networkIdStr) {
        NetworkId nid = NetworkId.fromString(networkIdStr);
        NetworkKeys nkeys = new NetworkKeys();
        nkeys.networkId(nid);
        return ModelObjectId.builder()
                .addChild(DefaultNetworks.class)
                .addChild(DefaultNetwork.class, nkeys)
                .build();
    }

    /**
     * Returns the resource data from the data node and the resource id.
     *
     * @param dataNode data node
     * @param resId    resource id
     * @return resource data
     */
    public static ResourceData getResourceData(DataNode dataNode, ResourceId resId) {
        return DefaultResourceData.builder()
                .addDataNode(dataNode)
                .resourceId(resId)
                .build();
    }

    /**
     * Returns the model object id for networks/network/node container.
     *
     * @return model object id
     */
    public static ModelObjectId getModIdForNode() {
        return ModelObjectId.builder()
                .addChild(DefaultNetworks.class)
                .addChild(DefaultNetwork.class, new NetworkKeys())
                .addChild(DefaultNode.class, new NodeKeys())
                .build();
    }

    /**
     * Returns the model object id for networks/network container.
     *
     * @param networkId identifier of the target network
     * @return model object id
     */
    public static ModelObjectId getModIdForYangNetwork(NetworkId networkId) {
        NetworkKeys networkKeys = new NetworkKeys();
        networkKeys.networkId(networkId);
        return ModelObjectId.builder()
                .addChild(DefaultNetworks.class)
                .addChild(DefaultNetwork.class, networkKeys)
                .build();
    }

    public static ModelObjectId getModIdForTe() {
        return ModelObjectId.builder()
                .addChild(DefaultTe.class)
                .build();
    }


    public static ModelObjectId getModIdForTeTunnels() {
        return ModelObjectId.builder()
                .addChild(DefaultTe.class)
                .addChild(DefaultTunnels.class)
                .build();
    }

    public static ModelObjectId getModIdForTeGlobals() {
        return ModelObjectId.builder()
                .addChild(DefaultTe.class)
                .addChild(DefaultGlobals.class)
                .build();
    }

    public static ModelObjectId getModIdForLsps() {
        return ModelObjectId.builder()
                .addChild(DefaultTe.class)
                .addChild(DefaultLsps.class)
                .build();
    }

    public static ModelObjectId getModIdForLsp(String tunnelNameStr) {
        LspKeys k = new LspKeys();
        k.tunnelName(tunnelNameStr);
        return ModelObjectId.builder()
                .addChild(DefaultTe.class)
                .addChild(DefaultLsps.class)
                .addChild(DefaultLsp.class, k)
                .build();
    }

    public static ModelObjectId getModIdForTeTunnel(String tunnelNameStr) {
        TunnelKeys tkeys = new TunnelKeys();
        tkeys.name(tunnelNameStr);
        return ModelObjectId.builder()
                .addChild(DefaultTe.class)
                .addChild(DefaultTunnels.class)
                .addChild(DefaultTunnel.class, tkeys)
                .build();
    }
    public static ModelObjectId getModIdForTeLspsState() {
        throw new RuntimeException("YangToolsUtil.getModIdForTeLspsState not implemented");
    }

    public static ModelObjectId getModIdForTeGlobalsPathConstraint(String pathConstraintName) {
        NamedPathConstraintKeys keys = new NamedPathConstraintKeys();
        keys.name(pathConstraintName);
        return ModelObjectId.builder()
                .addChild(DefaultTe.class)
                .addChild(DefaultGlobals.class)
                .addChild(DefaultNamedPathConstraints.class)
                .addChild(DefaultNamedPathConstraint.class, keys)
                .build();
    }
    public static ResourceId getRidForNetwork(String networkIdStr) {
        if (networkIdStr == null) {
            return null;
        }
        ModelObjectId modId = getModIdForNetwork(networkIdStr);
        return modelObjId2ResourceId(modId);
    }

    public static ResourceId getRidForTunnel(String tunnelNameStr) {
        if (tunnelNameStr != null && !tunnelNameStr.isEmpty()){
            ModelObjectId modId = getModIdForTeTunnel(tunnelNameStr);
            return modelObjId2ResourceId(modId);
        }
        return null;
    }

    public static ResourceId getRidForPhyConnections() {
        throw new RuntimeException("YangToolsUtil.getRidForPhyConnections not implemented");
    }
    public static ResourceId getRidForLink(String networkIdStr, String linkIdStr) {
        return modelObjId2ResourceId(getModIdForLink(networkIdStr, linkIdStr));
    }


    public static ResourceId getRidForNetworks() {
        return modelObjId2ResourceId(getModIdForNetworks());
    }

    private static ModelObjectId getModIdForNetworkLinkUnderlay(String networkIdStr, String linkIdStr) {
        NetworkId nid = NetworkId.fromString(networkIdStr);
        NetworkKeys nkeys = new NetworkKeys();
        nkeys.networkId(nid);
        LinkId lid = LinkId.fromString(linkIdStr);
        LinkKeys lkeys = new LinkKeys();
        lkeys.linkId(lid);
        return ModelObjectId.builder()
                .addChild(DefaultNetworks.class)
                .addChild(DefaultNetwork.class, nkeys)
                .addChild(DefaultLink.class, lkeys)
                .addChild(org.onosproject.yang.gen.v11.ietftetopology.rev20200806.ietftetopology.networks.network.link.augmentedntlink.DefaultTe.class)
                .addChild(DefaultTeLinkAttributes.class)
                .addChild(DefaultUnderlay.class)
                .build();
    }

    private static ModelObjectId getModIdForNode(String networkIdStr, String nodeIdStr) {
        NetworkId networkId = NetworkId.fromString(networkIdStr);
        NetworkKeys networkKeys = new NetworkKeys();
        networkKeys.networkId(networkId);

        NodeId nodeId = NodeId.fromString(nodeIdStr);
        NodeKeys nodeKeys = new NodeKeys();
        nodeKeys.nodeId(nodeId);

        return ModelObjectId.builder()
                .addChild(DefaultNetworks.class)
                .addChild(DefaultNetwork.class, networkKeys)
                .addChild(DefaultNode.class, nodeKeys)
                .build();
    }

    private static ModelObjectId getModIdForNetworkLink(String networkIdStr, String linkIdStr) {
        NetworkId nid = NetworkId.fromString(networkIdStr);
        NetworkKeys nkeys = new NetworkKeys();
        nkeys.networkId(nid);
        LinkId lid = LinkId.fromString(linkIdStr);
        LinkKeys lkeys = new LinkKeys();
        lkeys.linkId(lid);
        return ModelObjectId.builder()
                .addChild(DefaultNetworks.class)
                .addChild(DefaultNetwork.class, nkeys)
                .addChild(DefaultLink.class, lkeys)
                .build();
    }

    /**
     * Returns resource id from model converter.
     *
     * @param modelId model object id
     * @return resource id
     */
    public static ResourceId modelObjId2ResourceId(ModelObjectId modelId) {
        DefaultModelObjectData.Builder data = DefaultModelObjectData.builder().identifier(modelId);
        ResourceData resData = MODEL_CONVERTER.createDataNode(data.build());
        return resData.resourceId();
    }

    public static ResourceId getRidForTe() {
        return modelObjId2ResourceId(getModIdForTe());
    }

    public static ResourceId getRidForRoot() {
        return modelObjId2ResourceId(getModIdForRoot());
    }

    public static ResourceId getRidForTeTunnels() {
        return modelObjId2ResourceId(getModIdForTeTunnels());
    }

    public static ResourceId getRidForTeLsps(){
        return modelObjId2ResourceId(ModelObjectId.builder()
            .addChild(DefaultTe.class)
            .addChild(DefaultLsps.class)
            .build()
        );
    }

    public static ResourceId getRidForNetworkNode(String networkIdStr, String nodeIdStr) {
        return modelObjId2ResourceId(getModIdForNetworkNode(networkIdStr, nodeIdStr));
    }

    public static ModelObjectId getModIdForNetworkNode(String networkIdStr, String nodeIdStr) {
        NetworkId nid = NetworkId.fromString(networkIdStr);
        NetworkKeys networkKeys = new NetworkKeys();
        networkKeys.networkId(nid);

        NodeId nodeId = NodeId.fromString(nodeIdStr);
        NodeKeys nodeKeys = new NodeKeys();
        nodeKeys.nodeId(nodeId);

        return ModelObjectId.builder()
                .addChild(DefaultNetworks.class)
                .addChild(DefaultNetwork.class, networkKeys)
                .addChild(DefaultNode.class, nodeKeys)
                .build();
    }

    public static ResourceId getRidForTeTunnel(String tunnelName) {
        return modelObjId2ResourceId(getModIdForTeTunnel(tunnelName));
    }

    public static ResourceId getRidForLsp(String tunnelName) {
        return modelObjId2ResourceId(getModIdForLsp(tunnelName));
    }

    public static ResourceId getRidForNamedPathConstraint(String pathConstraintName) {
        return modelObjId2ResourceId(getModIdForTeGlobalsPathConstraint(pathConstraintName));
    }

    public static ResourceId getRidForTeGlobals() {
        return modelObjId2ResourceId(getModIdForTeGlobals());
    }

    public static ResourceId getRidForTeLspsState() {
        return modelObjId2ResourceId(getModIdForTeLspsState());
    }

    public static ResourceId getRidForTeTunnelStateDepTunnels(String teTunnelName) {
        return modelObjId2ResourceId(getModIdForTeTunnelStateDepTunnels(teTunnelName));
    }

    public static ResourceId getClientSvcRid() {
        return modelObjId2ResourceId(getModIdForClientSvc());

    }

    private static ModelObjectId getModIdForClientSvc() {
        return ModelObjectId.builder()
                .addChild(DefaultClientSvc.class)
                .build();
    }

    private static ModelObjectId getModIdForTeTunnelStateDepTunnels(String teTunnelName) {
        TunnelKeys tkeys = new TunnelKeys();
        tkeys.name(teTunnelName);
        return ModelObjectId.builder()
                .addChild(DefaultTe.class)
                .addChild(DefaultTunnels.class)
                .addChild(DefaultTunnel.class, tkeys)
                .build();
    }

    public static ModelObjectId getModIdForEthSvc() {
        return ModelObjectId.builder()
                .addChild(DefaultEthtSvc.class)
                .build();
    }

    public static ModelObjectId getModIdForEthGlobals() {
        return ModelObjectId.builder()
                .addChild(DefaultEthtSvc.class)
                .addChild(org.onosproject.yang.gen.v11.ietfethtranservice.rev20210111.ietfethtranservice.ethtsvc.DefaultGlobals.class)
                .build();
    }

    public static ModelObjectId getModIdForEthSvInstances(String ethSvcInstanceName) {
        EthtSvcInstancesKeys ethtSvcInstancesKeys = new EthtSvcInstancesKeys();
        ethtSvcInstancesKeys.ethtSvcName(ethSvcInstanceName);
        return ModelObjectId.builder()
                .addChild(DefaultEthtSvc.class)
                .addChild(DefaultEthtSvcInstances.class, ethtSvcInstancesKeys)
                .build();
    }

    public static ModelObjectId getModIdForEthSvState(String ethSvcInstanceName) {
        EthtSvcInstancesKeys ethtSvcInstancesKeys = new EthtSvcInstancesKeys();
        ethtSvcInstancesKeys.ethtSvcName(ethSvcInstanceName);
        return ModelObjectId.builder()
                .addChild(DefaultEthtSvc.class)
                .addChild(DefaultEthtSvcInstances.class, ethtSvcInstancesKeys)
                .build();
    }

    public static ModelObjectId getModIdForEthSvcConfig(String ethSvcName) {
        EthtSvcInstancesKeys ethtSvcInstancesKeys = new EthtSvcInstancesKeys();
        ethtSvcInstancesKeys.ethtSvcName(ethSvcName);

        return ModelObjectId.builder()
                .addChild(DefaultEthtSvc.class)
                .addChild(DefaultEthtSvcInstances.class, ethtSvcInstancesKeys)
                .build();
    }
    public static ResourceId getRIdForEthGlobals() {
        return modelObjId2ResourceId(getModIdForEthGlobals());
    }
    public static ResourceId getRIdForEthSvcInstances(String ethSvcInstanceName) {
        return modelObjId2ResourceId(getModIdForEthSvInstances(ethSvcInstanceName));
    }
    public static ResourceId getRidForTeTunnelP2p2ndPaths(String teTunnelName) {
        throw new RuntimeException("YangToolsUtil.getRidForTeTunnelP2p2nndPaths not implemented");
    }
    public static ResourceId getRidForTunnelState(String tunnelName) {
        throw new RuntimeException("YangToolsUtil.getRidForTunnelState not implemented");
    }
    public static ModelObjectId getModIdForNameExplicitPathState(String pathName) {
        return null;
    }

    public static ResourceId getRidForNameExplicitPathState(String name) {
        return modelObjId2ResourceId(getModIdForNameExplicitPathState(name));
    }

    public static ModelObjectId getModIdForPathConstraintState(String constraintName) {
        NamedPathConstraintKeys constraintKeys = new NamedPathConstraintKeys();
        constraintKeys.name(constraintName);
        return ModelObjectId.builder()
                .addChild(DefaultTe.class)
                .addChild(DefaultGlobals.class)
                .addChild(DefaultNamedPathConstraints.class)
                .addChild(DefaultNamedPathConstraint.class, constraintKeys)
                .build();
    }

    public static ResourceId getRidForNamePathConstraintState(String constraintName) {
        return modelObjId2ResourceId(getModIdForPathConstraintState(constraintName));
    }




    public static DataNode yangPojo2DataNode(ModelObjectId parentModId, ModelObject yangPojo) {
        ModelObjectData outputMo = yangPojo2ModData(parentModId, yangPojo);
        ResourceData resourceData = MODEL_CONVERTER.createDataNode(outputMo);

        return resourceData.dataNodes().get(0);
    }

    public static ModelObjectData yangPojo2ModData(ModelObjectId modId, ModelObject yangPojo) {
        return DefaultModelObjectData.builder().identifier(modId)
                .addModelObject(yangPojo).build();
    }

    public static <T extends InnerModelObject> T convertJsonToYangObj(String parentRidStr,
                                                                      ObjectNode jsonNode,
                                                                      Class<T> modelClass) {
        if (jsonNode == null) {
            log.error("ERROR: convertJsonToYangObj: jsonNode is null");
            return null;
        }

        ResourceData resourceData = CodecConverter.objectNodeToResourceData(parentRidStr, jsonNode);
        if (resourceData == null || resourceData.dataNodes() == null) {
            log.error("ERROR: convertJsonToYangObj: {} is null",
                      resourceData == null ? "resourceData" : "resourceData.dataNodes()");
            return null;
        }

        DataNode dataNode = resourceData.dataNodes().get(0);
        ResourceId parentRid = resourceData.resourceId();

        if (parentRid == null) {
            parentRid = ResourceId.builder().addBranchPointSchema("/", null).build();
            dataNode = CodecConverter.removeTopDataNode(dataNode);
        }

        return convertDataNodeToYangObj(parentRid, dataNode, modelClass);
    }

    public static <T extends InnerModelObject> T convertDataNodeToYangObj(ResourceId parentRid,
                                                                          DataNode dataNode,
                                                                          Class<T> modelClass) {
        List<ModelObject> objects = getModelObjects(dataNode, parentRid);

        for (ModelObject obj : objects) {
            if (modelClass.isInstance(obj)) {
                return (T) obj;
            }
        }

        log.error("ERROR: convertDataNodeToYangObj: conversion failed. parentRid={}, class={}",
                  parentRid, modelClass.getName());

        return null;
    }

    /**
     * Returns model objects of the store. The data node read from store
     * gives the particular node. So the node's parent resource id is taken
     * and the data node is given to model converter.
     *
     * @param dataNode  data node from store
     * @param parentRid parent resource id
     * @return model objects
     */
    public static List<ModelObject> getModelObjects(DataNode dataNode,
                                                    ResourceId parentRid) {
        if (dataNode == null || parentRid == null) {
            log.error("ERROR: getModelObjects: {} is null", dataNode == null ? "dataNode" : "parentRid");
            return null;
        }

        ResourceData data = getResourceData(dataNode, parentRid);
        ModelObjectData modelData = MODEL_CONVERTER.createModel(data);
        return modelData.modelObjects();
    }
}
