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
package org.onap.integration.actninterfacetools.sampleapp.mpiconverter.converter;

import org.onap.integration.actninterfacetools.globalapi.ActnDataConverter;
import org.onap.integration.actninterfacetools.globalapi.CustomerEthService;
import org.onap.integration.actninterfacetools.globalapi.CustomerOtnTopology;
import org.onap.integration.actninterfacetools.globalapi.CustomerOtnTunnel;
import org.onap.integration.actninterfacetools.sampleapp.mpiconverter.model.LTPoint;
import org.onap.integration.actninterfacetools.sampleapp.mpiconverter.model.LtpId;
import org.onap.integration.actninterfacetools.sampleapp.mpiconverter.model.TeNodeKey;
import org.onap.integration.actninterfacetools.sampleapp.mpiconverter.model.TsLink;
import org.onap.integration.actninterfacetools.sampleapp.mpiconverter.model.OduResource;
import org.onap.integration.actninterfacetools.sampleapp.mpiconverter.model.TsTunnel;
import org.onap.integration.actninterfacetools.sampleapp.mpiconverter.model.EthSrvInstance;
import org.onap.integration.actninterfacetools.sampleapp.mpiconverter.model.EthSrvAccessPoint;
import org.onap.integration.actninterfacetools.sampleapp.mpiconverter.model.EthSrvEndPoint;
import org.onap.integration.actninterfacetools.sampleapp.mpiconverter.model.TsEthSrv;
import org.onap.integration.actninterfacetools.sampleapp.mpiconverter.model.OduType;
import org.onap.integration.actninterfacetools.sampleapp.mpiconverter.model.TsNodeInfo;
import org.onap.integration.actninterfacetools.sampleapp.mpiconverter.model.PncOtnBandwidthProfile;
import org.onap.integration.actninterfacetools.sampleapp.mpiconverter.model.TsLinkKey;
import org.onap.integration.actninterfacetools.sampleapp.mpiconverter.model.OtnNetwork;
import org.onosproject.yang.gen.v1.ietfyangtypes.rev20210414.ietfyangtypes.DottedQuad;
import org.onosproject.yang.gen.v1.ietfyangtypes.rev20210414.ietfyangtypes.HexString;
import org.onosproject.yang.gen.v11.ietfethtranservice.rev20210111.ietfethtranservice.DefaultEthtSvc;
import org.onosproject.yang.gen.v11.ietfethtranservice.rev20210111.ietfethtranservice.bandwidthprofiles.Direction;
import org.onosproject.yang.gen.v11.ietfethtranservice.rev20210111.ietfethtranservice.bandwidthprofiles.direction.DefaultSymmetrical;
import org.onosproject.yang.gen.v11.ietfethtranservice.rev20210111.ietfethtranservice.bandwidthprofiles.direction.Symmetrical;
import org.onosproject.yang.gen.v11.ietfethtranservice.rev20210111.ietfethtranservice.bandwidthprofiles.direction.symmetrical.DefaultIngressEgressBandwidthProfile;
import org.onosproject.yang.gen.v11.ietfethtranservice.rev20210111.ietfethtranservice.bandwidthprofiles.direction.symmetrical.IngressEgressBandwidthProfile;
import org.onosproject.yang.gen.v11.ietfethtranservice.rev20210111.ietfethtranservice.ethtsvc.DefaultEthtSvcInstances;
import org.onosproject.yang.gen.v11.ietfethtranservice.rev20210111.ietfethtranservice.ethtsvc.EthtSvcInstances;
import org.onosproject.yang.gen.v11.ietfethtranservice.rev20210111.ietfethtranservice.ethtsvcendpointgrouping.DefaultEthtSvcAccessPoints;
import org.onosproject.yang.gen.v11.ietfethtranservice.rev20210111.ietfethtranservice.ethtsvcendpointgrouping.EthtSvcAccessPoints;
import org.onosproject.yang.gen.v11.ietfethtranservice.rev20210111.ietfethtranservice.ethtsvcendpointgrouping.ServiceClassification;
import org.onosproject.yang.gen.v11.ietfethtranservice.rev20210111.ietfethtranservice.ethtsvcendpointgrouping.serviceclassification.DefaultVlanClassification;
import org.onosproject.yang.gen.v11.ietfethtranservice.rev20210111.ietfethtranservice.ethtsvcendpointgrouping.serviceclassification.vlanclassification.DefaultOuterTag;
import org.onosproject.yang.gen.v11.ietfethtranservice.rev20210111.ietfethtranservice.ethtsvcendpointgrouping.serviceclassification.vlanclassification.OuterTag;
import org.onosproject.yang.gen.v11.ietfethtranservice.rev20210111.ietfethtranservice.ethtsvcinstanceconfig.DefaultEthtSvcEndPoints;
import org.onosproject.yang.gen.v11.ietfethtranservice.rev20210111.ietfethtranservice.ethtsvcinstanceconfig.DefaultUnderlay;
import org.onosproject.yang.gen.v11.ietfethtranservice.rev20210111.ietfethtranservice.ethtsvcinstanceconfig.EthtSvcEndPoints;
import org.onosproject.yang.gen.v11.ietfethtranservice.rev20210111.ietfethtranservice.ethtsvcinstanceconfig.Underlay;
import org.onosproject.yang.gen.v11.ietfethtranservice.rev20210111.ietfethtranservice.ethtsvctunnelparameters.technology.DefaultFrameBase;
import org.onosproject.yang.gen.v11.ietfethtranservice.rev20210111.ietfethtranservice.ethtsvctunnelparameters.technology.framebase.DefaultOtnTunnels;
import org.onosproject.yang.gen.v11.ietfethtranservice.rev20210111.ietfethtranservice.ethtsvctunnelparameters.technology.framebase.OtnTunnels;
import org.onosproject.yang.gen.v11.ietfethtranservice.rev20210111.ietfethtranservice.namedorvaluebandwidthprofile.Style;
import org.onosproject.yang.gen.v11.ietfethtranservice.rev20210111.ietfethtranservice.namedorvaluebandwidthprofile.style.DefaultValue;
import org.onosproject.yang.gen.v11.ietfethtranservice.rev20210111.ietfethtranservice.resiliencegrouping.DefaultResilience;
import org.onosproject.yang.gen.v11.ietfethtranservice.rev20210111.ietfethtranservice.resiliencegrouping.Resilience;
import org.onosproject.yang.gen.v11.ietfethtranservice.rev20210111.ietfethtranservice.vlanclassification.individualbundlingvlan.DefaultIndividualVlan;
import org.onosproject.yang.gen.v11.ietfethtranservice.rev20210111.ietfethtranservice.vlanclassification.individualbundlingvlan.IndividualVlan;
import org.onosproject.yang.gen.v11.ietfethtrantypes.rev20191103.ietfethtrantypes.ServiceTypeTypedef;
import org.onosproject.yang.gen.v11.ietfethtrantypes.rev20191103.ietfethtrantypes.RmpSvc;
import org.onosproject.yang.gen.v11.ietfethtrantypes.rev20191103.ietfethtrantypes.VlanClassification;
import org.onosproject.yang.gen.v11.ietfethtrantypes.rev20191103.ietfethtrantypes.EthTagClassify;
import org.onosproject.yang.gen.v11.ietfethtrantypes.rev20191103.ietfethtrantypes.ClassifyCvlan;
import org.onosproject.yang.gen.v11.ietfethtrantypes.rev20191103.ietfethtrantypes.Vlanid;
import org.onosproject.yang.gen.v11.ietfethtrantypes.rev20191103.ietfethtrantypes.BandwidthProfileTypeTypedef;
import org.onosproject.yang.gen.v11.ietfethtrantypes.rev20191103.ietfethtrantypes.Mef10Bwp;
import org.onosproject.yang.gen.v11.ietflayer1types.rev20210219.ietflayer1types.OtnTpn;
import org.onosproject.yang.gen.v11.ietflayer1types.rev20210219.ietflayer1types.Oduflex;
import org.onosproject.yang.gen.v11.ietflayer1types.rev20210219.ietflayer1types.Odu0;
import org.onosproject.yang.gen.v11.ietflayer1types.rev20210219.ietflayer1types.Odu1;
import org.onosproject.yang.gen.v11.ietflayer1types.rev20210219.ietflayer1types.Odu2;
import org.onosproject.yang.gen.v11.ietflayer1types.rev20210219.ietflayer1types.Odu2e;
import org.onosproject.yang.gen.v11.ietflayer1types.rev20210219.ietflayer1types.Odu3;
import org.onosproject.yang.gen.v11.ietflayer1types.rev20210219.ietflayer1types.Odu4;
import org.onosproject.yang.gen.v11.ietflayer1types.rev20210219.ietflayer1types.otnlabelstartend.RangeType;
import org.onosproject.yang.gen.v11.ietflayer1types.rev20210219.ietflayer1types.otnlabelstartend.rangetype.DefaultTribPort;
import org.onosproject.yang.gen.v11.ietflayer1types.rev20210219.ietflayer1types.otnlinkbandwidth.Odulist;
import org.onosproject.yang.gen.v11.ietflayer1types.rev20210219.ietflayer1types.otnpathbandwidth.Otn;
import org.onosproject.yang.gen.v11.ietflayer1types.rev20210219.ietflayer1types.otnpathbandwidth.otn.oduflextype.DefaultGfpNk;
import org.onosproject.yang.gen.v11.ietflayer1types.rev20210219.ietflayer1types.otnpathbandwidth.otn.oduflextype.GfpNk;
import org.onosproject.yang.gen.v11.ietfte.rev20210220.ietfte.pathconstraintscommon.DefaultPathOutSegment;
import org.onosproject.yang.gen.v11.ietfte.rev20210220.ietfte.pathconstraintscommon.PathOutSegment;
import org.onosproject.yang.gen.v11.ietfte.rev20210220.ietfte.protectionrestorationproperties.DefaultProtection;
import org.onosproject.yang.gen.v11.ietfte.rev20210220.ietfte.protectionrestorationproperties.Protection;
import org.onosproject.yang.gen.v11.ietfte.rev20210220.ietfte.tunnelproperties.DefaultPrimaryPaths;
import org.onosproject.yang.gen.v11.ietfte.rev20210220.ietfte.tunnelproperties.PrimaryPaths;
import org.onosproject.yang.gen.v11.ietfte.rev20210220.ietfte.tunnelproperties.primarypaths.DefaultPrimaryPath;
import org.onosproject.yang.gen.v11.ietfte.rev20210220.ietfte.tunnelproperties.primarypaths.PrimaryPath;
import org.onosproject.yang.gen.v11.ietfte.rev20210220.ietfte.tunnelsgrouping.tunnels.DefaultTunnel;
import org.onosproject.yang.gen.v11.ietftetypes.rev20200610.ietftetypes.TeNodeId;
import org.onosproject.yang.gen.v11.ietftetypes.rev20200610.ietftetypes.LspEncodingOduk;
import org.onosproject.yang.gen.v11.ietftetypes.rev20200610.ietftetypes.SwitchingOtn;
import org.onosproject.yang.gen.v11.ietftetypes.rev20200610.ietftetypes.TunnelAdminStateUp;
import org.onosproject.yang.gen.v11.ietftetypes.rev20200610.ietftetypes.LspProtectionUnprotected;
import org.onosproject.yang.gen.v11.ietftetypes.rev20200610.ietftetypes.TeGlobalId;
import org.onosproject.yang.gen.v11.ietftetypes.rev20200610.ietftetypes.TeTopologyId;
import org.onosproject.yang.gen.v11.ietftetypes.rev20200610.ietftetypes.TeTpId;
import org.onosproject.yang.gen.v11.ietftetypes.rev20200610.ietftetypes.labelrestrictioninfo.DefaultLabelStart;
import org.onosproject.yang.gen.v11.ietftetypes.rev20200610.ietftetypes.labelrestrictioninfo.LabelStart;
import org.onosproject.yang.gen.v11.ietftetypes.rev20200610.ietftetypes.labelsetinfo.DefaultLabelRestrictions;
import org.onosproject.yang.gen.v11.ietftetypes.rev20200610.ietftetypes.labelsetinfo.LabelRestrictions;
import org.onosproject.yang.gen.v11.ietftetypes.rev20200610.ietftetypes.labelsetinfo.labelrestrictions.DefaultLabelRestriction;
import org.onosproject.yang.gen.v11.ietftetypes.rev20200610.ietftetypes.labelsetinfo.labelrestrictions.LabelRestriction;
import org.onosproject.yang.gen.v11.ietftetypes.rev20200610.ietftetypes.tebandwidth.DefaultTeBandwidth;
import org.onosproject.yang.gen.v11.ietftetypes.rev20200610.ietftetypes.tebandwidth.TeBandwidth;
import org.onosproject.yang.gen.v11.ietftetypes.rev20200610.ietftetypes.tebandwidth.tebandwidth.Technology;
import org.onosproject.yang.gen.v11.ietfnetwork.rev20180226.ietfnetwork.Networks;
import org.onosproject.yang.gen.v11.ietfnetwork.rev20180226.ietfnetwork.NodeId;
import org.onosproject.yang.gen.v11.ietfnetwork.rev20180226.ietfnetwork.networks.DefaultNetwork;
import org.onosproject.yang.gen.v11.ietfnetwork.rev20180226.ietfnetwork.networks.Network;
import org.onosproject.yang.gen.v11.ietfnetwork.rev20180226.ietfnetwork.networks.network.DefaultNode;
import org.onosproject.yang.gen.v11.ietfnetwork.rev20180226.ietfnetwork.networks.network.Node;
import org.onosproject.yang.gen.v11.ietfnetworktopology.rev20180226.ietfnetworktopology.LinkId;
import org.onosproject.yang.gen.v11.ietfnetworktopology.rev20180226.ietfnetworktopology.TpId;
import org.onosproject.yang.gen.v11.ietfnetworktopology.rev20180226.ietfnetworktopology.networks.network.DefaultAugmentedNwNetwork;
import org.onosproject.yang.gen.v11.ietfnetworktopology.rev20180226.ietfnetworktopology.networks.network.augmentednwnetwork.DefaultLink;
import org.onosproject.yang.gen.v11.ietfnetworktopology.rev20180226.ietfnetworktopology.networks.network.augmentednwnetwork.Link;
import org.onosproject.yang.gen.v11.ietfnetworktopology.rev20180226.ietfnetworktopology.networks.network.augmentednwnetwork.link.DefaultDestination;
import org.onosproject.yang.gen.v11.ietfnetworktopology.rev20180226.ietfnetworktopology.networks.network.augmentednwnetwork.link.DefaultSource;
import org.onosproject.yang.gen.v11.ietfnetworktopology.rev20180226.ietfnetworktopology.networks.network.augmentednwnetwork.link.Destination;
import org.onosproject.yang.gen.v11.ietfnetworktopology.rev20180226.ietfnetworktopology.networks.network.augmentednwnetwork.link.Source;
import org.onosproject.yang.gen.v11.ietfotntopology.rev20210222.ietfotntopology.networks.network.link.te.telinkattributes.maxlinkbandwidth.tebandwidth.technology.augmentedtettechnology.DefaultOtn;
import org.onosproject.yang.gen.v11.ietftetopology.rev20200806.ietftetopology.networks.network.link.DefaultAugmentedNtLink;
import org.onosproject.yang.gen.v11.ietftetopology.rev20200806.ietftetopology.networks.network.link.augmentedntlink.DefaultTe;
import org.onosproject.yang.gen.v11.ietftetopology.rev20200806.ietftetopology.networks.network.node.augmentednwnode.Te;
import org.onosproject.yang.gen.v11.ietftetopology.rev20200806.ietftetopology.networks.network.node.te.tunnelterminationpoint.locallinkconnectivities.DefaultAugmentedLocalLinkConnectivities;
import org.onosproject.yang.gen.v11.ietftetopology.rev20200806.ietftetopology.telinkconfigattributes.TeLinkAttributes;
import org.onosproject.yang.gen.v11.ietftetopology.rev20200806.ietftetopology.telinkinfoattributes.UnreservedBandwidth;
import org.onosproject.yang.gen.v11.ietftetopology.rev20200806.ietftetopology.tenodeaugment.TunnelTerminationPoint;
import org.onosproject.yang.gen.v11.ietftetopology.rev20200806.ietftetopology.tenodetunnelterminationpointconfig.LocalLinkConnectivities;
import org.onosproject.yang.gen.v11.ietftetopology.rev20200806.ietftetopology.tenodetunnelterminationpointllclist.LocalLinkConnectivity;
import org.onosproject.yang.gen.v11.ietftetypes.rev20200610.ietftetypes.telabel.DefaultTeLabel;
import org.onosproject.yang.gen.v11.ietftetypes.rev20200610.ietftetypes.telabel.TeLabel;
import org.onosproject.yang.gen.v11.ietftetypes.rev20200610.ietftetypes.tetopologyid.TeTopologyIdUnion;
import org.onosproject.yang.gen.v11.ietftetypes.rev20200610.ietftetypes.tetopologyidentifier.DefaultTeTopologyIdentifier;
import org.onosproject.yang.gen.v11.ietftetypes.rev20200610.ietftetypes.tetopologyidentifier.TeTopologyIdentifier;
import org.onosproject.yang.gen.v11.ietftetypes.rev20200610.ietftetypes.tetpid.TeTpIdUnion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.onap.integration.actninterfacetools.sampleapp.mpiconverter.model.Link.State.ACTIVE;

