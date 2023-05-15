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

package org.onap.integration.actninterfacetools.protocol.restconf;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import java.net.InetAddress;
import java.util.Objects;
import java.util.UUID;

/**
 * Default implementation for Rest devices.
 */
public class DefaultPncInstance implements PncInstance {
    private static final String REST = "rest";
    private static final String COLON = ":";

    private final UUID pncId;
    private final InetAddress ip;
    private final int port;
    private final String username;
    private final String password;
    private String protocol;
    private String url;

    public DefaultPncInstance(InetAddress ip, int port, String name, String password,
                              String protocol, String url) {
        this.pncId = UUID.randomUUID();
        Preconditions.checkNotNull(ip, "IP address cannot be null");
        Preconditions.checkArgument(port > 0, "Port address cannot be negative");
        Preconditions.checkNotNull(protocol, "protocol address cannot be null");
        this.ip = ip;
        this.port = port;
        this.username = name;
        this.password = StringUtils.isEmpty(password) ? null : password;
        this.protocol = protocol;
        this.url = StringUtils.isEmpty(url) ? null : url;
    }

    @Override
    public InetAddress ip() {
        return this.ip;
    }

    @Override
    public int port() {
        return this.port;
    }

    @Override
    public String username() {
        return this.username;
    }

    @Override
    public String password() {
        return this.password;
    }

    @Override
    public UUID pncId() {
        return this.pncId;
    }

    @Override
    public String protocol() {
        return protocol;
    }

    @Override
    public String url() {
        return url;
    }


    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("url", url)
                .add("protocol", protocol)
                .add("username", username)
                .add("port", port)
                .add("ip", ip)
                .toString();

    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof PncInstance)) {
            return false;
        }
        PncInstance pncInstance = (PncInstance) obj;
        return this.username.equals(pncInstance.username()) && this.ip.equals(pncInstance.ip()) &&
                this.port == pncInstance.port();

    }

    @Override
    public int hashCode() {
        return Objects.hash(ip, port);
    }

}
