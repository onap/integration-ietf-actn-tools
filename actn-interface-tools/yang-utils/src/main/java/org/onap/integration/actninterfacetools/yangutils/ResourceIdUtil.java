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

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.dom4j.Attribute;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.onosproject.yang.compiler.datamodel.YangSchemaNode;
import org.onosproject.yang.model.KeyLeaf;
import org.onosproject.yang.model.LeafListKey;
import org.onosproject.yang.model.ListKey;
import org.onosproject.yang.model.NodeKey;
import org.onosproject.yang.model.ResourceData;
import org.onosproject.yang.model.ResourceId;
import org.onosproject.yang.runtime.AnnotatedNodeInfo;
import org.onosproject.yang.runtime.Annotation;
import org.onosproject.yang.runtime.DefaultAnnotatedNodeInfo;
import org.onosproject.yang.runtime.DefaultAnnotation;
import org.onosproject.yang.runtime.DefaultRuntimeContext;
import org.onosproject.yang.runtime.DefaultYangSerializerContext;
import org.onosproject.yang.runtime.RuntimeContext;
import org.onosproject.yang.runtime.SerializerHelper;
import org.onosproject.yang.runtime.YangSerializerContext;
import org.onosproject.yang.runtime.impl.DefaultYangModelRegistry;
import org.onosproject.yang.serializers.utils.SerializerUtilException;
import org.slf4j.Logger;

import javax.ws.rs.core.UriBuilder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * The class services as a placeholder for utilities related to
 * generic ResourceId operations.
 */
public final class ResourceIdUtil {
    public static final String ROOT_NAME = "/";
    public static final String ROOT_NS = null;
    public static final ResourceId ROOT_ID = ResourceId.builder().addBranchPointSchema(ROOT_NAME, ROOT_NS).build();

    private static final Logger log = getLogger(ResourceIdUtil.class);
    private static final String JSON_FORMAT = "JSON";
    private static final Splitter SLASH_SPLITTER = Splitter.on('/');
    private static final Splitter COMMA_SPLITTER = Splitter.on(',');
    private static final String QUOTES = "\"";
    private static final String ROOT_ELEMENT_START = "<root ";
    private static final String ROOT_ELEMENT_END = "</root>";
    private static final String URI_ENCODING_CHAR_SET = "ISO-8859-1";
    private static final String UTF8_ENCODING = "utf-8";
    private static final String ERROR_LIST_MSG = "List/Leaf-list node should be " +
            "in format \"nodeName=key\"or \"nodeName=instance-value\"";
    private static final String EQUAL = "=";
    private static final String COMMA = ",";
    private static final String COLON = ":";
    private static final String SLASH = "/";
    private static final String URI_ENCODED_SLASH = "%2F";
    private static final String URI_ENCODED_COLON = "%3A";


    // no instantiation
    private ResourceIdUtil() {
    }

    /**
     * Converts XML atrtibutes into annotated node info.
     *
     * @param element XML element
     * @param id      resource id of an element
     * @return annotated node info
     */
    public static AnnotatedNodeInfo convertXmlAttributesToAnnotations(Element element,
                                                                      ResourceId id) {
        Iterator iter = element.attributeIterator();
        if (!iter.hasNext()) {
            // element does not have any attributes
            return null;
        }
        AnnotatedNodeInfo.Builder builder = DefaultAnnotatedNodeInfo.builder();
        builder = builder.resourceId(id);
        while (iter.hasNext()) {
            Attribute attr = (Attribute) iter.next();
            DefaultAnnotation annotation = new DefaultAnnotation(
                    attr.getQualifiedName(), attr.getValue());
            builder = builder.addAnnotation(annotation);
        }
        return builder.build();
    }


