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
package org.onap.integration.actninterfacetools.protocol.restconf.ctl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableMap;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.glassfish.jersey.client.ChunkedInput;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.onap.integration.actninterfacetools.protocol.restconf.PncInstance;
import org.onap.integration.actninterfacetools.protocol.restconf.RestConfSBController;
import org.onap.integration.actninterfacetools.protocol.restconf.RestconfNotificationEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The implementation of RestConfSBController.
 */

public class RestConfSBControllerImpl implements RestConfSBController {
    private static volatile RestConfSBControllerImpl restConfClientInstance = null;
    private static final int STATUS_OK = Response.Status.OK.getStatusCode();
    private static final int STATUS_CREATED = Response.Status.CREATED.getStatusCode();
    private static final int STATUS_ACCEPTED = Response.Status.ACCEPTED.getStatusCode();
    private static final int PARTIAL_CONTENT = Response.Status.PARTIAL_CONTENT.getStatusCode();
    protected static final String DOUBLESLASH = "/";
    protected static final String COLON = ":";
    private static final String XML = "xml";
    private static final String JSON = "json";
    private static final String HTTPS = "https";
    private static final String AUTHORIZATION_PROPERTY = "authorization";
    private static final String BASIC_AUTH_PREFIX = "Basic ";
    private static final Logger log = LoggerFactory
            .getLogger(RestConfSBControllerImpl.class);

    // TODO: for the Ibis release when both RESTCONF server and RESTCONF client
    // fully support root resource discovery, ROOT_RESOURCE constant will be
    // removed and rather the value would get discovered dynamically.
    private static final String ROOT_RESOURCE = "/restconf";

    private static final String RESOURCE_PATH_PREFIX = "/data/";
    private static final String NOTIFICATION_PATH_PREFIX = "/streams/";

    private Map<UUID, Set<RestconfNotificationEventListener>>
            restconfNotificationListenerMap = new ConcurrentHashMap<>();
    private Map<UUID, GetChunksRunnable> runnableTable = new ConcurrentHashMap<>();
    private final Map<UUID, PncInstance> pncMap = new ConcurrentHashMap<>();
    private final Map<UUID, Client> clientMap = new ConcurrentHashMap<>();

    ExecutorService executor = Executors.newCachedThreadPool();

    private RestConfSBControllerImpl(){

    }
    public static RestConfSBControllerImpl getRestConfClient(){
        if(restConfClientInstance != null){
            return restConfClientInstance;
        }
        synchronized (RestConfSBControllerImpl.class) {
            if(restConfClientInstance == null){
                restConfClientInstance = new RestConfSBControllerImpl();

            }
            return restConfClientInstance;
        }
    }

    public void activate() {
        log.info("RESTCONF SBI Started");
    }

    public void deactivate() {
        log.info("RESTCONF SBI Stopped");
        executor.shutdown();
        this.clientMap.clear();
        this.pncMap.clear();
    }

    public Map<UUID, PncInstance> getPncInstances() {
        log.trace("RESTCONF SBI::getDevices");
        return ImmutableMap.copyOf(pncMap);
    }

    public PncInstance getPncInstance(UUID pncInfo) {
        log.trace("RESTCONF SBI::getDevice with deviceId");
        return pncMap.get(pncInfo);
    }

    public PncInstance getPncInstance(InetAddress ip, int port) {
        log.trace("RESTCONF SBI::getDevice with ip and port");
        return pncMap.values().stream().filter(v -> v.ip().equals(ip) && v.port() == port).findFirst().get();
    }

    public void addPncInstance(PncInstance pncInstance) {
        log.trace("RESTCONF SBI::addDevice");
        if (!pncMap.containsKey(pncInstance.pncId())) {
            Client client = ignoreSslClient();
            if (pncInstance.username() != null) {
                String username = pncInstance.username();
                String password = pncInstance.password() == null ? "" : pncInstance.password();
                client.register(HttpAuthenticationFeature.basic(username, password));
            }
            clientMap.put(pncInstance.pncId(), client);
            pncMap.put(pncInstance.pncId(), pncInstance);
        } else {
            log.warn("Trying to add a device that is already existing {}", pncInstance.pncId());
        }
    }
    private Client ignoreSslClient() {
        SSLContext sslcontext = null;

        try {
            sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init(null, new TrustManager[]{new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }}, new java.security.SecureRandom());
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }

