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

import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.ws.rs.core.MediaType;
import java.net.InetAddress;
import java.util.Map;
import java.util.UUID;

/**
 * Abstraction of a RESTCONF controller. Serves as a one-stop shop for obtaining
 * RESTCONF southbound devices and (un)register listeners.
 */
public interface RestConfSBController {
    /**
     * Returns all the devices known to this controller.
     *
     * @return map of devices
     */
    Map<UUID, PncInstance> getPncInstances();

    /**
     * Returns a device by node identifier.
     *
     * @param deviceInfo node identifier
     * @return RestSBDevice rest device
     */
    PncInstance getPncInstance(UUID deviceInfo);

    /**
     * Returns a device by Ip and Port.
     *
     * @param ip device ip
     * @param port device port
     * @return RestSBDevice rest device
     */
    PncInstance getPncInstance(InetAddress ip, int port);

    /**
     * Adds a device to the device map.
     *
     * @param pncInstance to be added
     */
    void addPncInstance(PncInstance pncInstance);

    /**
     * Removes the device from the devices map.
     *
     * @param pncId to be removed
     */
    void removeDevice(UUID pncId);
    /**
     * This method is to be called by whoever is interested to receive
     * Notifications from a specific device. It does a REST GET request
     * with specified parameters to the device, and calls the provided
     * callBackListener upon receiving notifications to notify the requester
     * about notifications.
     *
     * @param pncId           pnc to make the request to
     * @param request          url of the request
     * @param mediaType        format to retrieve the content in
     * @param callBackListener method to call when notifications arrives
     */
    void enableNotifications(UUID pncId, String request, String mediaType,
                             RestconfNotificationEventListener callBackListener);

    /**
     * Registers a listener for notification events that occur to restconf
     * devices.
     *
     * @param pncId identifier of the pnc to which the listener is attached
     * @param listener the listener to notify
     */
    void addNotificationListener(UUID pncId,
                                 RestconfNotificationEventListener listener);

    /**
     * Unregisters the listener for the device.
     *
     * @param pncId identifier of the pnc for which the listener
     *                 is to be removed
     * @param listener listener to be removed
     */
    void removeNotificationListener(UUID pncId,
                                    RestconfNotificationEventListener listener);

    /**
     * Returns true if a listener has been installed to listen to RESTCONF
     * notifications sent from a particular device.
     *
     * @param pncId identifier of the pnc from which the notifications
     *                 are generated
     * @return true if listener is installed; false otherwise
     */
    boolean isNotificationEnabled(UUID pncId);

    /**
     * HTTP POST request with specified parameters to the pnc.
     *
     * @param pncId pnc to make the request to
     * @param request url of the request
     * @param payload payload of the request as an ObjectNode
     * @param mediaType type of content in the payload i.e. application/json
     * @return status Commonly used status codes defined by HTTP
     */
    int post(UUID pncId, String request, ObjectNode payload, MediaType mediaType);
    /**
     * HTTP POST request with specified parameters to the pnc.
     *
     * @param pncId pnc to make the request to
     * @param request url of the request
     * @param payload payload of the request as an ObjectNode
     * @param mediaType type of content in the payload in the form of string i.e. "application/json"
     * @return true if status defined by HTTP is OK, CREATED, ACCEPTED or PARTIAL-CONTEND, otherwise return false
     */
    boolean post(UUID pncId, String request, ObjectNode payload, String mediaType);

    <T> T post(UUID pncId, String request, ObjectNode payload, String mediaType, Class<T> responseClass);

    <T> T post(UUID pncId, String request, ObjectNode payload, MediaType mediaType, Class<T> responseClass);

    /**
     * HTTP PUT request with specified parameters to the device.
     *
     * @param pncId pnc to make the request to
     * @param request resource path of the request
     * @param payload payload of the request as an ObjectNode
     * @param mediaType type of content in the payload i.e. application/json
     * @return status Commonly used status codes defined by HTTP
     */
    int put(UUID pncId, String request, ObjectNode payload, MediaType mediaType);

    /**
     * HTTP PUT request with specified parameters to the pnc.
     *
     * @param pncId pnc to make the request to
     * @param request url of the request
     * @param payload payload of the request as an ObjectNode
     * @param mediaType type of content in the payload in the form of string i.e. "application/json"
     * @return true if status defined by HTTP is OK, CREATED, ACCEPTED or PARTIAL-CONTEND, otherwise return false
     */
    boolean put(UUID pncId, String request, ObjectNode payload, String mediaType);

    /**
     * HTTP PATCH request with specified parameters to the device.
     *
     * @param pncId pnc to make the request to
     * @param request url of the request
     * @param payload payload of the request as an ObjectNode
     * @param mediaType format to retrieve the content in
     * @return status Commonly used status codes defined by HTTP
     */
    int patch(UUID pncId, String request, ObjectNode payload, MediaType mediaType);

    /**
     * HTTP PATCH request with specified parameters to the pnc.
     *
     * @param pncId pnc to make the request to
     * @param request url of the request
     * @param payload payload of the request as an ObjectNode
     * @param mediaType type of content in the payload i.e. application/json
     * @return status Commonly used status codes defined by HTTP
     */
    boolean patch(UUID pncId, String request, ObjectNode payload, String mediaType);

    /**
     * HTTP DELETE request with specified parameters to the device.
     *
     * @param pncId pnc to make the request to
     * @param request url of the request
     * @param mediaType type of content in the payload i.e. application/json
     * @return status Commonly used status codes defined by HTTP
     */
    int delete(UUID pncId, String request,  MediaType mediaType);

    /**
     * HTTP DELETE request with specified parameters to the pnc.
     *
     * @param pncId pnc to make the request to
     * @param request url of the request
     * @param mediaType type of content in the payload i.e. application/json
     * @return status Commonly used status codes defined by HTTP
     */
    boolean delete(UUID pncId, String request,  String mediaType);

    /**
     *
     * HTTP GET request with specified parameters to the device.
     *
     * @param pncId pnc to make the request to
     * @param request url of the request
     * @param mediaType format to retrieve the content in
     * @return an ObjectNode of data from the reply.
     */
    ObjectNode get(UUID pncId, String request, String mediaType);

}