    /**
     * Appends the XML data with root element.
     *
     * @param inputStream        XML data
     * @param protocolAnnotation list of annoations for root element
     * @return XML with root element
     * @throws DocumentException if root element cannot be created
     * @throws IOException       if input data cannot be read
     */
    public static String addRootElementWithAnnotation(InputStream inputStream,
                                                      List<Annotation>
                                                              protocolAnnotation)
            throws DocumentException, IOException {
        BufferedReader br;
        StringBuilder sb = new StringBuilder();
        String xmlData;
        // Parse composite stream resourceData
        br = new BufferedReader(new InputStreamReader(inputStream));
        while ((xmlData = br.readLine()) != null) {
            sb.append(xmlData);
        }

        StringBuilder rootElement = new StringBuilder(ROOT_ELEMENT_START);
        if (protocolAnnotation != null) {
            for (Annotation annotation : protocolAnnotation) {
                rootElement.append(annotation.name()).append(EQUAL)
                        .append(QUOTES).append(annotation.value()).append(QUOTES);
            }
        }
        rootElement.append(">").append(sb.toString()).append(ROOT_ELEMENT_END);
        return rootElement.toString();
    }

    public static ResourceId convertUriToRid(URI uri) {
        if (uri == null) {
            return null;
        }

        return convertUriStrToRid(getRawUriPath(uri));
    }

    public static ResourceId convertUriStrToRid(String uriStr) {
        if (uriStr == null || uriStr.isEmpty()) {
            return null;
        }

        ResourceData resourceData = CodecConverter.objectNodeToResourceData(uriStr, null);
        return resourceData.resourceId();
    }

    /**
     * Converts a URI string to resource identifier.
     *
     * @param uriString given URI
     * @param context   YANG schema context information
     * @return resource ID
     */
    @Deprecated
    public static ResourceId.Builder convertUriStrToRid(String uriString,
                                                        YangSerializerContext context) {
        if (uriString == null || uriString.isEmpty()) {
            return null;
        }
        List<String> paths = Arrays.asList(uriString.split(SLASH));

        if (!paths.isEmpty()) {
            ResourceId.Builder ridBuilder =
                    SerializerHelper.initializeResourceId(context);
            processPathSegments(paths, ridBuilder);
            return ridBuilder;
        }

        return null;
    }

    /**
     * Converts a list of path from the original format to ISO-8859-1 code.
     *
     * @param paths the original paths
     * @return list of decoded paths
     */
    @Deprecated
    public static List<String> urlPathArgsDecode(Iterable<String> paths) {
        try {
            List<String> decodedPathArgs = new ArrayList<>();
            for (String pathArg : paths) {
                String decode = URLDecoder.decode(pathArg,
                                                  URI_ENCODING_CHAR_SET);
                decodedPathArgs.add(decode);
            }
            return decodedPathArgs;
        } catch (UnsupportedEncodingException e) {
            throw new SerializerUtilException("Invalid URL path arg '" +
                                                      paths + "': ", e);
        }
    }

    private static ResourceId.Builder processPathSegments(List<String> paths,
                                                          ResourceId.Builder builder) {
        if (paths.isEmpty()) {
            return builder;
        }

        boolean isLastSegment = paths.size() == 1;

        String segment = paths.iterator().next();
        processSinglePathSegment(segment, builder);

        if (isLastSegment) {
            // We have hit the base case of recursion.
            return builder;
        }

        /*
         * Chop off the first segment, and recursively process the rest
         * of the path segments.
         */
        List<String> remainPaths = paths.subList(1, paths.size());
        processPathSegments(remainPaths, builder);

        return builder;
    }

    private static void processSinglePathSegment(String pathSegment,
                                                 ResourceId.Builder builder) {
        if (pathSegment.contains(COLON)) {
            processPathSegmentWithNamespace(pathSegment, builder);
        } else {
            processPathSegmentWithoutNamespace(pathSegment, builder);
        }
    }

    private static void processPathSegmentWithNamespace(String pathSegment,
                                                        ResourceId.Builder builder) {

        String nodeName = getLatterSegment(pathSegment, COLON);
        String namespace = getPreSegment(pathSegment, COLON);
        addNodeNameToRid(nodeName, namespace, builder);
    }

    private static void processPathSegmentWithoutNamespace(String nodeName,
                                                           ResourceId.Builder builder) {
        addNodeNameToRid(nodeName, null, builder);
    }

    private static void addNodeNameToRid(String nodeName,
                                         String namespace,
                                         ResourceId.Builder builder) {
        if (nodeName.contains(EQUAL)) {
            addListOrLeafList(nodeName, namespace, builder);
        } else {
            addLeaf(nodeName, namespace, builder);
        }
    }