        return ClientBuilder.newBuilder().sslContext(sslcontext).hostnameVerifier((s1, s2) -> true).build();
    }

    public void removeDevice(UUID pncId) {
        log.trace("RESTCONF SBI::removeDevice");
        clientMap.remove(pncId);
        pncMap.remove(pncId);
    }
    @Override
    public boolean post(UUID pncId, String request, ObjectNode payload,
                        String mediaType) {
        request = discoverRootResource(pncId) + RESOURCE_PATH_PREFIX
                + request;
        return checkStatusCode(post(pncId, request, payload, typeOfMediaType(mediaType)));
    }
    private MediaType typeOfMediaType(String type) {
        switch (type) {
            case XML:
                return MediaType.APPLICATION_XML_TYPE;
            case JSON:
                return MediaType.APPLICATION_JSON_TYPE;
            case MediaType.WILDCARD:
                return MediaType.WILDCARD_TYPE;
            default:
                throw new IllegalArgumentException("Unsupported media type " + type);

        }
    }
    private boolean checkStatusCode(int statusCode) {
        if (statusCode == STATUS_OK || statusCode == STATUS_CREATED || statusCode == STATUS_ACCEPTED) {
            return true;
        } else {
            log.error("Failed request, HTTP error code : " + statusCode);
            return false;
        }
    }
    @Override
    public int post(UUID pndId, String request, ObjectNode payload, MediaType mediaType) {
        Response response = getResponse(pndId, request, payload, mediaType);
        if (response == null) {
            return Response.Status.NO_CONTENT.getStatusCode();
        }
        return response.getStatus();
    }
    private Response getResponse(UUID pncId, String request, ObjectNode payload, MediaType mediaType) {

        WebTarget wt = getWebTarget(pncId, request);

        Response response = null;
        if (payload != null) {
            try {
                response = wt.request(mediaType)
                        .post(Entity.entity(payload.toString(), mediaType));
            } catch (Exception e) {
                log.error("Cannot do POST {} request on device {} because can't read payload", request, pncId);
            }
        } else {
            response = wt.request(mediaType).post(Entity.entity(null, mediaType));
        }
        return response;
    }
    protected WebTarget getWebTarget(UUID pncId, String request) {
        log.info("Sending request to URL {} ", getUrlString(pncId, request));
        return clientMap.get(pncId).target(getUrlString(pncId, request));
    }
    protected String getUrlString(UUID pncId, String request) {
        PncInstance pncInstance = pncMap.get(pncId);
        if (pncInstance == null) {
            log.warn("restSbDevice cannot be NULL!");
            return "";
        }
        if (pncInstance.url() != null) {
            return pncInstance.protocol() + COLON + DOUBLESLASH + pncInstance.url() + request;
        } else {
            return pncInstance.protocol() + COLON + DOUBLESLASH + pncInstance.ip().toString()
                    + COLON + pncInstance.port() + request;
        }
    }
    @Override
    public <T> T post(UUID pncId, String request, ObjectNode payload,
                      String mediaType, Class<T> responseClass) {
        request = discoverRootResource(pncId) + RESOURCE_PATH_PREFIX
                + request;
        return post(pncId, request, payload, typeOfMediaType(mediaType), responseClass);
    }
    @Override
    public <T> T post(UUID device, String request, ObjectNode payload, MediaType mediaType,
                      Class<T> responseClass) {
        Response response = getResponse(device, request, payload, mediaType);
        if (response != null && response.hasEntity()) {
            return responseClass == Response.class ? (T) response : response.readEntity(responseClass);
        }
        log.error("Response from device {} for request {} contains no entity", device, request);
        return null;
    }
    @Override
    public boolean put(UUID pncId, String request, ObjectNode payload,
                       String mediaType) {
        request = discoverRootResource(pncId) + RESOURCE_PATH_PREFIX
                + request;
        return checkStatusCode(put(pncId, request, payload, typeOfMediaType(mediaType)));
    }
    @Override
    public int put(UUID pncId, String request, ObjectNode payload, MediaType mediaType) {

        WebTarget wt = getWebTarget(pncId, request);

        Response response = null;
        if (payload != null) {
            try {
                response = wt.request(mediaType).put(Entity.entity(payload.toString(), mediaType));
            } catch (Exception e) {
                log.error("Cannot do PUT {} request on device {} because can't read payload", request, pncId);
            }
        } else {
            response = wt.request(mediaType).put(Entity.entity(null, mediaType));
        }

        if (response == null) {
            return Response.Status.NO_CONTENT.getStatusCode();
        }
        return response.getStatus();
    }
    @Override
    public ObjectNode get(UUID pncId, String request, String mediaType) {
        request = discoverRootResource(pncId) + RESOURCE_PATH_PREFIX
                + request;
        return get(pncId, request, typeOfMediaType(mediaType));
    }
    public ObjectNode get(UUID pncId, String request, MediaType mediaType) {
        WebTarget wt = getWebTarget(pncId, request);

        Response s = wt.request(mediaType).get();

        if (checkReply(s)) {
            try {
                String json = s.readEntity((String.class));
                return new ObjectMapper().readTree(json).deepCopy();
            } catch (Exception ex) {
                log.error("ERROR: ", ex);
            }
        }

        return null;
    }
    private boolean checkReply(Response response) {
        if (response != null) {
            boolean statusCode = checkStatusCode(response.getStatus());
            if (!statusCode && response.hasEntity()) {
                log.error("Failed request, HTTP error msg : " + response.readEntity(String.class));
            }
            return statusCode;
        }
        log.error("Null reply from device");
        return false;
    }
    @Override
    public boolean patch(UUID pncId, String request, ObjectNode payload,
                         String mediaType) {
        request = discoverRootResource(pncId) + RESOURCE_PATH_PREFIX
                + request;
        return checkStatusCode(patch(pncId, request, payload, typeOfMediaType(mediaType)));
    }
    @Override
    public int patch(UUID pncId, String request, ObjectNode payload, MediaType mediaType) {

        try {
            log.debug("Url request {} ", getUrlString(pncId, request));
            HttpPatch httprequest = new HttpPatch(getUrlString(pncId, request));
            if (pncMap.get(pncId).username() != null) {
                String pwd = pncMap.get(pncId).password() == null ? "" : COLON + pncMap.get(pncId).password();
                String userPassword = pncMap.get(pncId).username() + pwd;
                String base64string = Base64.getEncoder().encodeToString(userPassword.getBytes(StandardCharsets.UTF_8));
                httprequest.addHeader(AUTHORIZATION_PROPERTY, BASIC_AUTH_PREFIX + base64string);
            }
            if (payload != null) {
                StringEntity input = new StringEntity(payload.toString());
                input.setContentType(mediaType.toString());
                httprequest.setEntity(input);
            }
            CloseableHttpClient httpClient;
            if (pncMap.containsKey(pncId) && pncMap.get(pncId).protocol().equals(HTTPS)) {
                httpClient = getApacheSslBypassClient();
            } else {
                httpClient = HttpClients.createDefault();
            }
            return httpClient.execute(httprequest).getStatusLine().getStatusCode();
        } catch (IOException | NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            log.error("Cannot do PATCH {} request on device {}", request, pncId, e);
        }
        return Response.Status.BAD_REQUEST.getStatusCode();
    }
    private CloseableHttpClient getApacheSslBypassClient() throws NoSuchAlgorithmException,
            KeyManagementException, KeyStoreException {
        return HttpClients.custom().
                setHostnameVerifier(new AllowAllHostnameVerifier()).
                setSslcontext(new SSLContextBuilder()
                        .loadTrustMaterial(null, (arg0, arg1) -> true)
                        .build()).build();
    }
    @Override
    public boolean delete(UUID pncId, String request,
                          String mediaType) {
        request = discoverRootResource(pncId) + RESOURCE_PATH_PREFIX
                + request;
        return checkStatusCode(delete(pncId, request, typeOfMediaType(mediaType)));
    }
    @Override
    public int delete(UUID pncId, String request,  MediaType mediaType) {

        WebTarget wt = getWebTarget(pncId, request);

        Response response = wt.request(mediaType).delete();

        return response.getStatus();
    }
    @Override
    public void enableNotifications(UUID pncId, String request,
                                    String mediaType,
                                    RestconfNotificationEventListener listener) {

        if (isNotificationEnabled(pncId)) {
            log.warn("enableNotifications: already enabled on device: {}", pncId);
            return;
        }

        request = discoverRootResource(pncId) + NOTIFICATION_PATH_PREFIX
                + request;

        addNotificationListener(pncId, listener);

        GetChunksRunnable runnable = new GetChunksRunnable(request, mediaType,
                pncId);
        runnableTable.put(pncId, runnable);
        executor.execute(runnable);
    }

    public void stopNotifications(UUID pncId) {
        runnableTable.get(pncId).terminate();
        runnableTable.remove(pncId);
        restconfNotificationListenerMap.remove(pncId);
        log.debug("Stop sending notifications for device URI: " + pncId.toString());

    }

    public class GetChunksRunnable implements Runnable {
        private String request;
        private String mediaType;
        private UUID pnc;

        private volatile boolean running = true;

        public void terminate() {
            running = false;
        }

        /**
         * @param request   request
         * @param mediaType media type
         * @param pncId    PNC identifier
         */
        public GetChunksRunnable(String request, String mediaType,
                                 UUID pncId) {
            this.request = request;
            this.mediaType = mediaType;
            this.pnc = pncId;
        }

        @Override
        public void run() {
            WebTarget wt = getWebTarget(pnc, request);
            Response clientResp = wt.request(mediaType).get();
            Set<RestconfNotificationEventListener> listeners =
                    restconfNotificationListenerMap.get(pnc);
            final ChunkedInput<String> chunkedInput = (ChunkedInput<String>) clientResp
                    .readEntity(new GenericType<ChunkedInput<String>>() {
                    });

            String chunk;
            // Note that the read() is a blocking operation and the invoking
            // thread is blocked until a new chunk comes. Jersey implementation
            // of this IO operation is in a way that it does not respond to
            // interrupts.
            while (running) {
                chunk = chunkedInput.read();
                if (chunk != null) {
                    if (running) {
                        for (RestconfNotificationEventListener listener : listeners) {
                            listener.handleNotificationEvent(pnc, chunk);
                        }
                    } else {
                        log.trace("the requesting client is no more interested "
                                          + "to receive such notifications.");
                    }
                } else {
                    log.trace("The received notification chunk is null. do not continue any more.");
                    break;
                }
            }
            log.trace("out of while loop -- end of run");
        }
    }

    public String discoverRootResource(UUID pncId) {
        // FIXME: send a GET command to the device to discover the root resource.
        // The plan to fix this is for the Ibis release when the RESTCONF server and
        // the RESTCONF client both support root resource discovery.
        return ROOT_RESOURCE;
    }

    @Override
    public void addNotificationListener(UUID pncId,
                                        RestconfNotificationEventListener listener) {
        Set<RestconfNotificationEventListener> listeners =
                restconfNotificationListenerMap.get(pncId);
        if (listeners == null) {
            listeners = new HashSet<>();
        }

        listeners.add(listener);

        this.restconfNotificationListenerMap.put(pncId, listeners);
    }

    @Override
    public void removeNotificationListener(UUID pncId,
                                           RestconfNotificationEventListener listener) {
        Set<RestconfNotificationEventListener> listeners =
                restconfNotificationListenerMap.get(pncId);
        if (listeners != null) {
            listeners.remove(listener);
        }
    }

    public boolean isNotificationEnabled(UUID pncId) {
        return runnableTable.containsKey(pncId);
    }
}
