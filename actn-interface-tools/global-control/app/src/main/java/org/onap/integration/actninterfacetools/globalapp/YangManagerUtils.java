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
package org.onap.integration.actninterfacetools.globalapp;

import org.onosproject.yang.compiler.tool.YangCompilerManager;
import org.onosproject.yang.compiler.tool.YangNodeInfo;
import org.onosproject.yang.model.YangModel;
import org.onosproject.yang.runtime.helperutils.YangApacheUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class YangManagerUtils {
    private static final String SLASH;
    private static final String HYPHEN = "-";
    private static final String PERIOD = ".";
    private static final String YANG_RESOURCES = "yang/resources";
    private static final String SYSTEM;
    private static final String MAVEN = "mvn:";
    private static final String JAR = ".jar";
    private static final String USER_DIRECTORY = "user.dir";
    private static final Logger log;

    private YangManagerUtils() {
    }
    public static YangModel getYangModel(Class<?> modClass) throws URISyntaxException {
        String jarPath = new File(YangManagerUtils.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
        String yangModelPath = jarPath.replaceAll("/global-control/app/target/classes", "/actn-model/target/actn-model-1.0-SNAPSHOT");
        List<YangNodeInfo> nodeInfo = new ArrayList();
//        System.out.println(yangModelPath);
        String metaPath = yangModelPath + SLASH + "yang/resources" + SLASH;
        YangModel model = processJarParsingOperations(yangModelPath);
        if (model != null) {
            YangCompilerManager.setNodeInfo(model, nodeInfo);
            if (!nodeInfo.isEmpty()) {
                return YangCompilerManager.processYangModel(metaPath, nodeInfo, model.getYangModelId(), false);
            }
        }

        return null;
    }
    private static String getJarPathFromBundleLocation(String mvnLocationPath, String currentDirectory) {
        StringBuilder builder = new StringBuilder();
        builder.append(currentDirectory).append(SYSTEM);
        StringBuilder ver = new StringBuilder();
        if (mvnLocationPath.contains("mvn:")) {
            String[] strArray = mvnLocationPath.replaceFirst("\\$Bundle-.*$", "").split("mvn:");
            if (strArray[1].contains(File.separator)) {
                String[] split = strArray[1].split(File.separator);
                if (split[0].contains(".")) {
                    String[] groupId = split[0].split(Pattern.quote("."));
                    String[] var7 = groupId;
                    int var8 = groupId.length;

                    for(int var9 = 0; var9 < var8; ++var9) {
                        String s = var7[var9];
                        builder.append(s).append(SLASH);
                    }

                    for(int i = 1; i < split.length; ++i) {
                        builder.append(split[i]).append(SLASH);
                        ver.append(split[i]).append("-");
                    }

                    builder.append(ver);
                    builder.deleteCharAt(builder.length() - 1);
                    return builder.toString();
                }
            }
        }

        return null;
    }

    private static YangModel processJarParsingOperations(String path) {
        String jar = path + ".jar";

        try {
            File file = new File(jar);
            if (file.exists()) {
                return YangCompilerManager.parseJarFile(path + ".jar", path);
            }
        } catch (IOException var3) {
            log.error(" failed to parse the jar file in path {} : {} ", path, var3.getMessage());
        }

        return null;
    }

    static {
        SLASH = File.separator;
        SYSTEM = SLASH + "system" + SLASH;
        log = LoggerFactory.getLogger(YangApacheUtils.class);
    }
}
