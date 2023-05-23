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

package org.onap.integration.actninterfacetools.yangutils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.io.IOUtils;
import org.onap.integration.actninterfacetools.globalapi.GlobalService;
import org.onosproject.yang.model.DataNode;
import org.onosproject.yang.model.DefaultResourceData;
import org.onosproject.yang.model.InnerNode;
import org.onosproject.yang.model.NodeKey;
import org.onosproject.yang.model.ResourceData;
import org.onosproject.yang.model.ResourceId;
import org.onosproject.yang.runtime.CompositeData;
import org.onosproject.yang.runtime.CompositeStream;
import org.onosproject.yang.runtime.DefaultCompositeData;
import org.onosproject.yang.runtime.DefaultCompositeStream;
import org.onosproject.yang.runtime.DefaultRuntimeContext;
import org.onosproject.yang.runtime.RuntimeContext;
import org.onosproject.yang.runtime.YangRuntimeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilities used by the RESTCONF app.
 */
public final class CodecConverter {
    /**
     * Data format required by YangRuntime Service.
     */
    private static final String JSON_FORMAT = "JSON";
    private static final String SLASH = "/";
    private static YangRuntimeService YANG_RUNTIME = null ;
    private static final Logger log = LoggerFactory.getLogger(CodecConverter.class);

    public static void active(){
        ServiceLoader<GlobalService> serviceLoader = ServiceLoader.load(GlobalService.class);
        for (GlobalService service : serviceLoader) {
            YANG_RUNTIME = service.getRuntimeService();
        }
    }

    /**
     * No instantiation.
     */
    private CodecConverter() {
    }