public class PncConverter extends ActnDataConverter {

    private static final Logger log = LoggerFactory.getLogger(PncConverter.class);

    public CustomerOtnTopology convertActnOtnTopology(org.onosproject.yang.gen.v11.ietfnetwork.rev20180226.ietfnetwork.networks.Network network) throws Exception {
        checkNotNull(network, "convertActnOtnTopology: network is null");
        String networkId = network.networkId().toString();
        List<TeNodeKey> otnNodes = getOtnNodes(network);
        Map<TeNodeKey, TsNodeInfo> tsNodeInfo = getTsNodeInfo(network);
        List<TsLink> otnInnerLinks = getOtnInnerLinks(network);
        Map<TeNodeKey, List<TsLink>> otnEdgeLinks = getOtnEdgeLinks(network);
        PncOtnBandwidthProfile bandWidthProfile = getBandWidthProfile(network);
        return new OtnNetwork(networkId, otnNodes, tsNodeInfo, otnInnerLinks, otnEdgeLinks, bandWidthProfile);
    }

    public CustomerOtnTunnel convertActnOtnTunnel(DefaultTunnel actnOtnTunnel) throws Exception {
        checkNotNull(actnOtnTunnel, "convertActnOtnTunnel: actnOtnTunnel is null");
        TeNodeKey srcNode = (actnOtnTunnel.source() != null && actnOtnTunnel.source().dottedQuad().string() != "") ? TeNodeKey.of(actnOtnTunnel.source().toString()) : new TeNodeKey(-1, -1, -1, -1);
        LtpId srcLtpId = (actnOtnTunnel.srcTunnelTpId() != null && !(new String(actnOtnTunnel.srcTunnelTpId())).isEmpty()) ? LtpId.ltpId1(PncUtils.byteArray2Long(actnOtnTunnel.srcTunnelTpId()), new String(actnOtnTunnel.srcTunnelTpId())) : LtpId.ltpId(-1);
        //PncUtils.byteArray2Long(actnOtnTunnel.srcTunnelTpId())
        LTPoint srcLtPoint = new LTPoint(srcNode, srcLtpId);
        TsLink srcLink = new TsLink("defaultSrcId", srcLtPoint, srcLtPoint, ACTIVE, org.onap.integration.actninterfacetools.sampleapp.mpiconverter.model.Link.Type.REGULAR);
        TeNodeKey dstNode = (actnOtnTunnel.destination() != null && actnOtnTunnel.destination().dottedQuad().string() != "") ? TeNodeKey.of(actnOtnTunnel.destination().toString()) : new TeNodeKey(-1, -1, -1, -1);
        LtpId dstLtpId = (actnOtnTunnel.dstTunnelTpId() != null && !(new String(actnOtnTunnel.dstTunnelTpId())).isEmpty()) ? LtpId.ltpId1(PncUtils.byteArray2Long(actnOtnTunnel.dstTunnelTpId()), new String(actnOtnTunnel.dstTunnelTpId())) : LtpId.ltpId(-1);
        //PncUtils.byteArray2Long(actnOtnTunnel.dstTunnelTpId())
        LTPoint dstLtPoint = new LTPoint(dstNode, dstLtpId);
        TsLink dstLink = new TsLink("defaultDstId", dstLtPoint, dstLtPoint, ACTIVE, org.onap.integration.actninterfacetools.sampleapp.mpiconverter.model.Link.Type.REGULAR);
        List<TsLink> linkList = new ArrayList<>();
        linkList.add(srcLink);
        linkList.add(dstLink);
        TsTunnel tsTunnel = new TsTunnel(linkList, actnOtnTunnel.name());
        if (actnOtnTunnel.teBandwidth() != null &&
                actnOtnTunnel.teBandwidth().technology() instanceof org.onosproject.yang.gen.v11.ietfotntunnel.rev20210625.
                        ietfotntunnel.te.tunnels.tunnel.tebandwidth.technology.augmentedtetechnology.DefaultOtn) {
            TeBandwidth teBandwidth = actnOtnTunnel.teBandwidth();
            OduResource oduRsrce = teBandwidth2OduResource(teBandwidth);
            tsTunnel.bw(oduRsrce);
        } else {
            log.warn("Missing valid tunnel bandwidth info inside Otn DefaultTunnel");
        }
        return tsTunnel;
    }

