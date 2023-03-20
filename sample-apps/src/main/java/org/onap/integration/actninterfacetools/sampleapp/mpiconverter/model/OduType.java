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

public enum OduType {
    ODU0 (0,  1_244_160),
    ODU1 (1,  2_498_770),
    ODU2 (2, 10_037_270),
    ODU3 (3, 40_319_210),
    ODU4 (4,  104_794_440),
    ODU2E (5, 10_399_520),
    ODU3E2 (6, 41_785_960),
    ODUFLEX (7, 3);
//    ODUFLEX_GFP (8, -1);

    protected int type;

    //Bandwidth in Mbit/s
    protected int bandwidth;

    OduType(int type, int bandwidth){
        this.type = type;
        this.bandwidth = bandwidth;
    }

    public int typeValue() {
        return this.type;
    }

    public int getBandwidth() {
        return this.bandwidth;
    }

    static public OduType of(int i) {
        if (i >=0 && i <= 4) {
            switch (i){
                case 0: return OduType.ODU0;
                case 1: return OduType.ODU1;
                case 2: return OduType.ODU2;
                case 3: return OduType.ODU3;
                case 4: return OduType.ODU4;
                case 5: return OduType.ODU2E;
                case 6: return OduType.ODU3E2;
                case 7: return OduType.ODUFLEX;
//                case 8: return OduType.ODUFLEX_GFP;
            }
        }
        return null;

    }
}