    /**
     * Converts an input stream to JSON objectNode.
     *
     * @param inputStream the InputStream from Resource Data
     * @return JSON representation of the data resource
     */
    public static ObjectNode convertInputStreamToObjectNode(InputStream inputStream) {
        if (inputStream == null) {
            return null;
        }

        ObjectNode rootNode = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            rootNode = (ObjectNode) mapper.readTree(inputStream);
        } catch (IOException e) {
            log.error("ERROR: convertInputStreamToObjectNode: ", e);
        }
        return rootNode;
    }

    public static JsonNode convertInputStreamToJsonNode(InputStream inputStream) {
        if (inputStream == null) {
            return null;
        }

        JsonNode rootNode = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            rootNode = mapper.readTree(inputStream);
        } catch (IOException e) {
            log.error("ERROR: convertInputStreamToObjectNode: ", e);
        }
        return rootNode;
    }

    /**
     * Convert ObjectNode to InputStream.
     *
     * @param rootNode JSON representation of the data resource
     * @return the InputStream from Resource Data
     */
    public static InputStream convertObjectNodeToInputStream(ObjectNode rootNode) {
        if (rootNode == null) {
            return null;
        }

        String json = rootNode.toString();
        InputStream inputStream = null;
        try {
            inputStream = IOUtils.toInputStream(json);
        } catch (Exception e) {
            log.error("ERROR: convertObjectNodeToInputStream: ", e);
        }
        return inputStream;
    }

    public static InputStream convertJsonNodeToInputStream(JsonNode rootNode) {
        if (rootNode == null) {
            return null;
        }

        String json = rootNode.toString();
        InputStream inputStream = null;
        try {
            inputStream = IOUtils.toInputStream(json);
        } catch (Exception e) {
            log.error("ERROR: convertObjectNodeToInputStream: ", e);
        }
        return inputStream;
    }

    public static ResourceId uriToRid(URI uri) {
        ResourceData resourceData = objectNodeToResourceData(uri, null);
        return resourceData.resourceId();
    }


    public static DataNode objectNodeToDataNode(URI uri, ObjectNode jsonNode) {
        URI parentUri = ResourceIdUtil.rmLastPathSegment(uri);
        ResourceData resourceData = objectNodeToResourceData(parentUri, jsonNode);
        return resourceData.dataNodes().get(0);
    }

    public static DataNode objectNodeToDataNode(String uriStr, ObjectNode jsonNode) {
        String parentUriStr = ResourceIdUtil.rmLastPathSegmentStr(uriStr);
        return objectNodeToDataNodeWithParentUriStr(parentUriStr, jsonNode);
    }

    public static DataNode objectNodeToDataNodeWithParentUriStr(String parentUriStr, ObjectNode jsonNode) {
        ResourceData resourceData = objectNodeToResourceData(parentUriStr, jsonNode);
        return resourceData.dataNodes().get(0);
    }

    public static DataNode objectNodeToDataNodeWithParentRid(ResourceId parentRid, ObjectNode jsonNode) {
        String parentUriStr = ResourceIdUtil.convertRidToUriStr(parentRid);
        ResourceData resourceData = objectNodeToResourceData(parentUriStr, jsonNode);
        return resourceData.dataNodes().get(0);
    }

    public static DataNode objectNodeToDataNode(ResourceId rId, ObjectNode jsonNode) {
        ResourceId parentRid = ResourceIdUtil.parentOf(rId);
        return objectNodeToDataNodeWithParentRid(parentRid, jsonNode);
    }

    public static ResourceData objectNodeToResourceData(URI parentUri, ObjectNode rootNode) {
        return objectNodeToResourceData(ResourceIdUtil.getRawUriPath(parentUri), rootNode);
    }

    public static DataNode removeRootFromDataNode(DataNode dataNode) {
        if (dataNode instanceof InnerNode && dataNode.key().schemaId().name().equals("/")) {
            Map.Entry<NodeKey, DataNode> entry = ((InnerNode) dataNode).childNodes().entrySet().iterator().next();
            dataNode = entry.getValue();
        }
        return dataNode;
    }

    public static ResourceData objectNodeToResourceData(String parentUriStr, ObjectNode jsonNode) {

        RuntimeContext.Builder runtimeContextBuilder = new DefaultRuntimeContext.Builder();
        runtimeContextBuilder.setDataFormat(JSON_FORMAT);
        RuntimeContext context = runtimeContextBuilder.build();
        ResourceData resourceData = null;
        InputStream jsonData = null;
        try {
            if (jsonNode != null) {
                jsonData = convertObjectNodeToInputStream(jsonNode);
            }
            CompositeStream compositeStream = new DefaultCompositeStream(parentUriStr, jsonData);
            // CompositeStream --- YangRuntimeService ---> CompositeData.
            CompositeData compositeData = YANG_RUNTIME.decode(compositeStream, context);
            resourceData = compositeData.resourceData();
        } catch (Exception ex) {
            log.error("convertJsonToDataNode failure: ", ex);
        }
        return resourceData;
    }


    /**
     * Convert Resource Id and Data Node to Json ObjectNode.
     *
     * @param rid      resource identifier
     * @param dataNode represents type of node in data store
     * @return JSON representation of the data resource
     */
    public static ObjectNode dataNodeToObjectNode(ResourceId rid, DataNode dataNode) {
        return convertInputStreamToObjectNode(dataNodeToStream(rid, dataNode));
    }

    public static InputStream dataNodeToStream(ResourceId rid, DataNode dataNode) {
        RuntimeContext.Builder runtimeContextBuilder = DefaultRuntimeContext.builder();
        runtimeContextBuilder.setDataFormat(JSON_FORMAT);
        RuntimeContext context = runtimeContextBuilder.build();
        DefaultResourceData.Builder resourceDataBuilder = DefaultResourceData.builder();
        resourceDataBuilder.addDataNode(dataNode);
        resourceDataBuilder.resourceId(rid);
        ResourceData resourceData = resourceDataBuilder.build();
        DefaultCompositeData.Builder compositeDataBuilder = DefaultCompositeData.builder();
        compositeDataBuilder.resourceData(resourceData);
        CompositeData compositeData = compositeDataBuilder.build();
        InputStream inputStream = null;
        try {
            // CompositeData --- YangRuntimeService ---> CompositeStream.
            CompositeStream compositeStream = YANG_RUNTIME.encode(compositeData, context);
            inputStream = compositeStream.resourceData();
        } catch (Exception ex) {
            log.error("convertInputStreamToObjectNode failure: ", ex);
        }

        return inputStream;
    }

    public static DataNode removeTopDataNode(DataNode dataNode) {
        if (dataNode instanceof InnerNode && dataNode.key().schemaId().name().equals("/")) {
            Map.Entry<NodeKey, DataNode> entry = ((InnerNode) dataNode).childNodes().entrySet().iterator().next();
            dataNode = entry.getValue();
        }
        return dataNode;
    }
}
