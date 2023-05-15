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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class WebClientUtil {

    protected static final Logger LOGGER = LoggerFactory.getLogger(WebClientUtil.class);

    private WebClientUtil() {
    }

    public static String resetContentRange(String range) {
        String rangeResult = "";
        if (range != null) {
            final Pattern pattern = Pattern.compile("\\d+\\-\\d+/\\S+");
            final Matcher matcher = pattern.matcher(range);
            if (matcher.find()) {
                final String[] result = matcher.group(0).split("/");
                if (result.length >= 2) {
                    final String[] ranges = result[0].split("\\D");
                    // 如果返回截止长度大于等于总长度-1，则认为结束
                    if (isContentEnd(ranges[1], result[1])) {
                        return "";
                    }
                    rangeResult = "bytes=" + (Integer.parseInt(ranges[1]) + 1) + "-"
                            + (result[1].equals("*") ? "" : result[1]);
                }
            }
        }
        return rangeResult;
    }

    private static boolean isContentEnd(String end, String total) {
        return total.matches("[0-9]*") && Integer.parseInt(end) >= Integer.parseInt(total) - 1;
    }

}