    private static void addListOrLeafList(String path,
                                          String namespace,
                                          ResourceId.Builder builder) {
        String nodeName = getPreSegment(path, EQUAL);
        String keyStr = getLatterSegment(path, EQUAL);
        if (keyStr == null) {
            throw new SerializerUtilException(ERROR_LIST_MSG);
        }

        List<String> keys = uriDecodedKeys(keyStr);
        SerializerHelper.addToResourceId(builder, nodeName, namespace, keys);
    }

    private static List<String> uriDecodedKeys(String keyStr) {
        List<String> decodedKeys = Lists.newArrayList();

        if (keyStr.contains(COMMA)) {
            List<String> encodedKeys = Lists.newArrayList(COMMA_SPLITTER.split(keyStr));
            for (String encodedKey : encodedKeys) {
                decodedKeys.add(uriDecodedString(encodedKey));
            }
        } else {
            decodedKeys.add(uriDecodedString(keyStr));
        }

        return decodedKeys;
    }


    private static String uriDecodedString(String keyStr) {
        try {
            keyStr = URLDecoder.decode(keyStr, UTF8_ENCODING);
        } catch (UnsupportedEncodingException ex) {
            throw new SerializerUtilException("UnsupportedEncodingException: " + ex.getMessage());
        }

        return keyStr;
    }

    private static void addLeaf(String nodeName,
                                String namespace,
                                ResourceId.Builder builder) {
        checkNotNull(nodeName);
        String value = null;
        SerializerHelper.addToResourceId(builder, nodeName, namespace, value);
    }

    /**
     * Returns the previous segment of a path which is separated by a split char.
     * For example:
     * <pre>
     * "foo:bar", ":" to "foo"
     * </pre>
     *
     * @param path      the original path string
     * @param splitChar char used to split the path
     * @return the previous segment of the path
     */
    public static String getPreSegment(String path, String splitChar) {
        int idx = path.lastIndexOf(splitChar);
        if (idx == -1) {
            return null;
        }
        return path.substring(0, idx);
    }

    /**
     * Returns the latter segment of a path which is separated by a split char.
     * For example:
     * <pre>
     * "foo:bar", ":" to "bar"
     * </pre>
     *
     * @param path      the original path string
     * @param splitChar char used to split the path
     * @return the latter segment of the path
     */
    public static String getLatterSegment(String path, String splitChar) {
        int idx = path.lastIndexOf(splitChar);
        if (idx == -1) {
            return path;
        }

        return path.substring(idx + 1);
    }

    /**
     * Converts a resource identifier to URI string.
     *
     * @param rid resource identifier
     * @return URI
     */
    public static String convertRidToUriStr(ResourceId rid) {
        if (rid == null) {
            return null;
        }

        RuntimeContext.Builder runtimeContextBuilder = new DefaultRuntimeContext.Builder();
        runtimeContextBuilder.setDataFormat(JSON_FORMAT);
        RuntimeContext rc = runtimeContextBuilder.build();
        YangSerializerContext context = new DefaultYangSerializerContext(null,
                                                                         rc.getProtocolAnnotations());

        StringBuilder uriBuilder = new StringBuilder();
        List<NodeKey> nodeKeyList = rid.nodeKeys();
        String curNameSpace = null;
        for (NodeKey key : nodeKeyList) {
            curNameSpace = addNodeKeyToUri(key, curNameSpace, uriBuilder, context);
        }
        return trimAtLast(uriBuilder.toString(), SLASH);
    }

    private static String addNodeKeyToUri(NodeKey key,
                                          String curNameSpace,
                                          StringBuilder uriBuilder,
                                          YangSerializerContext context) {
        String newNameSpace = null;
        if (key instanceof LeafListKey) {
            newNameSpace = addLeafListNodeToUri((LeafListKey) key,
                                                curNameSpace, uriBuilder, context);
        } else if (key instanceof ListKey) {
            newNameSpace = addListNodeToUri((ListKey) key, curNameSpace,
                                            uriBuilder, context);
        } else {
            String name = key.schemaId().name();
            if (!name.equals(SLASH)) {
                newNameSpace = addNodeNameToUri(key, curNameSpace,
                                                uriBuilder, context);
            }
        }
        return newNameSpace;
    }

