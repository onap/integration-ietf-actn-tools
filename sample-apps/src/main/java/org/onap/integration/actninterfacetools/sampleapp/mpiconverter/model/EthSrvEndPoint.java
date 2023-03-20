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

import java.math.BigInteger;
import java.util.List;

public class EthSrvEndPoint {
    private final String ethtSvcEndPointName;
    private final List<EthSrvAccessPoint> ethSrvAccessPoints;
    private final int vlanValue;
    private final BigInteger CIR;
    private final BigInteger EIR;

    public EthSrvEndPoint(String ethtSvcEndPointName, List<EthSrvAccessPoint> ethSrvAccessPoints, int vlanValue, BigInteger CIR, BigInteger EIR) {
        this.ethtSvcEndPointName = ethtSvcEndPointName;
        this.ethSrvAccessPoints = ethSrvAccessPoints;
        this.vlanValue = vlanValue;
        this.CIR = CIR;
        this.EIR = EIR;
    }
    public String getEthtSvcEndPointName(){
        return this.ethtSvcEndPointName;
    }
    public List<EthSrvAccessPoint> getEthSrvAccessPoints(){
        return this.ethSrvAccessPoints;
    }

    public int getVlanValue(){
        return this.vlanValue;
    }
    public BigInteger getCIR(){
        return this.CIR;
    }
    public BigInteger getEIR(){
        return this.EIR;
    }
}
