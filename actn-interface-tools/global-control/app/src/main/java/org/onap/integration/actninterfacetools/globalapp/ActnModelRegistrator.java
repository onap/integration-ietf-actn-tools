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

package org.onap.integration.actninterfacetools.globalapp;


import com.google.common.collect.ImmutableMap;
import org.apache.felix.scr.annotations.Component;
import org.onosproject.yang.gen.v1.ietfethtetopology.rev20191118.IetfEthTeTopology;
import org.onosproject.yang.gen.v1.ietfethtetunnel.rev20180301.IetfEthTeTunnel;
import org.onosproject.yang.gen.v1.ietfinettypes.rev20210222.IetfInetTypes;
import org.onosproject.yang.gen.v1.ietfroutingtypes.rev20171204.IetfRoutingTypes;
import org.onosproject.yang.gen.v1.ietfyangtypes.rev20210414.IetfYangTypes;
import org.onosproject.yang.gen.v11.ietfethtranservice.rev20210111.IetfEthTranService;
import org.onosproject.yang.gen.v11.ietfethtrantypes.rev20191103.IetfEthTranTypes;
import org.onosproject.yang.gen.v11.ietflayer1types.rev20210219.IetfLayer1Types;
import org.onosproject.yang.gen.v11.ietfnetwork.rev20180226.IetfNetwork;
import org.onosproject.yang.gen.v11.ietfnetworktopology.rev20180226.IetfNetworkTopology;
import org.onosproject.yang.gen.v11.ietfopticalslice.rev20200821.IetfOpticalSlice;
import org.onosproject.yang.gen.v11.ietfotntopology.rev20210222.IetfOtnTopology;
import org.onosproject.yang.gen.v11.ietfotntunnel.rev20210625.IetfOtnTunnel;
import org.onosproject.yang.gen.v11.ietfte.rev20210220.IetfTe;
import org.onosproject.yang.gen.v11.ietftetopology.rev20200806.IetfTeTopology;
import org.onosproject.yang.gen.v11.ietftetypes.rev20200610.IetfTeTypes;
import org.onosproject.yang.model.YangModel;
import org.onosproject.yang.model.YangModuleId;
import org.onosproject.yang.runtime.AppModuleInfo;
import org.onosproject.yang.runtime.DefaultAppModuleInfo;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Representation of TE Topology YANG model registrator.
 */
public class ActnModelRegistrator extends AbstractYangModelRegistrator {
    /**
     * Creates ACTN YANG model registrator.
     */
    public ActnModelRegistrator() throws URISyntaxException {
        super(IetfTeTopology.class, getAppInfo());
    }

    private static Map<YangModuleId, AppModuleInfo> getAppInfo() throws URISyntaxException {
        Map<YangModuleId, AppModuleInfo> appInfo = new HashMap<>();
        YangModel model = YangManagerUtils.getYangModel(IetfTeTopology.class);
        Iterator<YangModuleId> it = model.getYangModulesId().iterator();
        YangModuleId id;
        while (it.hasNext()) {
            id = it.next();
            switch (id.moduleName()) {
                case "ietf-inet-types":
                    appInfo.put(id, new DefaultAppModuleInfo(IetfInetTypes.class, null));
                    break;

                case "ietf-network":
                    appInfo.put(id, new DefaultAppModuleInfo(IetfNetwork.class, null));
                    break;
                case "ietf-network-topology":
                    appInfo.put(id, new DefaultAppModuleInfo(IetfNetworkTopology.class, null));
                    break;

                case "ietf-yang-types":
                    appInfo.put(id, new DefaultAppModuleInfo(IetfYangTypes.class, null));
                    break;
                case "ietf-routing-types":
                    appInfo.put(id, new DefaultAppModuleInfo(IetfRoutingTypes.class, null));
                    break;
                case "ietf-te-types":
                    appInfo.put(id, new DefaultAppModuleInfo(IetfTeTypes.class, null));
                    break;



                case "ietf-te-topology":
                    appInfo.put(id, new DefaultAppModuleInfo(IetfTeTopology.class, null));
                    break;
                case "ietf-otn-topology":
                    appInfo.put(id, new DefaultAppModuleInfo(IetfOtnTopology.class, null));
                    break;

                case "ietf-eth-tran-types":
                    appInfo.put(id, new DefaultAppModuleInfo(IetfEthTranTypes.class, null));
                    break;
                case "ietf-eth-te-topology":
                    appInfo.put(id, new DefaultAppModuleInfo(IetfEthTeTopology.class, null));
                    break;


                case "ietf-te":
                    appInfo.put(id, new DefaultAppModuleInfo(IetfTe.class, null));
                    break;

                case "ietf-otn-tunnel":
                    appInfo.put(id, new DefaultAppModuleInfo(IetfOtnTunnel.class, null));
                    break;
                case "ietf-eth-te-tunnel":
                    appInfo.put(id, new DefaultAppModuleInfo(IetfEthTeTunnel.class, null));
                    break;


                case "ietf-optical-slice":
                    appInfo.put(id, new DefaultAppModuleInfo(IetfOpticalSlice.class,null));
                    break;
                case "ietf-layer1-types":
                    appInfo.put(id, new DefaultAppModuleInfo(IetfLayer1Types.class,null));
                    break;
                case "ietf-eth-tran-service":
                    appInfo.put(id, new DefaultAppModuleInfo(IetfEthTranService.class,null));
                    break;
                default:
                    break;
            }
        }

        return ImmutableMap.copyOf(appInfo);
    }
}
