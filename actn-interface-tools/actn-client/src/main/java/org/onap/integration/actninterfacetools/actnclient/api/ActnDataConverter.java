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
package org.onap.integration.actninterfacetools.actnclient.api;


import org.onosproject.yang.gen.v11.ietfethtranservice.rev20210111.ietfethtranservice.DefaultEthtSvc;
import org.onosproject.yang.gen.v11.ietfnetwork.rev20180226.ietfnetwork.networks.Network;
import org.onosproject.yang.gen.v11.ietfte.rev20210220.ietfte.tunnelsgrouping.tunnels.DefaultTunnel;
public abstract class ActnDataConverter {
    CustomerOtnTopology convertActnOtnTopology(Network actnOtnTopology) throws Exception {
        throw new Exception("unsupported method");
    }
    CustomerOtnTunnel convertActnOtnTunnel(DefaultTunnel actnOtnTunnel) throws Exception {
        throw new Exception("unsupported method");
    }
    DefaultTunnel convertActnOtnTunnel(CustomerOtnTunnel customerOtnTunnel) throws Exception {
        throw new Exception("unsupported method");
    }
    CustomerEthService convertActnEthService(DefaultEthtSvc actnEthService) throws Exception {
        throw new Exception("unsupported method");
    }
    DefaultEthtSvc convertActnEthService(CustomerEthService customerEthService) throws Exception {
        throw new Exception("unsupported method");
    }
}