    public DefaultTunnel convertActnOtnTunnel(CustomerOtnTunnel customerOtnTunnel) throws Exception {
        checkNotNull(customerOtnTunnel, "convertActnOtnTunnel: customerOtnTunnel is null");
        DefaultTunnel defaultTunnel = new DefaultTunnel();
        LTPoint src = ((TsTunnel) customerOtnTunnel).src();
        LTPoint dst = ((TsTunnel) customerOtnTunnel).dst();
        TeNodeKey srcTeNodeKey = src.tsDeviceId();
        TeNodeKey dstTeNodeKey = dst.tsDeviceId();
        String srcTeNodeId = srcTeNodeKey.toString();
        String dstTeNodeId = dstTeNodeKey.toString();
        defaultTunnel.source(TeNodeId.fromString(srcTeNodeId));
        defaultTunnel.destination(TeNodeId.fromString(dstTeNodeId));
        defaultTunnel.dstTunnelTpId(dst.ltPointId().toString().getBytes());
        String a = new String(dst.ltPointId().toString().getBytes());
        defaultTunnel.name(((TsTunnel) customerOtnTunnel).name());

        org.onosproject.yang.gen.v11.ietflayer1types.rev20210219.ietflayer1types.otnpathbandwidth.Otn layer1Otn = new org.onosproject.yang.gen.v11.ietflayer1types.rev20210219.ietflayer1types.otnpathbandwidth.DefaultOtn();
        GfpNk oduflexType = new DefaultGfpNk();
        oduflexType.gfpn((short) ((TsTunnel) customerOtnTunnel).bw().getNumberOfOdu(OduType.ODUFLEX));
        layer1Otn.oduType(Oduflex.class);
        layer1Otn.oduflexType(oduflexType);
        Technology otn = new org.onosproject.yang.gen.v11.ietfotntunnel.rev20210625.ietfotntunnel.te.tunnels.tunnel.tebandwidth.technology.augmentedtetechnology.DefaultOtn();
        ((org.onosproject.yang.gen.v11.ietfotntunnel.rev20210625.ietfotntunnel.te.tunnels.tunnel.tebandwidth.technology.augmentedtetechnology.DefaultOtn) otn).otn(layer1Otn);
        TeBandwidth teBandwidth = new DefaultTeBandwidth();
        teBandwidth.technology(otn);
        defaultTunnel.teBandwidth(teBandwidth);

        defaultTunnel.encoding(LspEncodingOduk.class);
        defaultTunnel.switchingType(SwitchingOtn.class);
        defaultTunnel.adminState(TunnelAdminStateUp.class);

        Protection protection = new DefaultProtection();
        protection.enable(true);
        protection.holdOffTime(0);
        protection.protectionReversionDisable(true);
        protection.protectionType(LspProtectionUnprotected.class);
        protection.waitToRevert(0);
        defaultTunnel.protection(protection);

        PrimaryPaths primaryPaths = new DefaultPrimaryPaths();
        List<PrimaryPath> primaryPathList = new ArrayList<PrimaryPath>();
        PrimaryPath primaryPath = new DefaultPrimaryPath();
        primaryPath.name("primary");
        PathOutSegment pathOutSegment = new DefaultPathOutSegment();
        LabelRestrictions labelRestrictions = new DefaultLabelRestrictions();
        List<LabelRestriction> labelRestrictionList = new ArrayList<LabelRestriction>();
        LabelRestriction labelRestriction = new DefaultLabelRestriction();
        labelRestriction.index(1);
        LabelStart labelStart = new DefaultLabelStart();
        TeLabel teLabel = new DefaultTeLabel();
        org.onosproject.yang.gen.v11.ietfotntunnel.rev20210625.ietfotntunnel.te.tunnels.tunnel.primarypaths.primarypath.pathinsegment.labelrestrictions.labelrestriction.labelstart.telabel.technology.augmentedtetechnology.DefaultOtn otnTpnTech = new org.onosproject.yang.gen.v11.ietfotntunnel.rev20210625.ietfotntunnel.te.tunnels.tunnel.primarypaths.primarypath.pathinsegment.labelrestrictions.labelrestriction.labelstart.telabel.technology.augmentedtetechnology.DefaultOtn();
        OtnTpn otnTpn = new OtnTpn(1);
        RangeType rangeType = new DefaultTribPort();
        ((DefaultTribPort) rangeType).otnTpn(otnTpn);
        otnTpnTech.rangeType(rangeType);
        teLabel.technology(otnTpnTech);
        labelStart.teLabel(teLabel);
        labelRestriction.labelStart(labelStart);
        labelRestrictionList.add(labelRestriction);
        labelRestrictions.labelRestriction(labelRestrictionList);
        pathOutSegment.labelRestrictions(labelRestrictions);
        primaryPath.pathOutSegment(pathOutSegment);
        primaryPathList.add(primaryPath);
        primaryPaths.primaryPath(primaryPathList);
        defaultTunnel.primaryPaths(primaryPaths);
        return defaultTunnel;
    }

    public CustomerEthService convertActnEthService(DefaultEthtSvc actnEthService) throws Exception {
        checkNotNull(actnEthService, "convertActnEthService: actnEthService is null");
        List<EthtSvcInstances> ethtSvcInstances = actnEthService.ethtSvcInstances();
        if (ethtSvcInstances != null && !ethtSvcInstances.isEmpty()) {
            List<EthSrvInstance> ethSrvInstances = new ArrayList<>();
            for (EthtSvcInstances ethtSvcInstance : ethtSvcInstances) {
                String ethtSvcName = ethtSvcInstance.ethtSvcName();
                String ethtSvcDescr = ethtSvcInstance.ethtSvcDescr();
                TeTopologyIdentifier teTopologyIdentifier = ethtSvcInstance.teTopologyIdentifier();
                long providerId = teTopologyIdentifier.providerId().uint32();
                long clientId = teTopologyIdentifier.clientId().uint32();
                String topologyId = teTopologyIdentifier.topologyId().union().string();
                List<TsTunnel> tsTunnels = new ArrayList<>();
                TeNodeKey src = new TeNodeKey(-1, -1, -1, -1);
                TeNodeKey dst = new TeNodeKey(-1, -1, -1, -1);
                LtpId dstLtpId = LtpId.ltpId(0, "");
                LTPoint srcLTPoint = new LTPoint(src, null);
                LTPoint dstLTPoint = new LTPoint(dst, dstLtpId);
                List<TsLink> tsLinkList = new ArrayList<>();
                TsLink tsLink = new TsLink(srcLTPoint, dstLTPoint);
                tsLinkList.add(tsLink);
                List<OtnTunnels> otnTunnels = ((DefaultFrameBase) ethtSvcInstance.underlay().technology()).otnTunnels();
                if (otnTunnels != null && !otnTunnels.isEmpty()) {
                    for (OtnTunnels otnTunnel : otnTunnels) {
                        tsTunnels.add(new TsTunnel(tsLinkList, otnTunnel.name().toString()));
                    }
                }
                List<EthSrvEndPoint> ethSrvEndPoints = new ArrayList<>();
                List<EthtSvcEndPoints> ethtSvcEndPoints = ethtSvcInstance.ethtSvcEndPoints();
                if (ethtSvcEndPoints != null && !ethtSvcEndPoints.isEmpty()) {
                    for (EthtSvcEndPoints ethtSvcEndPoint : ethtSvcEndPoints) {
                        String ethtSvcEndPointName = ethtSvcEndPoint.ethtSvcEndPointName();
                        List<EthSrvAccessPoint> ethSrvAccessPoints = new ArrayList<>();
                        List<EthtSvcAccessPoints> ethtSvcAccessPoints = ethtSvcEndPoint.ethtSvcAccessPoints();
                        if (ethtSvcAccessPoints != null && !ethtSvcAccessPoints.isEmpty()) {
                            for (EthtSvcAccessPoints ethtSvcAccessPoint : ethtSvcAccessPoints) {
                                ethSrvAccessPoints.add(new EthSrvAccessPoint(ethtSvcAccessPoint.accessPointId(), ethtSvcAccessPoint.accessNodeId().dottedQuad().string(), ethtSvcAccessPoint.accessLtpId().union().uint32()));
                            }
                        }
                        int vlanValue = ((DefaultIndividualVlan) ((DefaultVlanClassification) ethtSvcEndPoint.serviceClassification()).outerTag().individualBundlingVlan()).vlanValue().uint16();
                        BigInteger cir = ((DefaultValue) ((Symmetrical) ethtSvcEndPoint.direction()).ingressEgressBandwidthProfile().style()).cir();
                        BigInteger eir = ((DefaultValue) ((Symmetrical) ethtSvcEndPoint.direction()).ingressEgressBandwidthProfile().style()).eir();
                        ethSrvEndPoints.add(new EthSrvEndPoint(ethtSvcEndPointName, ethSrvAccessPoints, vlanValue, cir, eir));
                    }
                }
                ethSrvInstances.add(new EthSrvInstance(ethtSvcName, ethtSvcDescr, providerId, clientId, topologyId, tsTunnels, ethSrvEndPoints));
            }
            return new TsEthSrv(ethSrvInstances);
        } else {
            return new TsEthSrv(null);
        }
    }