    private static String addLeafListNodeToUri(LeafListKey key,
                                               String curNameSpace,
                                               StringBuilder uriBuilder,
                                               YangSerializerContext context) {

        String newNameSpace = addNodeNameToUri(key, curNameSpace, uriBuilder,
                                               context);
        uriBuilder.append(EQUAL);
        uriBuilder.append(key.asString());
        return newNameSpace;
    }

    private static String addListNodeToUri(ListKey key,
                                           String curNameSpace,
                                           StringBuilder uriBuilder,
                                           YangSerializerContext context) {
        String newNameSpace = addNodeNameToUri(key, curNameSpace, uriBuilder,
                                               context);
        uriBuilder.append(EQUAL);
        String prefix = "";
        for (KeyLeaf keyLeaf : key.keyLeafs()) {
            uriBuilder.append(prefix);
            prefix = COMMA;
            uriBuilder.append(keyLeaf.leafValue().toString());
        }

        return newNameSpace;
    }

    private static String addNodeNameToUri(NodeKey key,
                                           String curNameSpace,
                                           StringBuilder uriBuilder,
                                           YangSerializerContext context) {
        String newNameSpace = key.schemaId().namespace();
        if (newNameSpace == null) {
            return curNameSpace;
        }

        if (!newNameSpace.equals(curNameSpace)) {
            uriBuilder.append(getModuleNameFromNameSpace(context, newNameSpace));
            uriBuilder.append(COLON);
        }
        uriBuilder.append(key.schemaId().name());
        uriBuilder.append(SLASH);

        return newNameSpace;
    }

    public static String trimAtLast(String valueString, String...
            removalString) {
        StringBuilder stringBuilder = new StringBuilder(valueString);
        String midString;
        int index;
        for (String remove : removalString) {
            midString = stringBuilder.toString();
            index = midString.lastIndexOf(remove);
            if (index != -1) {
                stringBuilder.deleteCharAt(index);
            }
        }
        return stringBuilder.toString();
    }

    public static String getModuleNameFromNameSpace(YangSerializerContext c,
                                                    String ns) {

        YangSchemaNode schemaNode = ((DefaultYangModelRegistry) c.getContext())
                .getForNameSpace(ns, false);
        if (schemaNode != null) {
            return schemaNode.getName();
        }
        return null;
    }

    public static URI rmLastPathSegment(URI uri) {
        if (uri == null) {
            return null;
        }

        UriBuilder builder = UriBuilder.fromUri(uri);
        String newPath = rmLastPathSegmentStr(uri.getRawPath());
        builder.replacePath(newPath);

        return builder.build();
    }

    public static String rmLastPathSegmentStr(String rawPath) {
        if (rawPath == null) {
            return null;
        }
        int pos = rawPath.lastIndexOf(SLASH);
        if (pos <= 0) {
            return null;
        }

        return rawPath.substring(0, pos);
    }

    public static ResourceId parentOf(ResourceId path) {
        try {
            return path.copyBuilder().removeLastKey().build();
        } catch (CloneNotSupportedException e) {
            log.error("ERROR: parentOf: Could not copy {}", path, e);
            throw new IllegalArgumentException("Could not copy " + path, e);
        }
    }

    public static boolean isRootRid(ResourceId path) {
        if (path == null) {
            // null is a valid presentation of root resource ID
            return true;
        }

        if ((path.nodeKeys().size() == 1) && (path.nodeKeys().get(0).schemaId().name().equals("/"))) {
            return true;
        }

        return false;
    }

    public static boolean isRidLeafListEntry(ResourceId rId) {
        if (rId == null) {
            return false;
        }

        List<NodeKey> nodeKeyList = rId.nodeKeys();
        if (CollectionUtils.isEmpty(nodeKeyList)) {
            return false;
        }

        NodeKey lastNodeKey = nodeKeyList.get(nodeKeyList.size() - 1);
        if (lastNodeKey instanceof LeafListKey) {
            return true;
        }

        return false;
    }

    public static String getKeyStrFromLeafListKey(LeafListKey key) {
        return key.asString();
    }

    public static String getNodeNameFromNodeKey(NodeKey key) {
        return key.schemaId().name();
    }

    public static String getNameSpaceFromNodeKey(NodeKey key) {
        return key.schemaId().namespace();
    }

