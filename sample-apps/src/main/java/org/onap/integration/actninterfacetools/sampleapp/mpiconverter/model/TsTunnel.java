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

import com.google.common.collect.ImmutableList;


import java.util.List;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class TsTunnel extends TsLink {

    private final List<TsLink> links;
//    private final Weight cost;
    private final String name;
    private OduResource bw = null;


    public TsTunnel(List<TsLink> links, String name) {
        super(source(links), destination(links), State.ACTIVE);
        this.links = ImmutableList.copyOf(links);
//        this.cost = cost;
        this.name = name;

    }
//    public TsTunnel(LTPoint srcLtPoint, LTPoint dstLtPoint){
//        super(srcLtPoint, dstLtPoint);
//        this.links = null;
//        this.cost = null;
//    }

    public List<TsLink> links(){
        return links;
    }

//    public double cost() {
//        if (cost instanceof ScalarWeight) {
//            return ((ScalarWeight) cost).value();
//        }
//        return 0;
//    }

//    public Weight weight() {
//        return cost;
//    }
    public String name() {
        return this.name;
    }
    private static LTPoint source(List<TsLink> links) {
        checkNotNull(links, "List of path links cannot be null");
        checkArgument(!links.isEmpty(), "List of path links cannot be null");
        return links.get(0).dst();
    }

    private static LTPoint destination(List<TsLink> links) {
        checkNotNull(links, "List of path links cannot be null");
        checkArgument(!links.isEmpty(), "List of path links cannot be null");
        return links.get(links.size() - 1).dst();
    }

    public String toString(){
        StringBuilder sb = new StringBuilder("TsTunnel: ");
        for (TsLink li : this.links()) {
            sb.append("\n");
            sb.append(li.toString());
        };
        sb.append("\n");
//        sb.append("with total cost: " + this.cost.toString());
        return sb.toString();
    }
    public int hashCode() {
        return links.hashCode();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof TsTunnel) {
            final TsTunnel other = (TsTunnel) obj;
            return Objects.equals(this.links, other.links);
        }
         return false;
    }

    public OduResource bw() {
        return bw;
    }

    public void bw(OduResource bw) {
        this.bw = bw;
    }
}