    public DefaultEthtSvc convertActnEthService(CustomerEthService customerEthService) throws Exception {
        checkNotNull(customerEthService, "convertActnEthService: customerEthService is null");
        List<EthSrvInstance> ethSrvInstances = ((TsEthSrv) customerEthService).getEthSrvInstances();
        if (ethSrvInstances != null && !ethSrvInstances.isEmpty()) {
            DefaultEthtSvc defaultEthtSvc = new DefaultEthtSvc();
            List<EthtSvcInstances> ethtSvcInstances = new ArrayList<>();
            for (EthSrvInstance ethSrvInstance : ethSrvInstances) {
                EthtSvcInstances ethtSvcInstance = new DefaultEthtSvcInstances();
                String ethSrvName = ethSrvInstance.getEthSrvName();
                ethtSvcInstance.ethtSvcName(ethSrvName);
                String ethSrvDescr = ethSrvInstance.getEthSrvDescr();
                ethtSvcInstance.ethtSvcDescr(ethSrvDescr);
                ServiceTypeTypedef serviceTypeTypedef = new ServiceTypeTypedef(RmpSvc.class);
                ethtSvcInstance.ethtSvcType(serviceTypeTypedef);
                ethtSvcInstance.adminStatus(TunnelAdminStateUp.class);
                TeTopologyIdentifier teTopologyIdentifier = new DefaultTeTopologyIdentifier();
                teTopologyIdentifier.clientId(new TeGlobalId(ethSrvInstance.getClientId()));
                teTopologyIdentifier.providerId(new TeGlobalId(ethSrvInstance.getProviderId()));
                teTopologyIdentifier.topologyId(new TeTopologyId(new TeTopologyIdUnion(ethSrvInstance.getTopologyId())));
                ethtSvcInstance.teTopologyIdentifier(teTopologyIdentifier);
                Underlay underlay = new DefaultUnderlay();
                DefaultFrameBase defaultFrameBase = new DefaultFrameBase();
                List<OtnTunnels> otnTunnels = new ArrayList<>();
                if (ethSrvInstance.getOtnTunnels() != null && !ethSrvInstance.getOtnTunnels().isEmpty()) {
                    for (TsTunnel tsTunnel : ethSrvInstance.getOtnTunnels()) {
                        DefaultOtnTunnels defaultOtnTunnels = new DefaultOtnTunnels();
                        defaultOtnTunnels.name(tsTunnel.name());
                        otnTunnels.add(defaultOtnTunnels);
                    }
                }
                defaultFrameBase.otnTunnels(otnTunnels);
                underlay.technology(defaultFrameBase);
                ethtSvcInstance.underlay(underlay);
                Resilience resilience = new DefaultResilience();
                Protection protection = new DefaultProtection();
                protection.enable(true);
                protection.holdOffTime(0);
                protection.protectionReversionDisable(true);
                protection.protectionType(LspProtectionUnprotected.class);
                protection.waitToRevert(0);
                resilience.protection(protection);
                ethtSvcInstance.resilience(resilience);
                List<EthtSvcEndPoints> ethtSvcEndPoints = new ArrayList<>();
                if (ethSrvInstance.getEthSrvEndPoints() != null && !ethSrvInstance.getEthSrvEndPoints().isEmpty()) {
                    for (EthSrvEndPoint ethSrvEndPoint : ethSrvInstance.getEthSrvEndPoints()) {
                        EthtSvcEndPoints ethtSvcEndPoint = new DefaultEthtSvcEndPoints();
                        ethtSvcEndPoint.ethtSvcEndPointName(ethSrvEndPoint.getEthtSvcEndPointName());
                        List<EthtSvcAccessPoints> ethtSvcAccessPoints = new ArrayList<>();
                        if (ethSrvEndPoint.getEthSrvAccessPoints() != null && !ethSrvEndPoint.getEthSrvAccessPoints().isEmpty()) {
                            for (EthSrvAccessPoint ethSrvAccessPoint : ethSrvEndPoint.getEthSrvAccessPoints()) {
                                EthtSvcAccessPoints ethtSvcAccessPoint = new DefaultEthtSvcAccessPoints();
                                ethtSvcAccessPoint.accessPointId(ethSrvAccessPoint.getAccessPointId());
                                ethtSvcAccessPoint.accessNodeId(new TeNodeId(new DottedQuad(ethSrvAccessPoint.getAccessNodeId())));
                                ethtSvcAccessPoint.accessLtpId(new TeTpId(new TeTpIdUnion(ethSrvAccessPoint.getAccessLtpId())));
                                ethtSvcAccessPoints.add(ethtSvcAccessPoint);
                            }
                        }
                        ethtSvcEndPoint.ethtSvcAccessPoints(ethtSvcAccessPoints);
                        ethtSvcEndPoint.serviceClassificationType(VlanClassification.class);
                        ServiceClassification serviceClassification = new DefaultVlanClassification();
                        OuterTag outerTag = new DefaultOuterTag();
                        outerTag.tagType(new EthTagClassify(ClassifyCvlan.class));
                        IndividualVlan individualVlan = new DefaultIndividualVlan();
                        individualVlan.vlanValue(new Vlanid(ethSrvEndPoint.getVlanValue()));
                        outerTag.individualBundlingVlan(individualVlan);
                        ((DefaultVlanClassification) serviceClassification).outerTag(outerTag);
                        ethtSvcEndPoint.serviceClassification(serviceClassification);
                        Direction direction = new DefaultSymmetrical();
                        IngressEgressBandwidthProfile ingressEgressBandwidthProfile = new DefaultIngressEgressBandwidthProfile();
                        Style style = new DefaultValue();
                        ((DefaultValue) style).bandwidthProfileType(new BandwidthProfileTypeTypedef(Mef10Bwp.class));
                        ((DefaultValue) style).cir(ethSrvEndPoint.getCIR());
                        ((DefaultValue) style).eir(ethSrvEndPoint.getEIR());
                        ingressEgressBandwidthProfile.style(style);
                        ((DefaultSymmetrical) direction).ingressEgressBandwidthProfile(ingressEgressBandwidthProfile);
                        ethtSvcEndPoint.direction(direction);
                        ethtSvcEndPoints.add(ethtSvcEndPoint);
                    }
                }
                ethtSvcInstance.ethtSvcEndPoints(ethtSvcEndPoints);
                ethtSvcInstances.add(ethtSvcInstance);
            }
            defaultEthtSvc.ethtSvcInstances(ethtSvcInstances);
            return defaultEthtSvc;
        } else {
            return new DefaultEthtSvc();
        }
    }

    private OduResource teBandwidthToOduResource(TeBandwidth teBandwidth) {
        Technology otn = teBandwidth.technology();
        short odu0s = 0, odu1s = 0, odu2s = 0, odu2es = 0, odu3s = 0, odu4s = 0, oduFlex = 0;
        short numOfThisOdu = 1;
        for (Odulist elemOdu : ((DefaultOtn) otn).odulist()) {
            if (elemOdu.oduType().isAssignableFrom(Odu0.class)) {
                odu0s = (short) elemOdu.number();
            } else if (elemOdu.oduType().isAssignableFrom(Odu1.class)) {
                odu1s = (short) elemOdu.number();
            } else if (elemOdu.oduType().isAssignableFrom(Odu2.class)) {
                odu2s = (short) elemOdu.number();
            } else if (elemOdu.oduType().isAssignableFrom(Odu2e.class)) {
                odu2es = (short) elemOdu.number();
            } else if (elemOdu.oduType().isAssignableFrom(Odu3.class)) {
                odu3s = (short) elemOdu.number();
            } else if (elemOdu.oduType().isAssignableFrom(Odu4.class)) {
                odu4s = (short) elemOdu.number();
            } else if (elemOdu.oduType().isAssignableFrom(Oduflex.class)) {
                oduFlex = (short) elemOdu.number();
            }
        }
        return new OduResource(odu0s, odu1s, odu2s, odu2es, odu3s, odu4s, oduFlex,
                null, null); //TODO: TribPorts and TribSlots
    }

