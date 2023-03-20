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

import com.google.common.base.Objects;

import java.util.Arrays;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Node Key.
 */
public class TeNodeKey {

    private final long teNodeId;
    private final long providerId;
    private final long clientId;
    private final long topologyId;

    /**
     * Creates a TE node key.
     *
     * @param teNodeKey TE node key
     */
    public TeNodeKey(TeNodeKey teNodeKey) {
        this.providerId = teNodeKey.providerId();
        this.clientId = teNodeKey.clientId();
        this.topologyId = teNodeKey.topologyId();
        this.teNodeId = teNodeKey.teNodeId();
    }

    /**
     * Creates a TE node key.
     *
     * @param providerId provider identifier
     * @param clientId   client identifier
     * @param topologyId topology identifier
     * @param teNodeId   TE node identifier
     */
    public TeNodeKey(long providerId, long clientId,
                     long topologyId, long teNodeId) {
        this.providerId = providerId;
        this.clientId = clientId;
        this.topologyId = topologyId;
        this.teNodeId = teNodeId;
    }

    /**
     * Returns the TE Node identifier.
     *
     * @return the TE node id
     */
    public long teNodeId() {
        return teNodeId;
    }

    public long providerId() {
        return providerId;
    }

    public long clientId() {
        return clientId;
    }

    public long topologyId() {
        return topologyId;
    }
    /**
     * Parse a tenodekey from a string
     * @param string
     * @return a TeNodeKey based on provided string
     */
    public static TeNodeKey of(String string) {
        checkNotNull(string);
        String [] pieces = string.split("\\.");
        long [] ids = Arrays.stream(pieces).mapToLong(x -> Long.parseLong(x)).toArray();
        checkArgument( pieces.length == 4,
                "TeNodeKeyString must be in \"prviderId.clientid.topologyid.tenodeid\" format, but got %s", string);
        return new TeNodeKey(ids[0], ids[1], ids[2], ids[3]);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(providerId, clientId, topologyId, teNodeId);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof TeNodeKey) {
            TeNodeKey that = (TeNodeKey) object;
            return Objects.equal(this.providerId, that.providerId) &&
                    Objects.equal(this.clientId, that.clientId) &&
                    Objects.equal(this.topologyId, that.topologyId) &&
                    Objects.equal(this.teNodeId, that.teNodeId);
        }
        return false;
    }

    @Override
    public String toString() {
        return  this.providerId() + "." +
                this.clientId() + "." +
                this.topologyId() + "." +
                this.teNodeId();
    }
}
