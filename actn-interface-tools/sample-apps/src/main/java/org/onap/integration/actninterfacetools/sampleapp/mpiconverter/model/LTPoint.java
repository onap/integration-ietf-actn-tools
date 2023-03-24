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


import java.util.Objects;

public class LTPoint implements Comparable<LTPoint> {


    private final TeNodeKey tsDeviceId;
    private final LtpId ltPointId;
    public static final LTPoint DUMMY = new LTPoint(new TeNodeKey(-1L, -1L, -1L, -1l),
            LtpId.ltpId(-1L));

    public LTPoint(TeNodeKey tsDeviceId, LtpId ltPointId) {
        this.tsDeviceId = tsDeviceId;
        this.ltPointId = ltPointId;
    }

    public TeNodeKey tsDeviceId() {
        return tsDeviceId;
    }

    public LtpId ltPointId() {
        return ltPointId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tsDeviceId, ltPointId);
    }

    @Override
    public String toString() {
        return "[" +
                tsDeviceId.providerId() + "." +
                tsDeviceId.clientId() + "." +
                tsDeviceId.topologyId() + "." +
                tsDeviceId.teNodeId() +
                "](Tpid-"  +
                ltPointId + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof LTPoint){
            final LTPoint other = (LTPoint) obj;
            return Objects.equals(this.tsDeviceId, other.tsDeviceId) &&
                    Objects.equals(this.ltPointId, other.ltPointId);
        }
        return false;
    }

    @Override
    public int compareTo(LTPoint o) {
        int result = tsDeviceId().toString().compareTo(o.tsDeviceId().toString());
        if (result == 0) {
            int delta = ltPointId().toString().compareTo(o.ltPointId.toString());
            result = delta == 0 ? 0 : (delta < 0 ? -1 : 1);
        }
        return result;
    }
}