    private OduResource teBandwidth2OduResource(TeBandwidth teBandwidth) {
        Technology otn = teBandwidth.technology();
        short odu0s = 0, odu1s = 0, odu2s = 0, odu2es = 0, odu3s = 0, odu4s = 0, oduFlex = 0;
        short numOfThisOdu = 1;
        Otn elemOdu = ((org.onosproject.yang.gen.v11.ietfotntunnel.rev20210625.ietfotntunnel.te.tunnels.tunnel.tebandwidth.technology.augmentedtetechnology.DefaultOtn) otn).otn();
        if (elemOdu.oduType().isAssignableFrom(Odu0.class)) {
            odu0s = PncUtils.byteArray2Short(elemOdu.valueLeafFlags().toByteArray());
        } else if (elemOdu.oduType().isAssignableFrom(Odu1.class)) {
            odu1s = PncUtils.byteArray2Short(elemOdu.valueLeafFlags().toByteArray());
        } else if (elemOdu.oduType().isAssignableFrom(Odu2.class)) {
            odu2s = PncUtils.byteArray2Short(elemOdu.valueLeafFlags().toByteArray());
        } else if (elemOdu.oduType().isAssignableFrom(Odu2e.class)) {
            odu2es = PncUtils.byteArray2Short(elemOdu.valueLeafFlags().toByteArray());
        } else if (elemOdu.oduType().isAssignableFrom(Odu3.class)) {
            odu3s = PncUtils.byteArray2Short(elemOdu.valueLeafFlags().toByteArray());
        } else if (elemOdu.oduType().isAssignableFrom(Odu4.class)) {
            odu4s = PncUtils.byteArray2Short(elemOdu.valueLeafFlags().toByteArray());
        } else if (elemOdu.oduType().isAssignableFrom(Oduflex.class)) {
            oduFlex = PncUtils.byteArray2Short(elemOdu.valueLeafFlags().toByteArray());
        }
        return new OduResource(odu0s, odu1s, odu2s, odu2es, odu3s, odu4s, oduFlex,
                null, null); //TODO: TribPorts and TribSlots
    }

    //Transfer tslink to yanglink for otn tunnel deletion
    private static DefaultLink tsLink2YangLinkForTunnelDeletion(String sliceId, TsLink tslink, OduResource rezvdOdu,
                                                                Network yangNetwork, Networks yangNetworks, boolean isEdge, int tpn) {
        checkNotNull(tslink, "tsLink2YangLinkForTunnelDeletion: tslink is null");
        LinkId yangLinkId = LinkId.fromString(tslink.id());
        DefaultLink yangLink = new DefaultLink();
        yangLink.linkId(yangLinkId);

        //Update link source
        if (tslink.src() != null) {
            DefaultSource yangLinkSource = new DefaultSource();
            yangLinkSource.sourceNode(NodeId.fromString(tslink.src().tsDeviceId().toString()));
            yangLinkSource.sourceTp(TpId.fromString(tslink.src().ltPointId().toString()));
            yangLink.source(yangLinkSource);
        }
        //Update link destination
        if (tslink.dst() != null) {
            DefaultDestination yangLinkDest = new DefaultDestination();
            yangLinkDest.destNode(NodeId.fromString(tslink.dst().tsDeviceId().toString()));
            yangLinkDest.destTp(TpId.fromString(tslink.dst().ltPointId().toString()));
            yangLink.destination(yangLinkDest);
        }

        //Update te link attributes
        TeLinkAttributes yangLinkAttr = null;
        Link yangLinkSource = getLinkTeByLinkId(tslink.id(), yangNetwork, yangNetworks);

        if (((DefaultLink) yangLinkSource).augmentations() != null
                && !((DefaultLink) yangLinkSource).augmentations().isEmpty()) {
            DefaultAugmentedNtLink yangLinkAugment =
                    ((DefaultLink) yangLinkSource).augmentation(DefaultAugmentedNtLink.class);
            if (yangLinkAugment != null && yangLinkAugment.te() != null) {
                yangLinkAttr =
                        yangLinkAugment.te().teLinkAttributes();
                if (yangLinkAttr == null) {
                    log.error("Yanglink {} doesn't have yangLinkAttributes", tslink.id());
                    return null;
                }
            }
        }
        yangLinkAttr = updateYangLinkAttrForTunnelDeletion(sliceId, yangLinkAttr, tslink.id(), rezvdOdu, isEdge, tpn);

        DefaultTe yangLinkTe = new DefaultTe();
        yangLinkTe.teLinkAttributes(yangLinkAttr);

        DefaultAugmentedNtLink yangLinkAug = new DefaultAugmentedNtLink();
        yangLinkAug.te(yangLinkTe);
        yangLink.addAugmentation(yangLinkAug);
        return yangLink;
    }

    //Transfer tslink to yanglink for otn tunnel creation
    private static DefaultLink tsLink2YangLinkForTunnelCreation(String sliceId, TsLink tslink, OduResource rezvdOdu,
                                                                Network yangNetwork, Networks yangNetworks, boolean isEdgeLink,
                                                                int tpn) {
        checkNotNull(tslink, "tsLink2YangLinkForTunnelCreation");
        LinkId yangLinkId = LinkId.fromString(tslink.id());
        DefaultLink yangLink = new DefaultLink();
        yangLink.linkId(yangLinkId);

        //Update link source
        if (tslink.src() != null) {
            DefaultSource yangLinkSource = new DefaultSource();
            yangLinkSource.sourceNode(NodeId.fromString(tslink.src().tsDeviceId().toString()));
            yangLinkSource.sourceTp(TpId.fromString(tslink.src().ltPointId().toString()));
            yangLink.source(yangLinkSource);
        }
        //Update link destination
        if (tslink.dst() != null) {
            DefaultDestination yangLinkDest = new DefaultDestination();
            yangLinkDest.destNode(NodeId.fromString(tslink.dst().tsDeviceId().toString()));
            yangLinkDest.destTp(TpId.fromString(tslink.dst().ltPointId().toString()));
            yangLink.destination(yangLinkDest);
        }

        //Update te link attributes
        TeLinkAttributes yangLinkAttr = null;
        Link yangLinkSource = getLinkTeByLinkId(tslink.id(), yangNetwork, yangNetworks);

        if (((DefaultLink) yangLinkSource).augmentations() != null
                && !((DefaultLink) yangLinkSource).augmentations().isEmpty()) {
            DefaultAugmentedNtLink yangLinkAugment =
                    ((DefaultLink) yangLinkSource).augmentation(DefaultAugmentedNtLink.class);
            if (yangLinkAugment != null && yangLinkAugment.te() != null) {
                yangLinkAttr =
                        yangLinkAugment.te().teLinkAttributes();
                if (yangLinkAttr == null) {
                    log.error("Yanglink {} doesn't have yangLinkAttributes", tslink.id());
                    return null;
                }
            }
        }
        yangLinkAttr = updateYangLinkAttrForTunnelPvsn(sliceId, yangLinkAttr, tslink.id(), rezvdOdu, isEdgeLink, tpn);

        DefaultTe yangLinkTe = new DefaultTe();
        yangLinkTe.teLinkAttributes(yangLinkAttr);

        DefaultAugmentedNtLink yangLinkAug = new DefaultAugmentedNtLink();
        yangLinkAug.te(yangLinkTe);
        yangLink.addAugmentation(yangLinkAug);
        return yangLink;
    }

    //Transfer tslink to yanglink for optical slice creation rpc
    private static DefaultLink tsLink2YangLinkForSliceCreation(String sliceId, TsLink tslink, OduResource oduResource,
                                                               Network yangNetwork, Networks yangNetworks) {
        checkNotNull(tslink, "tsLink2YangLinkForSliceCreation: tslink is null");
        LinkId yangLinkId = LinkId.fromString(tslink.id());
        DefaultLink yangLink = new DefaultLink();
        yangLink.linkId(yangLinkId);

        //Update link source
        if (tslink.src() != null) {
            DefaultSource yangLinkSource = new DefaultSource();
            yangLinkSource.sourceNode(NodeId.fromString(tslink.src().tsDeviceId().toString()));
            yangLinkSource.sourceTp(TpId.fromString(tslink.src().ltPointId().toString()));
            yangLink.source(yangLinkSource);
        }
        //Update link destination
        if (tslink.dst() != null) {
            DefaultDestination yangLinkDest = new DefaultDestination();
            yangLinkDest.destNode(NodeId.fromString(tslink.dst().tsDeviceId().toString()));
            yangLinkDest.destTp(TpId.fromString(tslink.dst().ltPointId().toString()));
            yangLink.destination(yangLinkDest);
        }

        //Update te link attributes
        TeLinkAttributes yangLinkAttr = null;
        Link yangLinkSource = getLinkTeByLinkId(tslink.id(), yangNetwork, yangNetworks);

        if (((DefaultLink) yangLinkSource).augmentations() != null
                && !((DefaultLink) yangLinkSource).augmentations().isEmpty()) {
            DefaultAugmentedNtLink yangLinkAugment =
                    ((DefaultLink) yangLinkSource).augmentation(DefaultAugmentedNtLink.class);
            if (yangLinkAugment != null && yangLinkAugment.te() != null) {
                yangLinkAttr =
                        yangLinkAugment.te().teLinkAttributes();
                if (yangLinkAttr == null) {
                    log.error("Yanglink {} doesn't have yangLinkAttributes", tslink.id());
                    return null;
                }
            }
        }
        yangLinkAttr = updateYangLinkAttrForSlicePvsn(sliceId, yangLinkAttr, tslink.id(), oduResource);

        DefaultTe yangLinkTe = new DefaultTe();
        yangLinkTe.teLinkAttributes(yangLinkAttr);

        DefaultAugmentedNtLink yangLinkAug = new DefaultAugmentedNtLink();
        yangLinkAug.te(yangLinkTe);
        yangLink.addAugmentation(yangLinkAug);
        return yangLink;
    }

