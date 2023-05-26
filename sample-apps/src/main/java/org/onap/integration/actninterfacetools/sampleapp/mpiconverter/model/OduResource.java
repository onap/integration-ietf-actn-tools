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

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.util.Arrays;

/**
 * Representation of an ODU link resource.
 */
public class OduResource{
    private final short odu0s;
    private final short odu1s;
    private final short odu2s;
    private final short odu2es;
    private final short odu3s;
    private final short odu4s;
    private final short oduFlex;
    private final byte[] tribPortAvailabilityBitmap;
    private final byte[] tribSlotAvailabilityBitmap;


    public final static OduType[] SIMPLE_ODU = {
            OduType.ODU0, OduType.ODU1, OduType.ODU2, OduType.ODU3, OduType.ODU4
    };

    public final static OduType[] ADVANCE_ODU = {
            OduType.ODU0, OduType.ODU1, OduType.ODU2, OduType.ODU3, OduType.ODU4,
            OduType.ODU2E, OduType.ODU3E2, OduType.ODUFLEX
    };

    /**
     * Creates an instance of an ODU link resource.
     *
     * @param odu0s     number of available ODU0 containers
     * @param odu1s     number of available ODU1 containers
     * @param odu2s     number of available ODU2 containers
     * @param odu2es    number of available ODU2e containers
     * @param odu3s     number of available ODU3 containers
     * @param odu4s     number of available ODU4 containers
     * @param oduFlex available ODUflex bandwidth in terms of ODU0 containers
     */
    public OduResource(short odu0s, short odu1s, short odu2s,
                       short odu2es, short odu3s, short odu4s,
                       short oduFlex,
                       byte[] tpMap, byte[] tsMap) {
        this.odu0s = odu0s;
        this.odu1s = odu1s;
        this.odu2s = odu2s;
        this.odu2es = odu2es;
        this.odu3s = odu3s;
        this.odu4s = odu4s;
        this.oduFlex = oduFlex;
//        this.oduFlexCbrs = oduFlexCbrs;
        this.tribPortAvailabilityBitmap = tpMap == null ? null :
                Arrays.copyOf(tpMap, tpMap.length);
        this.tribSlotAvailabilityBitmap = tsMap == null ? null :
                Arrays.copyOf(tsMap, tsMap.length);
    }

    public int[] getOduResourceArray(OduType[] odus){
        int size = odus.length;
        int [] ans = new int [size];
        for(int i = 0, e = size; i < e; i++ ) {
            ans[i] = getNumberOfOdu(odus[i]);
        }
        return ans;
    }

    public static OduResource getOduResourceFromArray(int [] oduArray) {
        short odu0s = 0, odu1s = 0, odu2s = 0, odu2es = 0, odu3s = 0, odu4s = 0, oduFlex = 0;

        for (int i=0, e= oduArray.length; i<e; i++) {
            short val = (short) oduArray[i];
            switch(ADVANCE_ODU[i]){
                case ODU0: odu0s = val; break;
                case ODU1: odu1s = val; break;
                case ODU2: odu2s = val; break;
                case ODU3: odu3s = val; break;
                case ODU4: odu4s = val; break;
                case ODU2E: odu2es = val; break;
                case ODU3E2: break;
                case ODUFLEX: oduFlex = val; break;
//                case ODUFLEX_GFP: oduFlexGfps = val; break;
                default: break;
            }
        }
        return new OduResource(odu0s, odu1s, odu2s, odu2es, odu3s, odu4s, oduFlex,
                null, null);

    }
    public int getNumberOfOdu(OduType oduType){
        switch(oduType) {
            case ODU0: return this.odu0s();
            case ODU1: return this.odu1s();
            case ODU2: return this.odu2s();
            case ODU3: return this.odu3s();
            case ODU4: return this.odu4s();
            case ODU2E: return this.odu2es();
            case ODU3E2: return 0;
            case ODUFLEX: return this.oduFlex();
//            case ODUFLEX_GFP: return this.oduFlexGfps();
            default: throw new IllegalArgumentException("oduType has not been setup inside getNumberOfOdu method");
        }
    }

