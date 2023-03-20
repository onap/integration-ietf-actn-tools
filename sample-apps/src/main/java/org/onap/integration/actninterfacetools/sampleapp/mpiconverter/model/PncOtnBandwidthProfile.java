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


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class PncOtnBandwidthProfile implements Cloneable, BandwidthMgmtService {


    public static final boolean CAN_ODU_MULTIPLEXING = true;

    public static OduResource EMPTY_ODU_RESOURCE = new OduResource((short)0,(short)0,(short)0,(short)0,(short)0,
            (short)0,(short)0, null, null);

    private Map<TsLinkKey, int[]> unResvOdu = new HashMap<>();

    private Map<TsLinkKey, int[]> localReservedOdu = new HashMap<>();

    //private Map<TsLinkKey, Multiset<BwUsageRecord>> localReservedOdu = new HashMap<>();

    //Default PncOtnBandwidthProfile constructor
    public PncOtnBandwidthProfile(){

    }

    // Allocate new ODu bandwidth to link tunnel, the allocation will add new entry into localReservedOdu map
    // and deduct one odu of specific type from unReserOdu map
    // NOTE: always call this function before checking there are feasible odu(s) left.
    public boolean allocLocalReserveOdu(TsLinkKey linkkey, OduResource request) {

        //Multiset<BwUsageRecord> allocatedBw = localReservedOdu.get(linkkey);
        int [] unreservedOdulist = unResvOdu.get(linkkey);
        int [] reservedOdu = localReservedOdu.get(linkkey);
        if (reservedOdu == null){
            reservedOdu = new int[OduResource.ADVANCE_ODU.length];
        }
        int oduRequestArray[] = request.getOduResourceArray(OduResource.ADVANCE_ODU);
        checkArgument(unreservedOdulist.length == oduRequestArray.length, "Target Odu is not available");
        //Subtract certain share(s) of this ODU Type
        if (unreservedOdulist[0] >= reservedOdu[0] + request.equivalentToNumOfOdu0s()){
            reservedOdu[0] +=  request.equivalentToNumOfOdu0s();
        } else {
            for(int i=0,e=unreservedOdulist.length;i < e; i++) {
                int leftover = unreservedOdulist[i] - (reservedOdu[i] + oduRequestArray[i]);
                checkArgument(leftover >= 0, "Odu type is not sufficient for building slice tunnel");
                reservedOdu[i] +=  oduRequestArray[i];
            }
        }
        localReservedOdu.put(linkkey, reservedOdu);
        return true;
    }


    public boolean addUnreservedOdu(TsLinkKey linkkey, OduResource oduResource) {
        int oduInventory[] = oduResource.getOduResourceArray(OduResource.ADVANCE_ODU);
        unResvOdu.put(linkkey, oduInventory);
        return true;
    }

    public boolean addUnreservedOdu(TsLinkKey linkkey, int[] odulist) {
        unResvOdu.put(linkkey, odulist);
        return true;
    }


    public boolean removeUnreservedOdu(TsLinkKey linkkey) {
        unResvOdu.remove(linkkey);
        return true;
    }


    public int[] getUnreservedOdu(TsLinkKey linkkey) {
        checkNotNull(linkkey);
        int [] origin = unResvOdu.get(linkkey);
        return origin;
    }

    public int[] getReservedOdu(TsLinkKey linkkey) {
        checkNotNull(linkkey);
        int [] origin = localReservedOdu.get(linkkey);
        return origin;
    }

    public OduResource getUnreserveOduResource(TsLinkKey linkkey){
        int [] oduArray = getUnreservedOdu(linkkey);
        return (oduArray == null)? null: OduResource.getOduResourceFromArray(oduArray);
    }

    public OduResource getReservedOduResource(TsLinkKey linkkey) {
        int [] oduArray = getReservedOdu(linkkey);
        return (oduArray == null)? null: OduResource.getOduResourceFromArray(oduArray);
    }

    public boolean isOduTypeAvailable(TsLink link, OduResource request) {
        boolean available = true;
        OduResource unreserveOduResource = getUnreserveOduResource(TsLinkKey.tsLinkKey(link));
        OduResource reservedOduResource = getReservedOduResource(TsLinkKey.tsLinkKey(link));
        //int oduRequestArray[] = request.getOduResourceArray(ODU_TYPE_IN_IETF_TE_TYPE_SIMPLE);
        if (null != unreserveOduResource) {
            if (null != reservedOduResource &&
                    unreserveOduResource.equivalentToNumOfOdu0s() <  request.equivalentToNumOfOdu0s() + reservedOduResource.equivalentToNumOfOdu0s()){
                available = false;
            } else if ( null == reservedOduResource &&
                    unreserveOduResource.equivalentToNumOfOdu0s() <  request.equivalentToNumOfOdu0s()){
                available = false;
            }
            if(available == false){
                available = true;
                for (OduType t : OduResource.ADVANCE_ODU) {
                    if (null != reservedOduResource &&
                            unreserveOduResource.getNumberOfOdu(t) <  request.getNumberOfOdu(t) + reservedOduResource.getNumberOfOdu(t)){
                        available = false;
                    } else if ( null == reservedOduResource &&
                            unreserveOduResource.getNumberOfOdu(t) <  request.getNumberOfOdu(t) ) {
                        available = false;
                    }
                }
            }
        } else {
            available = false;
        }
        return available;
    }


    private int feasibleOduByBandwidth(double[] bandwidthArr, int target) {
        int start = 0, end = bandwidthArr.length - 1;
        int ans = -1;
        while (start <= end) {
            int mid = (start + end) / 2;
            if (bandwidthArr[mid] < target) {
                start = mid + 1;
            } else {
                ans = mid;
                end = mid - 1;
            }
        }
        return ans;
    }

    private int feasibleOduByOduList(int[] numberODUArr, int idx, boolean multiplexing) {
        int oduIdx = -1;
        int len = numberODUArr.length;
        if (idx >= len) {
            return -1;
        }
        while (idx < len) {
            if (numberODUArr[idx] > 0) {
                oduIdx = idx;
                break;
            } else if (!multiplexing) {
                break;
            } else {
                idx++;
            }
        }
        return oduIdx;
    }

    public Object clone() throws CloneNotSupportedException {
        PncOtnBandwidthProfile t = (PncOtnBandwidthProfile) super.clone();
        t.unResvOdu = new HashMap<TsLinkKey, int[]>();
        t.localReservedOdu = new HashMap<TsLinkKey, int[]> ();
        for (Map.Entry<TsLinkKey, int[]> entry : unResvOdu.entrySet()) {
            t.unResvOdu.put(entry.getKey(),
                    Arrays.copyOf(entry.getValue(),
                            entry.getValue().length));
        }

        for (Map.Entry<TsLinkKey, int[]> entry : localReservedOdu.entrySet()) {
            t.localReservedOdu.put(entry.getKey(),
                    Arrays.copyOf(entry.getValue(),
                            entry.getValue().length));
        }
        //t.localReservedOdu = new HashMap<TsLinkKey, Multiset<BwUsageRecord>>();
        return t;
    }

}