    private static TeLinkAttributes updateYangLinkAttrForTunnelPvsn(String sliceId, TeLinkAttributes yangLinkAttr,
                                                                    String id, OduResource rezvedOdu, boolean isEdge,
                                                                    int tpn) {


        //Update label-restriction
        if (isEdge && !yangLinkAttr.labelRestrictions().labelRestriction().isEmpty()) {
            //Note: Only support single unreservedBandwidth/tebandwidth
            HexString bitmap = yangLinkAttr.labelRestrictions().labelRestriction().get(0).rangeBitmap();
            byte[] bytemap = new byte[bitmap.toString().length() / 2];
            for (int i = 0; i < bytemap.length; i++) {
                int index = i * 2;
                int j = Integer.parseInt(bitmap.toString().substring(index, index + 2), 16);
                bytemap[i] = (byte) j;
            }
            PncUtils.setBitsZero(bytemap, tpn, rezvedOdu.equivalentToNumOfOdu0s());
            yangLinkAttr.labelRestrictions().labelRestriction().get(0).rangeBitmap(bitmap);
        }

        //Write optical slice unreservedBandwidth info into te/te-link-attributes/unreservedBandwidth
        //Note: Only support single unreservedBandwidth/tebandwidth
        Technology otnAug = yangLinkAttr.unreservedBandwidth().get(0).teBandwidth().technology();
        checkNotNull(otnAug, "yanglinkAttr/unreservedBandwidth/tebandwidth/technology is null");
        short priority = yangLinkAttr.unreservedBandwidth().get(0).priority();
        TeBandwidth teBandWidth = new DefaultTeBandwidth();
        if (otnAug != null &&
                (otnAug instanceof org.onosproject.yang.gen.v11.ietfotntopology.rev20210222.ietfotntopology.networks.network.
                        link.te.telinkattributes.unreservedbandwidth.tebandwidth.technology.augmentedtettechnology.DefaultOtn)) {
            org.onosproject.yang.gen.v11.ietfotntopology.rev20210222.ietfotntopology.networks.network.
                    link.te.telinkattributes.unreservedbandwidth.tebandwidth.technology.augmentedtettechnology.DefaultOtn otnAugUpdated =
                    new org.onosproject.yang.gen.v11.ietfotntopology.rev20210222.ietfotntopology.networks.network.
                            link.te.telinkattributes.unreservedbandwidth.tebandwidth.technology.augmentedtettechnology.DefaultOtn();

            //Update unreservedbandwidth/te-bandwidth/technology/slicelist
            org.onosproject.yang.gen.v11.ietfotntopology.rev20210222.ietfotntopology.otnslicelinkbandwidth.DefaultSlicelist
                    newSliceElem = null;
            for (org.onosproject.yang.gen.v11.ietfotntopology.rev20210222.ietfotntopology.otnslicelinkbandwidth.Slicelist elemSlice :
                    ((org.onosproject.yang.gen.v11.ietfotntopology.rev20210222.ietfotntopology.networks.network.
                            link.te.telinkattributes.unreservedbandwidth.tebandwidth.technology.augmentedtettechnology.DefaultOtn) otnAug).slicelist()) {
                if (elemSlice.sliceId().equals(sliceId)) {

                    if (elemSlice.odu0Number() >= (int) rezvedOdu.equivalentToNumOfOdu0s()) {
                        newSliceElem =
                                new org.onosproject.yang.gen.v11.ietfotntopology.rev20210222.ietfotntopology.otnslicelinkbandwidth.DefaultSlicelist();
                        newSliceElem.sliceId(sliceId);
                        newSliceElem.odu0Number(elemSlice.odu0Number() - (int) rezvedOdu.equivalentToNumOfOdu0s());
                    } else {
                        throw new RuntimeException("Target slice does't have enough bandwidth");
                    }
                    ;
                }
            }
            if (newSliceElem != null) {
                otnAugUpdated.slicelist(((org.onosproject.yang.gen.v11.ietfotntopology.rev20210222.ietfotntopology.networks.network.
                        link.te.telinkattributes.unreservedbandwidth.tebandwidth.technology.augmentedtettechnology.DefaultOtn) otnAug).slicelist());
                otnAugUpdated.addToSlicelist(newSliceElem);
            } else {
                throw new RuntimeException("Target slice doesn't exist");
            }

            //Update OduList
            for (org.onosproject.yang.gen.v11.ietflayer1types.rev20210219.ietflayer1types.otnlinkbandwidth.Odulist elemOdu :
                    ((org.onosproject.yang.gen.v11.ietfotntopology.rev20210222.ietfotntopology.networks.network.
                            link.te.telinkattributes.unreservedbandwidth.tebandwidth.technology.augmentedtettechnology.DefaultOtn) otnAug).odulist()) {
                if (elemOdu.oduType().isAssignableFrom(Odu0.class)) {
                    elemOdu.number((short) elemOdu.number() - (short) rezvedOdu.equivalentToNumOfOdu0s());
                }
                ;
                otnAugUpdated.addToOdulist(elemOdu);
            }

            teBandWidth.technology(otnAugUpdated);
            org.onosproject.yang.gen.v11.ietftetopology.rev20200806.ietftetopology.telinkinfoattributes.DefaultUnreservedBandwidth urLkBw =
                    new org.onosproject.yang.gen.v11.ietftetopology.rev20200806.ietftetopology.telinkinfoattributes.DefaultUnreservedBandwidth();
            urLkBw.teBandwidth(teBandWidth);
            urLkBw.priority(priority);
            List<UnreservedBandwidth> uRsBwList = yangLinkAttr.unreservedBandwidth();
            uRsBwList.clear();
            uRsBwList.add(urLkBw);
            yangLinkAttr.unreservedBandwidth(uRsBwList);
        }
        return yangLinkAttr;
    }

    private static TeLinkAttributes updateYangLinkAttrForTunnelDeletion(String sliceId, TeLinkAttributes yangLinkAttr,
                                                                        String id, OduResource rezvedOdu,
                                                                        boolean isEdge,
                                                                        int tpn) {
        {
            //Update label-restriction
            if (isEdge && !yangLinkAttr.labelRestrictions().labelRestriction().isEmpty()) {
                //Note: Only support single unreservedBandwidth/tebandwidth
                HexString bitmap = yangLinkAttr.labelRestrictions().labelRestriction().get(0).rangeBitmap();
                byte[] bytemap = new byte[bitmap.toString().length() / 2];
                for (int i = 0; i < bytemap.length; i++) {
                    int index = i * 2;
                    int j = Integer.parseInt(bitmap.toString().substring(index, index + 2), 16);
                    bytemap[i] = (byte) j;
                }
                PncUtils.setBitsOne(bytemap, tpn, rezvedOdu.equivalentToNumOfOdu0s());
                yangLinkAttr.labelRestrictions().labelRestriction().get(0).rangeBitmap(bitmap);
            }


            //Write optical slice unreservedBandwidth info into te/te-link-attributes/unreservedBandwidth
            //Note: Only support single unreservedBandwidth/tebandwidth
            Technology otnAug = yangLinkAttr.unreservedBandwidth().get(0).teBandwidth().technology();
            checkNotNull(otnAug, "yanglinkAttr/unreservedBandwidth/tebandwidth/technology is null");
            short priority = yangLinkAttr.unreservedBandwidth().get(0).priority();
            TeBandwidth teBandWidth = new DefaultTeBandwidth();
            if (otnAug != null &&
                    (otnAug instanceof org.onosproject.yang.gen.v11.ietfotntopology.rev20210222.ietfotntopology.networks.network.
                            link.te.telinkattributes.unreservedbandwidth.tebandwidth.technology.augmentedtettechnology.DefaultOtn)) {
                org.onosproject.yang.gen.v11.ietfotntopology.rev20210222.ietfotntopology.networks.network.
                        link.te.telinkattributes.unreservedbandwidth.tebandwidth.technology.augmentedtettechnology.DefaultOtn otnAugUpdated =
                        new org.onosproject.yang.gen.v11.ietfotntopology.rev20210222.ietfotntopology.networks.network.
                                link.te.telinkattributes.unreservedbandwidth.tebandwidth.technology.augmentedtettechnology.DefaultOtn();

                //Update unreservedbandwidth/te-bandwidth/technology/slicelist
                org.onosproject.yang.gen.v11.ietfotntopology.rev20210222.ietfotntopology.otnslicelinkbandwidth.DefaultSlicelist
                        newSliceElem = null;
                for (org.onosproject.yang.gen.v11.ietfotntopology.rev20210222.ietfotntopology.otnslicelinkbandwidth.Slicelist elemSlice :
                        ((org.onosproject.yang.gen.v11.ietfotntopology.rev20210222.ietfotntopology.networks.network.
                                link.te.telinkattributes.unreservedbandwidth.tebandwidth.technology.augmentedtettechnology.DefaultOtn) otnAug).slicelist()) {
                    if (elemSlice.sliceId().equals(sliceId)) {

                        newSliceElem =
                                new org.onosproject.yang.gen.v11.ietfotntopology.rev20210222.ietfotntopology.otnslicelinkbandwidth.DefaultSlicelist();
                        newSliceElem.sliceId(sliceId);
                        newSliceElem.odu0Number(elemSlice.odu0Number() + (int) rezvedOdu.equivalentToNumOfOdu0s());
                    }
                }
                if (newSliceElem != null) {
                    otnAugUpdated.slicelist(((org.onosproject.yang.gen.v11.ietfotntopology.rev20210222.ietfotntopology.networks.network.
                            link.te.telinkattributes.unreservedbandwidth.tebandwidth.technology.augmentedtettechnology.DefaultOtn) otnAug).slicelist());
                    otnAugUpdated.addToSlicelist(newSliceElem);
                } else {
                    throw new RuntimeException("Target slice doesn't exist");
                }

//                Update OduList
                for (org.onosproject.yang.gen.v11.ietflayer1types.rev20210219.ietflayer1types.otnlinkbandwidth.Odulist elemOdu :
                        ((org.onosproject.yang.gen.v11.ietfotntopology.rev20210222.ietfotntopology.networks.network.
                                link.te.telinkattributes.unreservedbandwidth.tebandwidth.technology.augmentedtettechnology.DefaultOtn) otnAug).odulist()) {
                    if (elemOdu.oduType().isAssignableFrom(Odu0.class)) {
                        elemOdu.number((short) elemOdu.number() + (short) rezvedOdu.equivalentToNumOfOdu0s());
                    }
                    ;
                    otnAugUpdated.addToOdulist(elemOdu);
                }

                teBandWidth.technology(otnAugUpdated);
                org.onosproject.yang.gen.v11.ietftetopology.rev20200806.ietftetopology.telinkinfoattributes.DefaultUnreservedBandwidth urLkBw =
                        new org.onosproject.yang.gen.v11.ietftetopology.rev20200806.ietftetopology.telinkinfoattributes.DefaultUnreservedBandwidth();
                urLkBw.teBandwidth(teBandWidth);
                urLkBw.priority(priority);
                List<UnreservedBandwidth> uRsBwList = yangLinkAttr.unreservedBandwidth();
                uRsBwList.clear();
                uRsBwList.add(urLkBw);
                yangLinkAttr.unreservedBandwidth(uRsBwList);
            }
            return yangLinkAttr;
        }
    }