    public static String getRawUriPath(URI uri) {
        if (uri == null) {
            return null;
        }

        String path = uri.getRawPath();
        if (path.equals("/restconf/data")) {
            return null;
        }

        return path.replaceAll("^/restconf/data/", "")
                .replaceAll("^/restconf/configdata/", "")
                .replaceAll("^/restconf/operations/", "");
    }

    public static ResourceId getResourceIdInRange(ResourceId rsId, int fromIdx, int toIdx) {
        checkArgument(rsId.nodeKeys().size() >= toIdx,
                      "%s path must be deeper than base prefix %d", rsId, toIdx);

        // FIXME waiting for Yang tools 2.2.0-b4 or later
        // return ResourceId.builder().append(child.nodeKeys().subList(fromIdx, toIdx+1).build();

        ResourceId.Builder builder = ResourceId.builder();
        for (NodeKey nodeKey : rsId.nodeKeys().subList(fromIdx, toIdx + 1)) {
            if (nodeKey instanceof ListKey) {
                ListKey listKey = (ListKey) nodeKey;
                builder.addBranchPointSchema(nodeKey.schemaId().name(),
                                             nodeKey.schemaId().namespace());
                for (KeyLeaf keyLeaf : listKey.keyLeafs()) {
                    builder.addKeyLeaf(keyLeaf.leafSchema().name(),
                                       keyLeaf.leafSchema().namespace(),
                                       keyLeaf.leafValAsString());
                }
            } else if (nodeKey instanceof LeafListKey) {
                LeafListKey llKey = (LeafListKey) nodeKey;
                builder.addLeafListBranchPoint(llKey.schemaId().name(),
                                               llKey.schemaId().namespace(),
                                               llKey.value());

            } else {
                builder.addBranchPointSchema(nodeKey.schemaId().name(),
                                             nodeKey.schemaId().namespace());
            }
        }
        return builder.build();
    }

    public static List<String> getResourceIdTokens(ResourceId rId) {
        if (rId == null) {
            return null;
        }
        List<NodeKey> nodeKeys = rId.nodeKeys();
        if (CollectionUtils.isEmpty(nodeKeys)) {
            return null;
        }
        List<String> res = Lists.newArrayList();
        for (NodeKey nodeKey : nodeKeys) {
            res.add(nodeKey.schemaId().name());
        }

        return res;
    }

    public static boolean nodeKeysMatchPrefix(ResourceId rid, List<String> nodeKeyPrefix) {
        if (rid == null || CollectionUtils.isEmpty(rid.nodeKeys())) {
            return false;
        }
        return nodeKeysMatchPrefix(rid.nodeKeys(), nodeKeyPrefix);
    }

