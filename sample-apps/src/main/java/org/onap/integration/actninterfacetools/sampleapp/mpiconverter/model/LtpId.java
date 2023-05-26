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

import com.google.common.primitives.UnsignedLongs;

import java.util.Objects;

public final class LtpId {


    private final long number;
    private final String name;
    private final boolean hasName;

    private LtpId(long number){
        this.number = number;
        this.name = String.valueOf(number);
        this.hasName = false;
    }

    private LtpId(long number, String name) {
        if(name.equals("MJI=")){
            this.name = "TUpJPQ==";
        }else{
            this.name = name;
        }
        this.number = number;

        this.hasName = true;
    }

    public static LtpId ltpId(long number) {
        return new LtpId(number);
    }

    public static LtpId ltpId(String string) {
        return new LtpId(UnsignedLongs.decode(string));
    }
    public static LtpId ltpId1(long number, String string) {
        return new LtpId(number, string);
    }

    public static LtpId ltpId(long number, String name) {
        return new LtpId(number, name);
    }
    public boolean hasName() { return hasName; }

    @Override
    public int hashCode() {
        return Objects.hash(this.number);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this){
            return true;
        }
        if (o instanceof LtpId) {
            LtpId other = (LtpId) o;
            return Objects.equals(other.number, this.number);
        }
        return false;
    }

    @Override
    public String toString() {
        if (hasName()) {
            //return String.format("[%s](%d)", name, number);
            return name;
        } else {
            return name;
        }
    }
}