    private static TeLinkAttributes updateYangLinkAttrForSlicePvsn(String sliceId, TeLinkAttributes yangLinkAttr, String id, OduResource oduResource) {

        //Write optical slice maxLinkBandwidth info into te/te-link-attributes/maxLinkBandwidth
        Technology otnAug = yangLinkAttr.maxLinkBandwidth().teBandwidth().technology();
        TeBandwidth teBandWidth = new DefaultTeBandwidth();
        if (otnAug != null &&
                (otnAug instanceof DefaultOtn)) {

            DefaultOtn otnAugUpdated = new DefaultOtn();

            otnAugUpdated.odulist(((DefaultOtn) otnAug).odulist());
            org.onosproject.yang.gen.v11.ietfotntopology.rev20210222.ietfotntopology.otnslicelinkbandwidth.DefaultSlicelist
                    newSliceElem =
                    new org.onosproject.yang.gen.v11.ietfotntopology.rev20210222.ietfotntopology.otnslicelinkbandwidth.DefaultSlicelist();
            newSliceElem.sliceId(sliceId);
            newSliceElem.odu0Number((int) oduResource.odu0s());

            ((DefaultOtn) otnAugUpdated).addToSlicelist(newSliceElem);
            teBandWidth.technology(otnAugUpdated);
            org.onosproject.yang.gen.v11.ietftetopology.rev20200806.ietftetopology.telinkinfoattributes.DefaultMaxLinkBandwidth mxLkBw =
                    new org.onosproject.yang.gen.v11.ietftetopology.rev20200806.ietftetopology.telinkinfoattributes.DefaultMaxLinkBandwidth();
            mxLkBw.teBandwidth(teBandWidth);
            yangLinkAttr.maxLinkBandwidth(mxLkBw);
        }

        //Write optical slice unreservedBandwidth info into te/te-link-attributes/unreservedBandwidth
        //Note: Only support single unreservedBandwidth/tebandwidth
        otnAug = yangLinkAttr.unreservedBandwidth().get(0).teBandwidth().technology();
        short priority = yangLinkAttr.unreservedBandwidth().get(0).priority();
        teBandWidth = new DefaultTeBandwidth();
        if (otnAug != null &&
                (otnAug instanceof org.onosproject.yang.gen.v11.ietfotntopology.rev20210222.ietfotntopology.networks.network.
                        link.te.telinkattributes.unreservedbandwidth.tebandwidth.technology.augmentedtettechnology.DefaultOtn)) {
            org.onosproject.yang.gen.v11.ietfotntopology.rev20210222.ietfotntopology.networks.network.
                    link.te.telinkattributes.unreservedbandwidth.tebandwidth.technology.augmentedtettechnology.DefaultOtn otnAugUpdated =
                    new org.onosproject.yang.gen.v11.ietfotntopology.rev20210222.ietfotntopology.networks.network.
                            link.te.telinkattributes.unreservedbandwidth.tebandwidth.technology.augmentedtettechnology.DefaultOtn();

            otnAugUpdated.odulist(((org.onosproject.yang.gen.v11.ietfotntopology.rev20210222.ietfotntopology.networks.network.
                    link.te.telinkattributes.unreservedbandwidth.tebandwidth.technology.augmentedtettechnology.DefaultOtn) otnAug).odulist());

            org.onosproject.yang.gen.v11.ietfotntopology.rev20210222.ietfotntopology.otnslicelinkbandwidth.DefaultSlicelist
                    newSliceElem =
                    new org.onosproject.yang.gen.v11.ietfotntopology.rev20210222.ietfotntopology.otnslicelinkbandwidth.DefaultSlicelist();
            newSliceElem.sliceId(sliceId);
            newSliceElem.odu0Number((int) oduResource.odu0s());

            otnAugUpdated.addToSlicelist(newSliceElem);
            teBandWidth.technology(otnAugUpdated);
            org.onosproject.yang.gen.v11.ietftetopology.rev20200806.ietftetopology.telinkinfoattributes.DefaultUnreservedBandwidth urLkBw =
                    new org.onosproject.yang.gen.v11.ietftetopology.rev20200806.ietftetopology.telinkinfoattributes.DefaultUnreservedBandwidth();
            urLkBw.teBandwidth(teBandWidth);
            urLkBw.priority(priority);
            List<UnreservedBandwidth> uRsBwList = yangLinkAttr.unreservedBandwidth();
            uRsBwList.clear();
            uRsBwList.add(urLkBw);
            yangLinkAttr.unreservedBandwidth(uRsBwList);
        }
        return yangLinkAttr;
    }

    public static Link getLinkTeByLinkId(String id, Network yangNetwork, Networks yangNetworks) {

        if (((DefaultNetwork) yangNetwork).augmentation(DefaultAugmentedNwNetwork.class) != null) { //check augmentations
            DefaultAugmentedNwNetwork augmentLink =
                    ((DefaultNetwork) yangNetwork).augmentation(DefaultAugmentedNwNetwork.class);
            for (Link yangLink : augmentLink.link()) {
                if (yangLink.linkId().uri().string().equals(id)) {
                    return yangLink;
                }
            }

        }
        return null;
    }

    //Reading Info from datastore
    //Get a list of TeNodeKey within this yang otn network
    public static List<TeNodeKey> getOtnNodes(Network yangNetwork) {
        List<Node> yangNodes;
        List<TeNodeKey> tenodeKeys = null;
        if ((yangNodes = yangNetwork.node()) != null) {
            for (Node node : yangNodes) {
                if (null == tenodeKeys) {
                    tenodeKeys = new ArrayList<>(yangNodes.size());
                }
                tenodeKeys.add(TeNodeKey.of(node.nodeId().uri().string()));
            }
        }
        return tenodeKeys;
    }

    public static Map<TeNodeKey, TsNodeInfo> getTsNodeInfo(Network yangNetwork) {
        Map<TeNodeKey, TsNodeInfo> ttpMap = new HashMap<>();
        for (Node yangNode : yangNetwork.node()) {
            TeNodeKey tenodeKey = TeNodeKey.of(yangNode.nodeId().uri().string());
            TsNodeInfo tsNodeInfo = new TsNodeInfo();

            org.onosproject.yang.gen.v11.ietftetopology.rev20200806.ietftetopology.networks
                    .network.node.DefaultAugmentedNwNode yangNodeAugment = ((DefaultNode) yangNode)
                    .augmentation(org.onosproject.yang.gen.v11.ietftetopology.rev20200806.ietftetopology.networks
                            .network.node.DefaultAugmentedNwNode.class);
            if (yangNodeAugment != null
                    && yangNodeAugment.te() != null) {
                Te yangNodeAugTe = yangNodeAugment.te();
                if (yangNodeAugTe.tunnelTerminationPoint() != null) {
                    for (TunnelTerminationPoint yangTtp : yangNodeAugTe.tunnelTerminationPoint()) {
                        String ttpId = new String(yangTtp.tunnelTpId());
                        LocalLinkConnectivities localLinkConnectivities = yangTtp.localLinkConnectivities();
                        if (localLinkConnectivities != null) {
                            DefaultAugmentedLocalLinkConnectivities augmentedLocalLinkConnectivities = localLinkConnectivities.augmentation(DefaultAugmentedLocalLinkConnectivities.class);
                            if (augmentedLocalLinkConnectivities != null) {
                                for (LocalLinkConnectivity localLinkConnectivity : augmentedLocalLinkConnectivities.localLinkConnectivity()) {
                                    if (localLinkConnectivity.isAllowed()) {
                                        String linkTpRef = localLinkConnectivity.linkTpRef().toString();
                                        tsNodeInfo.addToTtpMap(ttpId, LtpId.ltpId(linkTpRef));
                                    }
                                }
                            }
                        }

                    }
                }
            }
            ttpMap.put(tenodeKey, tsNodeInfo);
        }
        return ttpMap;
    }