    public static boolean nodeKeysMatchPrefix(List<NodeKey> nodeKeys, List<String> nodeKeyPrefix) {
        if (nodeKeyPrefix == null || nodeKeyPrefix.isEmpty()) {
            return true;   // no prefix, so match is true
        }
        if (nodeKeys == null || nodeKeys.isEmpty() || nodeKeys.size() < nodeKeyPrefix.size()) {
            return false;
        }

        for (int i = 0; i < nodeKeyPrefix.size(); i++) {
            if (!nodeKeys.get(i).schemaId().name().equals(nodeKeyPrefix.get(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean nodeKeysMatch(List<NodeKey> nodeKeys, List<String> nodeKeyNames) {
        int size = nodeKeyNames.size();
        if (nodeKeys == null || nodeKeys.isEmpty() || nodeKeys.size() != size) {
            return false;
        }

        for (int i = 0; i < size; i++) {
            if (!nodeKeys.get(i).schemaId().name().equals(nodeKeyNames.get(i))) {
                return false;
            }
        }
        return true;
    }

    private static boolean listKeySchemaMatch(ListKey listKeyA, ListKey listKeyB) {
        List<KeyLeaf> keyLeavesA = listKeyA.keyLeafs();
        List<KeyLeaf> keyLeavesB = listKeyB.keyLeafs();
        if (keyLeavesA.size() != keyLeavesB.size()) {
            return false;
        }

        for (int i = 0; i < keyLeavesA.size(); i++) {
            KeyLeaf keyLeafA = keyLeavesA.get(i);
            KeyLeaf keyLeafB = keyLeavesB.get(i);
            if (!keyLeafA.leafSchema().name().equals(keyLeafB.leafSchema().name())) {
                //should we compare keyLeaf.leafSchema().namespace()?
                return false;
            }
        }
        return true;
    }

    public static String getPrintableForm(ResourceId rid) {
        if (rid == null || CollectionUtils.isEmpty(rid.nodeKeys())) {
            return "null";
        }

        StringBuilder builder = new StringBuilder();
        List<NodeKey> nodeKeys = rid.nodeKeys();
        for (NodeKey nodeKey : nodeKeys) {
            if (nodeKey instanceof ListKey) {
                builder.append("/").append(nodeKey.schemaId().name());
                builder.append("=");
                List<KeyLeaf> keyLeafList = ((ListKey) nodeKey).keyLeafs();
                int i = 0;
                for (KeyLeaf keyLeaf : keyLeafList) {
                    if (i > 0) {
                        builder.append(",");
                    }
                    builder.append(keyLeaf.leafValue());
                    builder.append("(").append(keyLeaf.leafSchema().name()).append(")");
                    i++;
                }
            } else if (nodeKey instanceof LeafListKey) {
                builder.append("/")
                        .append(((LeafListKey) nodeKey).value())
                        .append("(").append(nodeKey.schemaId().name()).append(")");

            } else if (nodeKey instanceof NodeKey) {
                if (!nodeKey.schemaId().name().equals("/")) {
                    builder.append("/").append(nodeKey.schemaId().name());
                }
            } else {
                builder.append("ERROR: ").append(nodeKey.toString());
            }
        }
        return builder.toString();
    }

    public static boolean ridSchemaMatchPrefix(ResourceId ridA, ResourceId prefixRid) {
        if (ridA == null || prefixRid == null) {
            return false;
        }

        List<NodeKey> nodeKeysA = ridA.nodeKeys();
        List<NodeKey> nodeKeysB = prefixRid.nodeKeys();

        if (nodeKeysA.size() < nodeKeysB.size()) {
            return false;
        }

        for (int i = 0; i < nodeKeysB.size(); i++) {
            NodeKey nodeKeyA = nodeKeysA.get(i);
            NodeKey nodeKeyB = nodeKeysB.get(i);

            if (nodeKeyA instanceof ListKey) {
                if (!(nodeKeyB instanceof ListKey)) {
                    return false;
                }
                if (!listKeySchemaMatch((ListKey) nodeKeyA, (ListKey) nodeKeyB)) {
                    return false;
                }

            } else if (nodeKeyA instanceof LeafListKey) {
                if (!(nodeKeyB instanceof LeafListKey)) {
                    return false;
                }
                LeafListKey llKeyA = (LeafListKey) nodeKeyA;
                LeafListKey llKeyB = (LeafListKey) nodeKeyB;

                if (!llKeyA.schemaId().name().equals(llKeyB.schemaId().name())) {
                    //should we also compare nodeKey.schemaId().namespace()?
                    return false;
                }
            } else {
                if (nodeKeyB instanceof ListKey || nodeKeyB instanceof LeafListKey) {
                    return false;
                }
                if (!nodeKeyA.schemaId().name().equals(nodeKeyB.schemaId().name())) {
                    //should we also compare nodeKey.schemaId().namespace()?
                    return false;
                }
            }
        }
        return true;
    }

    public static String convertRidToString(ResourceId rid) {
        return getPrintableForm(rid);
    }

    public static boolean ridSchemaMatch(ResourceId ridA, ResourceId ridB) {
        if (ridA == null || ridB == null) {
            return false;
        }

        List<NodeKey> nodeKeysA = ridA.nodeKeys();
        List<NodeKey> nodeKeysB = ridB.nodeKeys();

        if (nodeKeysA.size() != nodeKeysB.size()) {
            return false;
        }

        for (int i = 0; i < nodeKeysA.size(); i++) {
            NodeKey nodeKeyA = nodeKeysA.get(i);
            NodeKey nodeKeyB = nodeKeysB.get(i);

            if (nodeKeyA instanceof ListKey) {
                if (!(nodeKeyB instanceof ListKey)) {
                    return false;
                }
                if (!listKeySchemaMatch((ListKey) nodeKeyA, (ListKey) nodeKeyB)) {
                    return false;
                }

            } else if (nodeKeyA instanceof LeafListKey) {
                if (!(nodeKeyB instanceof LeafListKey)) {
                    return false;
                }
                LeafListKey llKeyA = (LeafListKey) nodeKeyA;
                LeafListKey llKeyB = (LeafListKey) nodeKeyB;

                if (!llKeyA.schemaId().name().equals(llKeyB.schemaId().name())) {
                    //should we also compare nodeKey.schemaId().namespace()?
                    return false;
                }
            } else {
                if (nodeKeyB instanceof ListKey || nodeKeyB instanceof LeafListKey) {
                    return false;
                }
                if (!nodeKeyA.schemaId().name().equals(nodeKeyB.schemaId().name())) {
                    //should we also compare nodeKey.schemaId().namespace()?
                    return false;
                }
            }
        }
        return true;
    }

    public static ResourceId getGenericRidForNetworkLink() {
        ResourceId rid = ResourceId.builder()
                .addBranchPointSchema("/", null)
                .addBranchPointSchema("networks", "generic-ns")
                .addBranchPointSchema("network", "generic-ns")
                .addKeyLeaf("network-id", "generic-ns", "generic-network-id")
                .addBranchPointSchema("link", "generic-ns")
                .addKeyLeaf("link-id", "generic-ns", "generic-link-id")
                .build();
        return rid;
    }

    public static ResourceId getGenericRidForNetworkNode() {
        ResourceId rid = ResourceId.builder()
                .addBranchPointSchema("/", null)
                .addBranchPointSchema("networks", "generic-ns")
                .addBranchPointSchema("network", "generic-ns")
                .addKeyLeaf("network-id", "generic-ns", "generic-network-id")
                .addBranchPointSchema("node", "generic-ns")
                .addKeyLeaf("node-id", "generic-ns", "generic-node-id")
                .build();
        return rid;
    }

    public static ResourceId getGenericRidForNetworkNodeTp() {
        ResourceId rid = ResourceId.builder()
                .addBranchPointSchema("/", null)
                .addBranchPointSchema("networks", "generic-ns")
                .addBranchPointSchema("network", "generic-ns")
                .addKeyLeaf("network-id", "generic-ns", "generic-network-id")
                .addBranchPointSchema("node", "generic-ns")
                .addKeyLeaf("node-id", "generic-ns", "generic-node-id")
                .addBranchPointSchema("termination-point", "generic-ns")
                .addKeyLeaf("tp-id", "generic-ns", "generic-tp-id")
                .build();
        return rid;
    }

    public static ResourceId getGenericRidForNetworkNodeTtp() {
        ResourceId rid = ResourceId.builder()
                .addBranchPointSchema("/", null)
                .addBranchPointSchema("networks", "generic-ns")
                .addBranchPointSchema("network", "generic-ns")
                .addKeyLeaf("network-id", "generic-ns", "generic-network-id")
                .addBranchPointSchema("node", "generic-ns")
                .addKeyLeaf("node-id", "generic-ns", "generic-node-id")
                .addBranchPointSchema("te", "generic-ns")
                .addBranchPointSchema("tunnel-termination-point", "generic-ns")
                .addKeyLeaf("tunnel-tp-id", "generic-ns", "generic-ttp-id")
                .build();
        return rid;
    }

    public static ResourceId getGenericRidForJuniperConfigGroups() {
        ResourceId rid = ResourceId.builder()
                .addBranchPointSchema("/", null)
                .addBranchPointSchema("configuration", "generic-ns")
                .addBranchPointSchema("groups", "generic-ns")
                .addKeyLeaf("name", "generic-ns", "generic-name")
                .build();
        return rid;
    }

    public static ResourceId getGenericRidForEthPmStateParam() {
        ResourceId rid = ResourceId.builder()
                .addBranchPointSchema("/", null)
                .addBranchPointSchema("performance-monitoring", "generic-ns")
                .addBranchPointSchema("service-pm", "generic-ns")
                .addKeyLeaf("service-name", "generic-ns", "generic-list-key")
                .addBranchPointSchema("service-pm-state", "generic-ns")
                .addBranchPointSchema("performance-data", "generic-ns")
                .addKeyLeaf("parameter-name", "generic-ns", "generic-list-key")
                .addBranchPointSchema("parameter-value", "generic-ns")
                .addKeyLeaf("index", "generic-ns", "generic-list-key")
                .build();
        return rid;
    }

    public static ResourceId getGenericRidForTeTunnel() {
        ResourceId rid = ResourceId.builder()
                .addBranchPointSchema("/", null)
                .addBranchPointSchema("te", "generic-ns")
                .addBranchPointSchema("tunnels", "generic-ns")
                .addBranchPointSchema("tunnel", "generic-ns")
                .addKeyLeaf("name", "generic-ns", "generic-tunnel-name")
                .build();
        return rid;
    }

    public static ResourceId getGenericRidForEthtSvcInstances() {
        ResourceId rid = ResourceId.builder()
                .addBranchPointSchema("/", null)
                .addBranchPointSchema("etht-svc", "generic-ns")
                .addBranchPointSchema("etht-svc-instances", "generic-ns")
                .addKeyLeaf("etht-svc-name", "generic-ns", "generic-key-id")
                .build();
        return rid;
    }

    public static ResourceId convertToGenericRid(ResourceId rid) {
        if (rid == null) {
            return null;
        }

        ResourceId genericRid = null;

        genericRid = getGenericRidForEthPmStateParam();
        if (ridSchemaMatch(rid, genericRid)) {
            return genericRid;
        }

        genericRid = getGenericRidForNetworkLink();
        if (ridSchemaMatch(rid, genericRid)) {
            return genericRid;
        }

        genericRid = getGenericRidForTeTunnel();
        if (ridSchemaMatch(rid, genericRid)) {
            return genericRid;
        }

        genericRid = getGenericRidForNetworkNode();
        if (ridSchemaMatch(rid, genericRid)) {
            return genericRid;
        }

        genericRid = getGenericRidForEthtSvcInstances();
        if (ridSchemaMatch(rid, genericRid)) {
            return genericRid;
        }

        genericRid = getGenericRidForNetworkNodeTp();
        if (ridSchemaMatch(rid, genericRid)) {
            return genericRid;
        }

        genericRid = getGenericRidForNetworkNodeTtp();
        if (ridSchemaMatch(rid, genericRid)) {
            return genericRid;
        }

        genericRid = getGenericRidForJuniperConfigGroups();
        if (ridSchemaMatch(rid, genericRid)) {
            return genericRid;
        }
        //TODO: HENRY: add other generic RIDs.

        return null;
    }

    public static String convertRid2Uri(ResourceId rid) {
        if (rid == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        List<NodeKey> nodeKeys = rid.nodeKeys();

        for (int i = 0; i < nodeKeys.size(); i++) {
            NodeKey nodeKey = nodeKeys.get(i);
            String nodeName = nodeKey.schemaId().name();

            if (nodeKey instanceof ListKey) {
                sb.append("/");
                sb.append(nodeName);
                sb.append("=");
                sb.append(listKeyValues2UriString((ListKey) nodeKey));
            } else if (nodeKey instanceof LeafListKey) {
                sb.append("/");
                sb.append(nodeName);
                sb.append("=");
                sb.append(leafListKey2UriString((LeafListKey) nodeKey));
            } else {
                if (!nodeName.equals("/")) {
                    sb.append("/");
                    sb.append(nodeName);
                    //should we also compare nodeKey.schemaId().namespace()?
                }
            }
        }

        return sb.toString();
    }

    private static String leafListKey2UriString(LeafListKey leafListKey) {
        String decodedValue = leafListKey.asString();
        try {
            return URLEncoder.encode(decodedValue, "utf-8");
        } catch (UnsupportedEncodingException e) {
            log.error("ERROR: leafListKey2UriString: {}", e.getMessage());
        }

        return "";
    }

    private static String listKeyValues2UriString(ListKey listKey) {
        if (listKey == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        List<KeyLeaf> keyLeaves = listKey.keyLeafs();

        for (int i = 0; i < keyLeaves.size(); i++) {
            sb.append(",");
            KeyLeaf keyLeaf = keyLeaves.get(i);
            String rawValue = keyLeaf.leafValue().toString();
            try {
                sb.append(URLEncoder.encode(rawValue, "utf-8"));
            } catch (UnsupportedEncodingException e) {
                log.error("ERROR: listKey2UriString: {}", e.getMessage());
            }
        }

        sb.deleteCharAt(sb.lastIndexOf(","));
        return sb.toString();
    }
}
