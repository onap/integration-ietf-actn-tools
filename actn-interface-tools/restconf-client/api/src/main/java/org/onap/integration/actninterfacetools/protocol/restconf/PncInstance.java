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

import java.net.InetAddress;
import java.util.Optional;
import java.util.UUID;

/**
 * Represents an abstraction of a Pnc.
 */
public interface PncInstance {
    /**
     * Returns the ip of the Pnc Service.
     *
     * @return ip
     */
    InetAddress ip();

    /**
     * Returns the port of the Pnc Service.
     *
     * @return port
     */
    int port();

    /**
     * Returns the username of the Pnc Service.
     *
     * @return username
     */
    String username();

    /**
     * Returns the password of the Pnc Service.
     *
     * @return password
     */
    String password();

    /**
     * Returns the pncId.
     *
     * @return pncId
     */
    UUID pncId();


    /**
     * Returns the protocol for the REST request, usually HTTP o HTTPS.
     *
     * @return protocol
     */
    String protocol();

    /**
     * Returns the url for the REST requests, to be used instead of IP and PORT.
     *
     * @return url
     */
    String url();

}