    public static OduResource fromString(String val) {
        short odu0s = 0, odu1s = 0, odu2s = 0, odu2es = 0, odu3s = 0, odu4s = 0, oduFlex = 0;
        String [] sarr = val.split("\\,");
        if (sarr.length % 2 == 0){
            for(int i=0, e=sarr.length; i < e ; i=i+2){
                try{
                    int odutype = Integer.parseInt(sarr[i].trim());
                    int num = Integer.parseInt(sarr[i+1].trim());
                    if (num <= 0) {
                        continue;
                    }
                    switch (odutype){
                        case 0: odu0s = (short) num; break;
                        case 1: odu1s = (short) num; break;
                        case 2: odu2s = (short) num; break;
                        case 3: odu3s = (short) num; break;
                        case 4: odu4s = (short) num; break;
                        case 5: odu2es = (short) num; break;
                        case 6: break;
                        case 7: oduFlex = (short) num; break;
//                        case 8: oduFlexGfps = (short) num; break;
                        default: continue;
                    }

                } catch (NumberFormatException exception){
                    return null;
                }
            }

            return new OduResource(odu0s, odu1s, odu2s, odu2es, odu3s, odu4s, oduFlex,
                    null, null);
        }
        return null;
    }
    public static OduResource clone(OduResource o) {
        return new OduResource(o.odu0s(), o.odu1s(), o.odu2s(), o.odu2es(),
                o.odu3s(), o.odu4s(), o.oduFlex(),
                o.getTribPortAvailabilityBitmap(), o.getTribSlotAvailabilityBitmap());
    }


    public short equivalentToNumOfOdu0s() {
        short count = 0;
        for(OduType otype : ADVANCE_ODU){
            switch (otype){
                case ODU0: count += this.odu0s();break;
                case ODU1: count += 2 * this.odu1s();break;
                case ODU2: count += 8 * this.odu2s();break;
                case ODU3: count += 32 * this.odu3s();break;
                case ODU4: count += 80 * this.odu4s();break;
                case ODU2E: count += 8 * this.odu2es();break;
                case ODU3E2: count += 0;break;
                case ODUFLEX: break;
//                case ODUFLEX_GFP: break;
                default: continue;
            }
        }
        return count;
    }
    /**
     * Returns the number of available ODU0s.
     *
     * @return the odu0s
     */
    public short odu0s() {
        return odu0s;
    }

    /**
     * Returns the number of available ODU1s.
     *
     * @return the odu1s
     */
    public short odu1s() {
        return odu1s;
    }

    /**
     * Returns the number of available ODU2s.
     *
     * @return the odu2s
     */
    public short odu2s() {
        return odu2s;
    }

    /**
     * Returns the number of available ODU2es.
     *
     * @return the odu2es
     */
    public short odu2es() {
        return odu2es;
    }

    /**
     * Returns the number of available ODU3s.
     *
     * @return the odu3s
     */
    public short odu3s() {
        return odu3s;
    }

    /**
     * Returns the number of available ODU4s.
     *
     * @return the odu4s
     */
    public short odu4s() {
        return odu4s;
    }

    /**
     * Returns available ODUflex bandwidth in terms of ODU0 containers.
     *
     * @return the oduFlexes
     */
    public short oduFlex() {
        return oduFlex;
    }

//    public short oduFlexCbrs() {
//        return oduFlexCbrs;
//    }

    public byte[] getTribPortAvailabilityBitmap() {
        return tribPortAvailabilityBitmap == null ? null :
                Arrays.copyOf(tribPortAvailabilityBitmap,
                        tribPortAvailabilityBitmap.length);
    }

    public byte[] getTribSlotAvailabilityBitmap() {
        return tribSlotAvailabilityBitmap == null ? null :
                Arrays.copyOf(tribSlotAvailabilityBitmap,
                        tribSlotAvailabilityBitmap.length);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(odu0s, odu1s, odu2s, odu2es, odu3s,
                odu4s, oduFlex,
                Arrays.hashCode(tribPortAvailabilityBitmap),
                Arrays.hashCode(tribSlotAvailabilityBitmap));
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof OduResource) {
            OduResource that = (OduResource) object;
            return (this.odu0s == that.odu0s) &&
                    (this.odu1s == that.odu1s) &&
                    (this.odu2s == that.odu2s) &&
                    (this.odu2es == that.odu2es) &&
                    (this.odu3s == that.odu3s) &&
                    (this.odu4s == that.odu4s) &&
                    (this.oduFlex == that.oduFlex) &&
                    Arrays.equals(this.tribPortAvailabilityBitmap,
                            that.tribPortAvailabilityBitmap) &&
                    Arrays.equals(this.tribSlotAvailabilityBitmap,
                            that.tribSlotAvailabilityBitmap);
        }
        return false;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("odu0s", odu0s)
                .add("odu1s", odu1s)
                .add("odu2s", odu2s)
                .add("odu2es", odu2es)
                .add("odu3s", odu3s)
                .add("odu4s", odu4s)
                .add("oduFlexes", oduFlex)
                .add("tribPortAvailabilityBitmap", tribPortAvailabilityBitmap)
                .add("tribSlotAvailabilityBitmap", tribSlotAvailabilityBitmap)
                .toString();
    }

}
