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

import java.util.Objects;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class TsLinkKey {


    private static final String DELIM = "-";

    private final LTPoint src = null;
    private final LTPoint dst = null;
    private final String id;

    /**
     * Creates a link identifier with source and destination LTPoint point.
     *
     * @param src source LTPoint point
     * @param dst destination LTPoint point
     */
    private TsLinkKey(LTPoint src, LTPoint dst) {
        this(src, dst, src + DELIM + dst);
    }

    /**
     * Creates a link identifier with source and destination LTPoint point.
     *
     * @param src source LTPoint point
     * @param dst destination LTPoint point
     */
    public TsLinkKey(LTPoint src, LTPoint dst, String id) {
        this.id = checkNotNull(id);
        //this.src = checkNotNull(src);
        //this.dst = checkNotNull(dst);
    }

    /**
     * Returns source connection point.
     *
     * @return source connection point
     */
    public LTPoint src() {
        return src;
    }

    /**
     * Returns destination connection point.
     *
     * @return destination connection point
     */
    public LTPoint dst() {
        return dst;
    }

    /**
     * Returns a string suitable to be used as an identifier.
     *
     * @return string as identifier
     */
    public String asId() {
        return src + DELIM + dst;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof TsLinkKey) {
            final TsLinkKey other = (TsLinkKey) obj;
            return  Objects.equals(this.id, other.id) &&
                    Objects.equals(this.src, other.src) &&
                    Objects.equals(this.dst, other.dst);
        }
        return false;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(getClass())
                .add("src", src)
                .add("dst", dst)
                .toString();
    }

    /**
     * Creates a link identifier with source and destination connection point.
     *
     * @param src source connection point
     * @param dst destination connection point
     * @return a link identifier
     */
    public static TsLinkKey tsLinkKey(LTPoint src, LTPoint dst) {
        return new TsLinkKey(src, dst);
    }

    /**
     * Creates a link identifier for the specified link.
     *
     * @param link link descriptor
     * @return a link identifier
     */
    public static TsLinkKey tsLinkKey(Link link) {
        if (link.src() == null || link.dst() == null) {
            checkArgument(link.type() != Link.Type.REGULAR,
                    "TsLinkKey invalid; only ingress/egress link can have null src/dst");
        }
        return new TsLinkKey(link.src(), link.dst(), link.id() );
    }

}
