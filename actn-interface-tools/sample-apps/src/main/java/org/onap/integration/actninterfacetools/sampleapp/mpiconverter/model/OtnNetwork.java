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

import org.onap.integration.actninterfacetools.actnclient.api.CustomerOtnTopology;
import org.onap.integration.actninterfacetools.sampleapp.mpiconverter.converter.PncConverter;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static com.google.common.base.Preconditions.checkNotNull;

public class OtnNetwork extends CustomerOtnTopology implements Network {

    private final Type networkType = Type.of(0);
    private final String networkId;
    private final List<TeNodeKey> nodes;
    private final Map<TeNodeKey, TsNodeInfo> tsNodeInfos;
    private final List<TsLink> innerLinks;
    private final Map<TeNodeKey,List<TsLink>> edgeLinks;
    private final PncOtnBandwidthProfile bandwidthProfileSupplier;

//    public OtnNetwork(org.onosproject.yang.gen.v11.ietfnetwork.rev20180226.ietfnetwork.networks.Network yangNetwork){
//
//        this(yangNetwork,
//                ()-> PncConverter.getOtnNodes(yangNetwork),
//                ()-> PncConverter.getTsNodeInfo(yangNetwork),
//                ()-> PncConverter.getOtnInnerLinks(yangNetwork),
//                ()-> PncConverter.getOtnEdgeLinks(yangNetwork),
//                ()-> PncConverter.getBandWidthProfile(yangNetwork));
//    }


//     OtnNetwork(org.onosproject.yang.gen.v11.ietfnetwork.rev20180226.ietfnetwork.networks.Network yangNetwork,
//                      Supplier<List<TeNodeKey>> nodes,
//                      Supplier<Map<TeNodeKey, TsNodeInfo>> tsNodeInfos,
//                      Supplier<List<TsLink>> innerLinks,
//                      Supplier<Map<TeNodeKey, List<TsLink>>> edgeLinks,
//                      Supplier<PncOtnBandwidthProfile> bandwidthProfile){
//        checkNotNull(yangNetwork, "OtnNetwork: yangNetwork passed in is null");
//        this.networkId = yangNetwork.networkId().toString();
//        this.nodes = nodes;
//        this.innerLinks = innerLinks;
//        this.edgeLinks = edgeLinks;
//        this.tsNodeInfos = tsNodeInfos;
//        this.bandwidthProfileSupplier = bandwidthProfile;
//    }

    public OtnNetwork(String networkId, List<TeNodeKey> otnNodes, Map<TeNodeKey, TsNodeInfo> tsNodeInfo, List<TsLink> otnInnerLinks, Map<TeNodeKey, List<TsLink>> otnEdgeLinks, PncOtnBandwidthProfile bandWidthProfile) {
        this.networkId = networkId;
        this.nodes = otnNodes;
        this.innerLinks = otnInnerLinks;
        this.edgeLinks = otnEdgeLinks;
        this.tsNodeInfos = tsNodeInfo;
        this.bandwidthProfileSupplier = bandWidthProfile;
    }

    @Override
    public Type networkType() {
        return networkType;
    }

    @Override
    public List<TeNodeKey> nodes() {
        return nodes;
    }

    public List<TsLink> innerlinks() {
        return innerLinks;
    }

    public Map<TeNodeKey,List<TsLink>> edgeLinks(){
        return edgeLinks;
    }

    public Map<TeNodeKey, TsNodeInfo> tsNodeInfos(){
        return tsNodeInfos;
    }

    public PncOtnBandwidthProfile bandwidthProfile() {
        return bandwidthProfileSupplier;
    }

    @Override
    public String networkId(){
        return this.networkId;
    }
}