    public static Map<TeNodeKey, List<TsLink>> getOtnEdgeLinks(Network yangNetwork) {
        Map<TeNodeKey, List<TsLink>> edgeLinkMap = new HashMap<TeNodeKey, List<TsLink>>();
        DefaultAugmentedNwNetwork augmentLink;
        //check augmentations
        if ((augmentLink = ((DefaultNetwork) yangNetwork).augmentation(DefaultAugmentedNwNetwork.class))
                != null) {
            for (Link link : augmentLink.link()) {
                if (link.source() == null ^ link.destination() == null) {
                    TsLink lk = makeTsLink(link);
                    TeNodeKey nodeKey;
                    List<TsLink> egLinks;
                    if (lk != null) {
                        switch (lk.type()) {
                            case INGRESS:
                                nodeKey = lk.dst().tsDeviceId();
                                egLinks = edgeLinkMap.get(nodeKey);
                                if (egLinks == null) {
                                    egLinks = new LinkedList<>();
                                }
                                egLinks.add(lk);
                                edgeLinkMap.put(nodeKey, egLinks);
                                break;
                            case EGRESS:
                                nodeKey = lk.src().tsDeviceId();
                                egLinks = edgeLinkMap.get(nodeKey);
                                if (egLinks == null) {
                                    egLinks = new LinkedList<>();
                                }
                                egLinks.add(lk);
                                edgeLinkMap.put(nodeKey, egLinks);
                                break;
                            default:
                                break;
                        }

                    }
                }
            }
        }
        return edgeLinkMap;
    }

    //Get a list of TsLink within this yang
    public static List<TsLink> getOtnInnerLinks(Network yangNetwork) {
        List<TsLink> links = null;
        DefaultAugmentedNwNetwork augmentLink;
        //check augmentations
        if ((augmentLink = ((DefaultNetwork) yangNetwork).augmentation(DefaultAugmentedNwNetwork.class))
                != null) {
            for (Link link : augmentLink.link()) {
                // Convert the Yang Link to a link
                if (links == null) {
                    links = new ArrayList<>();
                }

                if (link.source() != null && link.destination() != null) {
                    // This link contains both source and destination attributes
                    TsLink lk = makeTsLink(link);
                    links.add(lk);
                }

            }
        }
        return links;
    }

    ;

    public static PncOtnBandwidthProfile getBandWidthProfile(Network yangNetwork) {
        DefaultAugmentedNwNetwork augmentLink;
        PncOtnBandwidthProfile bandwidthProfile = new PncOtnBandwidthProfile();
        //check augmentations
        if ((augmentLink = ((DefaultNetwork) yangNetwork).augmentation(DefaultAugmentedNwNetwork.class))
                != null) {
            for (Link yangLink : augmentLink.link()) {
                // Convert the Yang Link to a link
                Source yangsrc = yangLink.source();
                Destination yangdst = yangLink.destination();
                if (yangsrc == null || yangdst == null) {
                    //Skip ingress and egress link,
                    continue;
                }
                //checkNotNull(yangsrc, "yanglink source attribute is missing");
                //checkNotNull(yangdst, "yanglink destination attribute is missing");
                String linkId = yangLink.linkId().uri().toString();
                TeNodeKey device1 = TeNodeKey.of(yangsrc.sourceNode().uri().toString());
                TeNodeKey device2 = TeNodeKey.of(yangdst.destNode().uri().toString());
                String ltp1 = yangsrc.sourceTp().uri().toString();
                String ltp2 = yangdst.destTp().uri().toString();
                LTPoint src = new LTPoint(device1, LtpId.ltpId(ltp1));
                LTPoint dst = new LTPoint(device2, LtpId.ltpId(ltp2));
                TsLinkKey linkKey = new TsLinkKey(src, dst, linkId);

                //Add bandwidth info into bandwidthProfile
                OduResource oduResource;
                DefaultLink defaultYangLink = (DefaultLink) yangLink;
                if (defaultYangLink.augmentations() != null
                        && !defaultYangLink.augmentations().isEmpty()) {
                    DefaultAugmentedNtLink yangLinkAugment =
                            defaultYangLink.augmentation(DefaultAugmentedNtLink.class);
                    if (yangLinkAugment != null && yangLinkAugment.te() != null) {
                        TeLinkAttributes yangLinkAttr =
                                yangLinkAugment.te().teLinkAttributes();
                        if (yangLinkAttr != null) {
                            oduResource = yangLinkAttr2TeOduResource(yangLinkAttr);
                            if (oduResource != null) {
                                bandwidthProfile.addUnreservedOdu(linkKey, oduResource);
                            }
                        }

                    }
                }
            }


        }
        return bandwidthProfile;
    }

    private static TsLink makeTsLink(Link yangLink) {
        TsLink curLink;
        Source yangsrc = yangLink.source();
        Destination yangdst = yangLink.destination();
        if (yangsrc != null && yangdst != null) {
            //checkNotNull(yangsrc, "yanglink source attribute is missing");
            //checkNotNull(yangdst, "yanglink destination attribute is missing");
            String linkId = yangLink.linkId().uri().toString();
            TeNodeKey device1 = TeNodeKey.of(yangsrc.sourceNode().uri().toString());
            TeNodeKey device2 = TeNodeKey.of(yangdst.destNode().uri().toString());
            String ltp1 = yangsrc.sourceTp().uri().toString();
            String ltp2 = yangdst.destTp().uri().toString();
            LTPoint src = new LTPoint(device1, LtpId.ltpId(ltp1));
            LTPoint dst = new LTPoint(device2, LtpId.ltpId(ltp2));

            //DefaultAnnotations.Builder annotationBuilder = DefaultAnnotations.builder();
            //TODO:If cost not set cost : default value case
            curLink = TsLink.builder().id(linkId).src(src).dst(dst).state(ACTIVE).build();
        } else if (yangsrc != null && yangdst == null) {
            // egress link
            String linkId = yangLink.linkId().uri().toString();
            TeNodeKey device1 = TeNodeKey.of(yangsrc.sourceNode().uri().toString());
            String ltp1 = yangsrc.sourceTp().uri().toString();
            LTPoint src = new LTPoint(device1, LtpId.ltpId(ltp1));
            curLink = TsLink.builder().id(linkId).src(src).type(org.onap.integration.actninterfacetools.sampleapp.mpiconverter.model.Link.Type.EGRESS).build();
        } else if (yangsrc == null && yangdst != null) {
            // ingress link
            String linkId = yangLink.linkId().uri().toString();
            TeNodeKey device2 = TeNodeKey.of(yangdst.destNode().uri().toString());
            String ltp2 = yangdst.destTp().uri().toString();
            LTPoint dst = new LTPoint(device2, LtpId.ltpId(ltp2));
            curLink = TsLink.builder().id(linkId).dst(dst).type(org.onap.integration.actninterfacetools.sampleapp.mpiconverter.model.Link.Type.INGRESS).build();

        } else {
            return null;
        }
        return curLink;
    }

    public static OduResource yangLinkAttr2TeOduResource(TeLinkAttributes yangTeLinkAttr) {
        if (yangTeLinkAttr == null ||
                yangTeLinkAttr.unreservedBandwidth() == null) {
            log.error("ERROR: yangLinkAttr2TeOduResource: {} is null",
                    yangTeLinkAttr == null ? "yangTeLinkAttr" : "yangTeLinkAttr.unreservedBandwidth");
            return null;
        }

        List<UnreservedBandwidth> urbwList = yangTeLinkAttr.unreservedBandwidth();
        UnreservedBandwidth urbw = urbwList.get(0);
        if (urbw == null || urbw.teBandwidth() == null || urbw.teBandwidth().technology() == null) {
            return null;
        }


        Technology otnAug = urbw.teBandwidth().technology();

        if (otnAug == null ||
                !(otnAug instanceof org.onosproject.yang.gen.v11.ietfotntopology.rev20210222.ietfotntopology.networks.network.link.te
                        .telinkattributes.unreservedbandwidth.tebandwidth.technology.augmentedtettechnology.DefaultOtn)) {
            return null;
        }

        short odu0s = 0, odu1s = 0, odu2s = 0, odu2es = 0, odu3s = 0, odu4s = 0, oduFlex = 0;
        org.onosproject.yang.gen.v11.ietfotntopology.rev20210222.ietfotntopology.networks.network.link.te
                .telinkattributes.unreservedbandwidth.tebandwidth
                .technology.augmentedtettechnology.DefaultOtn otn = (org.onosproject.yang.gen.v11.ietfotntopology.rev20210222.ietfotntopology.networks.network.link.te
                .telinkattributes.unreservedbandwidth.tebandwidth.technology.augmentedtettechnology.DefaultOtn) otnAug;

        for (Odulist elemOdu : otn.odulist()) {
            if (elemOdu.oduType().isAssignableFrom(Odu0.class)) {
                odu0s = (short) elemOdu.number();
            } else if (elemOdu.oduType().isAssignableFrom(Odu1.class)) {
                odu1s = (short) elemOdu.number();
            } else if (elemOdu.oduType().isAssignableFrom(Odu2.class)) {
                odu2s = (short) elemOdu.number();
            } else if (elemOdu.oduType().isAssignableFrom(Odu2e.class)) {
                odu2es = (short) elemOdu.number();
            } else if (elemOdu.oduType().isAssignableFrom(Odu3.class)) {
                odu3s = (short) elemOdu.number();
            } else if (elemOdu.oduType().isAssignableFrom(Odu4.class)) {
                odu4s = (short) elemOdu.number();
            } else if (elemOdu.oduType().isAssignableFrom(Oduflex.class)) {
                oduFlex = (short) elemOdu.number();
            }
        }

        return new OduResource(odu0s, odu1s, odu2s, odu2es, odu3s, odu4s, oduFlex,
                null, null); //TODO: TribPorts and TribSlots

    }


}

