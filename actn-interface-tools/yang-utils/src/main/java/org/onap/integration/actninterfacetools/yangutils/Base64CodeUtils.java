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

import java.util.Base64;
/**
 * Created by sdn on 8/2/17.
 */
public class Base64CodeUtils {
    @Deprecated
    public static String EnCode(String str) {
        if (str == null) {
            return null;
        }
        byte[] bytes = str.getBytes();
        String result = null;
        try {
            result = Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    @Deprecated
    public static String decode(String str) {
        if (str == null) {
            return null;
        }
        byte[] bytes = null;
        try {
            bytes = Base64.getDecoder().decode(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String result = String.valueOf(bytes);
        return result;
    }

    @Deprecated
    public static byte[] numsCovertToBytes(long num) {
        String NumString = String.valueOf(num);
        String EncodeString = "";
        for (int i = 0; i < NumString.length(); i++) {
            EncodeString += EnCode(String.valueOf(NumString.charAt(i)));
        }
        return EncodeString.getBytes();
    }

    //convert from TE long to Yang binary
    public static byte[] longToBytes(long x) {
        //TODO: HENRY: need to convert 'x' into a string format, e.g., "MTEwMTE=".
        //should just return unprocessed byte[].
        byte[] src = String.valueOf(x).getBytes();

        return src;
    }

    //convert from Yang binary to TE long
    public static long bytesToLong(byte[] bytes) {
        /*
         * the input bytes are already decoded by YangTools.
         */
        String s = new String(bytes);
        return Long.valueOf(s);
    }
}
