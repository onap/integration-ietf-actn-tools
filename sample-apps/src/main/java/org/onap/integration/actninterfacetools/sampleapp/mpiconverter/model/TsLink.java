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

import org.onap.integration.actninterfacetools.actnclient.api.CustomerOtnTunnel;

import java.util.List;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

public class TsLink extends CustomerOtnTunnel implements Link {

    private final String id;
    private final LTPoint src;
    private final LTPoint dst;
    private final State state;
    private final Type type;
    private String transportSliceId;
    private List<Integer> numberOfOdu;

    protected TsLink(LTPoint src, LTPoint dst, State state){
        this("Default", src, dst, state);
    }

    protected TsLink(String id, LTPoint src, LTPoint dst, State state){
        this(id, src, dst, state, Type.REGULAR);
    }

    public TsLink(String id, LTPoint src, LTPoint dst, State state, Type type){
        this.id = id;
        this.src = src;
        this.dst = dst;
        this.state = state;
        this.type = type;
    }

    public TsLink(LTPoint srcLtPoint, LTPoint dstLtPoint) {
        this.id = "defaultId";
        this.src = srcLtPoint;
        this.dst = dstLtPoint;
        this.state = State.ACTIVE;
        this.type = Type.REGULAR;
    }

    public String sliceId() {
        return transportSliceId;
    }

    public void sliceId(String id) {
        this.transportSliceId = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof TsLink){
            final TsLink other = (TsLink) o;
            return  Objects.equals(this.id, other.id) &&
                    Objects.equals(this.type, other.type) &&
                    Objects.equals(this.state, other.state);
        }
        return false;
    }

    public String id() {
        return this.id;
    }

    @Override
    public String toString(){
        return (src + "==>" + dst);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, src, dst, state);
    }

    @Override
    public LTPoint src() {
        return this.src;
    }

    @Override
    public LTPoint dst() {
        return this.dst;
    }

    @Override
    public State state() {
        return state;
    }

    @Override
    public Type type() {
        return type;
    }

    //===Builder========
    public static Builder builder() { return new Builder(); }

    public List<Integer> getNumberOfOdu() {
        return numberOfOdu;
    }

    public void setNumberOfOdu(List<Integer> numberOfOdu) {
        this.numberOfOdu = numberOfOdu;
    }

    public static class Builder {

        private String id;
        private LTPoint src;
        private LTPoint dst;
        private State state = State.ACTIVE;
        private Type type = Type.REGULAR;


        protected Builder() {

        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }
        public Builder src(LTPoint src) {
            this.src = src;
            return this;
        }
        public Builder dst(LTPoint dst) {
            this.dst = dst;
            return this;
        }
        public Builder state(State state) {
            this.state = state;
            return this;
        }
        public Builder type(Type type) {
            this.type = type;
            return this;
        }
        public TsLink build(){
            if (type == Type.INGRESS){
                checkNotNull(dst, "Destination connect point cannot be null");
            } else if (type == Type.EGRESS) {
                checkNotNull(src, "Source connect point cannot be null");
            } else if (type == Type.REGULAR){
                checkNotNull(dst, "Destination connect point cannot be null");
                checkNotNull(src, "Source connect point cannot be null");
            }
            checkNotNull(state, "State cannot be null");
            return new TsLink(id, src, dst, state, type);
        }
    }



}
