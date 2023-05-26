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
package org.onap.integration.actninterfacetools.sampleapp.mpiconverter;

import com.google.common.net.InetAddresses;
import org.onap.integration.actninterfacetools.globalapi.ActnDataConverter;
import org.onap.integration.actninterfacetools.globalapi.CustomerOtnTopology;
import org.onap.integration.actninterfacetools.globalapi.CustomerOtnTunnel;
import org.onap.integration.actninterfacetools.globalapi.PncClient;
import org.onap.integration.actninterfacetools.globalapi.GlobalService;
import org.onap.integration.actninterfacetools.globalapi.ActnClientService;
import org.onap.integration.actninterfacetools.globalapi.CustomerEthService;
import org.onap.integration.actninterfacetools.globalapp.impl.GlobalServiceImpl;
import org.onap.integration.actninterfacetools.protocol.restconf.DefaultPncInstance;
import org.onap.integration.actninterfacetools.protocol.restconf.PncInstance;
import org.onap.integration.actninterfacetools.sampleapp.mpiconverter.converter.PncConverter;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class OssWorkThreadB extends Thread {
    private CountDownLatch latch;
    GlobalServiceImpl globalService;
    private static final Logger log = LoggerFactory.getLogger(OssWorkThreadA.class);

    public OssWorkThreadB(String name, CountDownLatch latch) {
        this.latch = latch;
        setName(name);
    }

    @Override
    public void run() {
        try {
            System.out.printf("[ %s ] created, blocked by the latch...\n", getName());
            latch.await();
            System.out.printf("[ %s ] starts at: %s\n", getName(), Instant.now());
            // do actual work here...
            PncInstance pncInstanceA = new DefaultPncInstance(InetAddresses.forString("192.168.198.10"), 18181, null, null, "http", null);
            PncInstance pncInstanceB = new DefaultPncInstance(InetAddresses.forString("192.168.198.10"), 28181, null, null, "http", null);
            ActnDataConverter actnDataConverter = new PncConverter();
            ActnClientService actnClientService = GlobalService.getActnClientSvc(actnDataConverter);
            PncClient pncClientA = actnClientService.getPncClient(pncInstanceA);
            PncClient pncClientB = actnClientService.getPncClient(pncInstanceB);
            testGetOtnTopology(pncClientA, pncClientB);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void testGetOtnTopology(PncClient pncClientA, PncClient pncClientB) throws Exception {
        CustomerOtnTopology customerOtnTopology = pncClientA.getNetworkTopology("providerId-10-clientId-0-topologyId-1");
        log.info("pncA: " + customerOtnTopology.toString());
        CustomerOtnTopology customerOtnTopologyB = pncClientB.getNetworkTopology("providerId-20-clientId-0-topologyId-1");
        log.info("pncB: " + customerOtnTopologyB.toString());
    }

    public void testGetOtnTunnel(PncClient pncClientA, PncClient pncClientB) throws Exception {
        CustomerOtnTunnel otnTunnel = pncClientA.getOtnTunnel("otntunnel-1");
        log.info("pncA: " + otnTunnel.toString());
        CustomerOtnTunnel otnTunnelB = pncClientB.getOtnTunnel("otntunnel-1");
        log.info("pncB: " + otnTunnelB.toString());
    }

    public void testCreateOtnTunnel(PncClient pncClientA, PncClient pncClientB) throws Exception {
        CustomerOtnTunnel customerOtnTunnel = buildTeOtnTunnel("otntunnel-1");
        pncClientA.createOtnTunnel(customerOtnTunnel);
        CustomerOtnTunnel customerOtnTunnelB = buildTeOtnTunnel2("otntunnel-1");
        pncClientB.createOtnTunnel(customerOtnTunnelB);
    }

    public void testDeleteOtnTunnel(PncClient pncClientA, PncClient pncClientB) throws Exception {
        pncClientA.deleteOtnTunnel("otntunnel-1");
        pncClientB.deleteOtnTunnel("otntunnel-1");
    }

    public void testUpdateOtnTunnel(PncClient pncClientA, PncClient pncClientB) throws Exception {
        CustomerOtnTunnel customerOtnTunnel = updateTeOtnTunnel("otntunnel-1");
        pncClientA.updateOtnTunnel(customerOtnTunnel, "otntunnel-1");
        CustomerOtnTunnel customerOtnTunnelB = updateTeOtnTunnel2("otntunnel-1");
        pncClientB.updateOtnTunnel(customerOtnTunnelB, "otntunnel-1");
    }

    public void testGetEthSvc(PncClient pncClientA, PncClient pncClientB) throws Exception {
        CustomerEthService ethService = pncClientA.getEthService("cll-instance-01");
        log.info("pncA: " + ethService.toString());
        CustomerEthService ethServiceB = pncClientB.getEthService("cll-instance-01");
        log.info("pncB: " + ethServiceB.toString());
    }

    public void testCreateEthSvc(PncClient pncClientA, PncClient pncClientB) throws Exception {
        CustomerEthService customerEthService = buildTeEthSrv("cll-instance-01");
        pncClientA.createEthService(customerEthService);
        CustomerEthService customerEthServiceB = buildTeEthSrv2("cll-instance-01");
        pncClientB.createEthService(customerEthServiceB);
    }

    public void testDeleteEthSvc(PncClient pncClientA, PncClient pncClientB) throws Exception {
        pncClientA.deleteEthService("cll-instance-01");
        pncClientB.deleteEthService("cll-instance-01");
    }

    public void testUpdateEthSvc(PncClient pncClientA, PncClient pncClientB) throws Exception {
        CustomerEthService customerEthService = updateTeEthSrv("cll-instance-01");
        pncClientA.updateEthService(customerEthService);
        CustomerEthService customerEthServiceB = updateTeEthSrv2("cll-instance-01");
        pncClientB.updateEthService(customerEthServiceB);
    }

    public static CustomerOtnTunnel buildTeOtnTunnel(String tunnelName) {
        TeNodeKey src = new TeNodeKey(10, 1, 1, 3);
        TeNodeKey dst = new TeNodeKey(10, 1, 1, 3);
        LtpId dstLtpId = LtpId.ltpId(0, "8");
        LTPoint srcLTPoint = new LTPoint(src, null);
        LTPoint dstLTPoint = new LTPoint(dst, dstLtpId);
        List<TsLink> tsLinkList = new ArrayList<>();
        TsLink tsLink = new TsLink(srcLTPoint, dstLTPoint);
        tsLinkList.add(tsLink);
        OduResource oduResource = new OduResource((short) 0, (short) 0, (short) 0, (short) 0, (short) 0, (short) 0, (short) 3, null, null);
        CustomerOtnTunnel customerOtnTunnel = new TsTunnel(tsLinkList, tunnelName);
        ((TsTunnel) customerOtnTunnel).bw(oduResource);
        return customerOtnTunnel;
    }

    public static CustomerOtnTunnel buildTeOtnTunnel2(String tunnelName) {
        TeNodeKey src = new TeNodeKey(10, 2, 1, 3);
        TeNodeKey dst = new TeNodeKey(10, 2, 1, 2);
        LtpId dstLtpId = LtpId.ltpId(0, "12");
        LTPoint srcLTPoint = new LTPoint(src, null);
        LTPoint dstLTPoint = new LTPoint(dst, dstLtpId);
        List<TsLink> tsLinkList = new ArrayList<>();
        TsLink tsLink = new TsLink(srcLTPoint, dstLTPoint);
        tsLinkList.add(tsLink);
        OduResource oduResource = new OduResource((short) 0, (short) 0, (short) 0, (short) 0, (short) 0, (short) 0, (short) 3, null, null);
        CustomerOtnTunnel customerOtnTunnel = new TsTunnel(tsLinkList, tunnelName);
        ((TsTunnel) customerOtnTunnel).bw(oduResource);
        return customerOtnTunnel;
    }

    public static CustomerOtnTunnel updateTeOtnTunnel(String tunnelName) {
        TeNodeKey src = new TeNodeKey(10, 1, 1, 3);
        TeNodeKey dst = new TeNodeKey(10, 1, 1, 3);
        LtpId dstLtpId = LtpId.ltpId(0, "8");
        LTPoint srcLTPoint = new LTPoint(src, null);
        LTPoint dstLTPoint = new LTPoint(dst, dstLtpId);
        List<TsLink> tsLinkList = new ArrayList<>();
        TsLink tsLink = new TsLink(srcLTPoint, dstLTPoint);
        tsLinkList.add(tsLink);
        OduResource oduResource = new OduResource((short) 0, (short) 0, (short) 0, (short) 0, (short) 0, (short) 0, (short) 4, null, null);
        CustomerOtnTunnel customerOtnTunnel = new TsTunnel(tsLinkList, tunnelName);
        ((TsTunnel) customerOtnTunnel).bw(oduResource);
        return customerOtnTunnel;
    }

    public static CustomerOtnTunnel updateTeOtnTunnel2(String tunnelName) {
        TeNodeKey src = new TeNodeKey(10, 2, 1, 3);
        TeNodeKey dst = new TeNodeKey(10, 2, 1, 2);
        LtpId dstLtpId = LtpId.ltpId(0, "12");
        LTPoint srcLTPoint = new LTPoint(src, null);
        LTPoint dstLTPoint = new LTPoint(dst, dstLtpId);
        List<TsLink> tsLinkList = new ArrayList<>();
        TsLink tsLink = new TsLink(srcLTPoint, dstLTPoint);
        tsLinkList.add(tsLink);
        OduResource oduResource = new OduResource((short) 0, (short) 0, (short) 0, (short) 0, (short) 0, (short) 0, (short) 4, null, null);
        CustomerOtnTunnel customerOtnTunnel = new TsTunnel(tsLinkList, tunnelName);
        ((TsTunnel) customerOtnTunnel).bw(oduResource);
        return customerOtnTunnel;
    }

    public static CustomerEthService buildTeEthSrv(String serviceName) {
        List<EthSrvInstance> ethSrvInstances = new ArrayList<>();
        List<TsTunnel> tsTunnelList = new ArrayList<>();
        TeNodeKey src = new TeNodeKey(-1, -1, -1, -1);
        TeNodeKey dst = new TeNodeKey(-1, -1, -1, -1);
        LtpId dstLtpId = LtpId.ltpId(0, "");
        LTPoint srcLTPoint = new LTPoint(src, null);
        LTPoint dstLTPoint = new LTPoint(dst, dstLtpId);
        List<TsLink> tsLinkList = new ArrayList<>();
        TsLink tsLink = new TsLink(srcLTPoint, dstLTPoint);
        tsLinkList.add(tsLink);
        TsTunnel tsTunnela = new TsTunnel(tsLinkList, "otntunnel-2");
        TsTunnel tsTunnelb = new TsTunnel(tsLinkList, "otntunnel-1");
        tsTunnelList.add(tsTunnela);
        tsTunnelList.add(tsTunnelb);
        List<EthSrvEndPoint> ethSrvEndPoints = new ArrayList<>();
        List<EthSrvAccessPoint> ethSrvAccessPointsA = new ArrayList<>();
        ethSrvAccessPointsA.add(new EthSrvAccessPoint("0", "10.1.1.1", 1000001));
        EthSrvEndPoint ethSrvEndPointA = new EthSrvEndPoint("uni-01", ethSrvAccessPointsA, 47, BigInteger.valueOf(3000000), BigInteger.valueOf(3000000));
        List<EthSrvAccessPoint> ethSrvAccessPointsB = new ArrayList<>();
        ethSrvAccessPointsB.add(new EthSrvAccessPoint("0", "10.1.1.1", 2000001));
        EthSrvEndPoint ethSrvEndPointB = new EthSrvEndPoint("uni-02", ethSrvAccessPointsB, 48, BigInteger.valueOf(3000000), BigInteger.valueOf(3000000));
        List<EthSrvAccessPoint> ethSrvAccessPointsC = new ArrayList<>();
        ethSrvAccessPointsC.add(new EthSrvAccessPoint("0", "10.1.1.3", 1000001));
        EthSrvEndPoint ethSrvEndPointC = new EthSrvEndPoint("uni-03", ethSrvAccessPointsC, 49, BigInteger.valueOf(3000000), BigInteger.valueOf(3000000));
        ethSrvEndPoints.add(ethSrvEndPointA);
        ethSrvEndPoints.add(ethSrvEndPointB);
        ethSrvEndPoints.add(ethSrvEndPointC);
        EthSrvInstance ethSrvInstance = new EthSrvInstance(serviceName, "desc-Eth-service", 10, 0, "2", tsTunnelList, ethSrvEndPoints);
        ethSrvInstances.add(ethSrvInstance);
        CustomerEthService customerEthService = new TsEthSrv(ethSrvInstances);
        return customerEthService;
    }

    public static CustomerEthService buildTeEthSrv2(String serviceName) {
        List<EthSrvInstance> ethSrvInstances = new ArrayList<>();
        List<TsTunnel> tsTunnelList = new ArrayList<>();
        TeNodeKey src = new TeNodeKey(-1, -1, -1, -1);
        TeNodeKey dst = new TeNodeKey(-1, -1, -1, -1);
        LtpId dstLtpId = LtpId.ltpId(0, "");
        LTPoint srcLTPoint = new LTPoint(src, null);
        LTPoint dstLTPoint = new LTPoint(dst, dstLtpId);
        List<TsLink> tsLinkList = new ArrayList<>();
        TsLink tsLink = new TsLink(srcLTPoint, dstLTPoint);
        tsLinkList.add(tsLink);
        TsTunnel tsTunnela = new TsTunnel(tsLinkList, "otntunnel-2");
        TsTunnel tsTunnelb = new TsTunnel(tsLinkList, "otntunnel-1");
        tsTunnelList.add(tsTunnela);
        tsTunnelList.add(tsTunnelb);
        List<EthSrvEndPoint> ethSrvEndPoints = new ArrayList<>();
        List<EthSrvAccessPoint> ethSrvAccessPointsA = new ArrayList<>();
        ethSrvAccessPointsA.add(new EthSrvAccessPoint("0", "10.2.1.2", 512));
        EthSrvEndPoint ethSrvEndPointA = new EthSrvEndPoint("uni-04", ethSrvAccessPointsA, 47, BigInteger.valueOf(3000000), BigInteger.valueOf(3000000));
        ethSrvEndPoints.add(ethSrvEndPointA);
        EthSrvInstance ethSrvInstance = new EthSrvInstance(serviceName, "desc-Eth-service", 20, 0, "2", tsTunnelList, ethSrvEndPoints);
        ethSrvInstances.add(ethSrvInstance);
        CustomerEthService customerEthService = new TsEthSrv(ethSrvInstances);
        return customerEthService;
    }

    public static CustomerEthService updateTeEthSrv(String serviceName) {
        List<EthSrvInstance> ethSrvInstances = new ArrayList<>();
        List<TsTunnel> tsTunnelList = new ArrayList<>();
        TeNodeKey src = new TeNodeKey(-1, -1, -1, -1);
        TeNodeKey dst = new TeNodeKey(-1, -1, -1, -1);
        LtpId dstLtpId = LtpId.ltpId(0, "");
        LTPoint srcLTPoint = new LTPoint(src, null);
        LTPoint dstLTPoint = new LTPoint(dst, dstLtpId);
        List<TsLink> tsLinkList = new ArrayList<>();
        TsLink tsLink = new TsLink(srcLTPoint, dstLTPoint);
        tsLinkList.add(tsLink);
        TsTunnel tsTunnela = new TsTunnel(tsLinkList, "otntunnel-2");
        TsTunnel tsTunnelb = new TsTunnel(tsLinkList, "otntunnel-1");
        tsTunnelList.add(tsTunnela);
        tsTunnelList.add(tsTunnelb);
        List<EthSrvEndPoint> ethSrvEndPoints = new ArrayList<>();
        List<EthSrvAccessPoint> ethSrvAccessPointsA = new ArrayList<>();
        ethSrvAccessPointsA.add(new EthSrvAccessPoint("0", "10.1.1.1", 1000001));
        EthSrvEndPoint ethSrvEndPointA = new EthSrvEndPoint("uni-01", ethSrvAccessPointsA, 47, BigInteger.valueOf(3000000), BigInteger.valueOf(3000000));
        List<EthSrvAccessPoint> ethSrvAccessPointsB = new ArrayList<>();
        ethSrvAccessPointsB.add(new EthSrvAccessPoint("0", "10.1.1.1", 2000001));
        EthSrvEndPoint ethSrvEndPointB = new EthSrvEndPoint("uni-02", ethSrvAccessPointsB, 48, BigInteger.valueOf(3000000), BigInteger.valueOf(3000000));
        List<EthSrvAccessPoint> ethSrvAccessPointsC = new ArrayList<>();
        ethSrvAccessPointsC.add(new EthSrvAccessPoint("0", "10.1.1.3", 1000001));
        EthSrvEndPoint ethSrvEndPointC = new EthSrvEndPoint("uni-03", ethSrvAccessPointsC, 49, BigInteger.valueOf(3000000), BigInteger.valueOf(3000000));
        List<EthSrvAccessPoint> ethSrvAccessPointsD = new ArrayList<>();
        ethSrvAccessPointsD.add(new EthSrvAccessPoint("0", "10.1.1.4", 1000001));
        EthSrvEndPoint ethSrvEndPointD = new EthSrvEndPoint("uni-04", ethSrvAccessPointsC, 50, BigInteger.valueOf(3000000), BigInteger.valueOf(3000000));
        ethSrvEndPoints.add(ethSrvEndPointA);
        ethSrvEndPoints.add(ethSrvEndPointB);
        ethSrvEndPoints.add(ethSrvEndPointC);
        ethSrvEndPoints.add(ethSrvEndPointD);
        EthSrvInstance ethSrvInstance = new EthSrvInstance(serviceName, "desc-Eth-service", 10, 0, "2", tsTunnelList, ethSrvEndPoints);
        ethSrvInstances.add(ethSrvInstance);
        CustomerEthService customerEthService = new TsEthSrv(ethSrvInstances);
        return customerEthService;
    }

    public static CustomerEthService updateTeEthSrv2(String serviceName) {
        List<EthSrvInstance> ethSrvInstances = new ArrayList<>();
        List<TsTunnel> tsTunnelList = new ArrayList<>();
        TeNodeKey src = new TeNodeKey(-1, -1, -1, -1);
        TeNodeKey dst = new TeNodeKey(-1, -1, -1, -1);
        LtpId dstLtpId = LtpId.ltpId(0, "");
        LTPoint srcLTPoint = new LTPoint(src, null);
        LTPoint dstLTPoint = new LTPoint(dst, dstLtpId);
        List<TsLink> tsLinkList = new ArrayList<>();
        TsLink tsLink = new TsLink(srcLTPoint, dstLTPoint);
        tsLinkList.add(tsLink);
        TsTunnel tsTunnela = new TsTunnel(tsLinkList, "otntunnel-2");
        TsTunnel tsTunnelb = new TsTunnel(tsLinkList, "otntunnel-1");
        tsTunnelList.add(tsTunnela);
        tsTunnelList.add(tsTunnelb);
        List<EthSrvEndPoint> ethSrvEndPoints = new ArrayList<>();
        List<EthSrvAccessPoint> ethSrvAccessPointsA = new ArrayList<>();
        List<EthSrvAccessPoint> ethSrvAccessPointsC = new ArrayList<>();
        ethSrvAccessPointsA.add(new EthSrvAccessPoint("0", "10.2.1.2", 512));
        EthSrvEndPoint ethSrvEndPointA = new EthSrvEndPoint("uni-04", ethSrvAccessPointsA, 47, BigInteger.valueOf(3000000), BigInteger.valueOf(3000000));
        ethSrvAccessPointsC.add(new EthSrvAccessPoint("0", "10.2.1.3", 1000001));
        EthSrvEndPoint ethSrvEndPointC = new EthSrvEndPoint("uni-03", ethSrvAccessPointsC, 49, BigInteger.valueOf(3000000), BigInteger.valueOf(3000000));
        ethSrvEndPoints.add(ethSrvEndPointA);
        ethSrvEndPoints.add(ethSrvEndPointC);
        EthSrvInstance ethSrvInstance = new EthSrvInstance(serviceName, "desc-Eth-service", 10, 0, "2", tsTunnelList, ethSrvEndPoints);
        ethSrvInstances.add(ethSrvInstance);
        CustomerEthService customerEthService = new TsEthSrv(ethSrvInstances);
        return customerEthService;
    }
}
