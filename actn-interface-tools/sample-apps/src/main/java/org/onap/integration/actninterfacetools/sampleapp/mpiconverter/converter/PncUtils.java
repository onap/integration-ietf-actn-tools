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
package org.onap.integration.actninterfacetools.sampleapp.mpiconverter.converter;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PncUtils {

    private static final Logger log = LoggerFactory.getLogger(PncUtils.class);
    //startIdx is 1-indexing;
    public static void setBitsZero(byte[] bytes, int startIdx, int length){
        int currentByteIdx = (startIdx -1) / 8;

        if (currentByteIdx < bytes.length){
            byte currByte = bytes[currentByteIdx];
            int offset = startIdx ;
            while (offset - startIdx < length){
                int currBitIdx = (offset - 1) % 8;
                currByte = (byte) (currByte & ~(1 << (7 - currBitIdx)));
                bytes[currentByteIdx] = currByte;
                if (currBitIdx == 7){
                    currentByteIdx++;
                    if (currentByteIdx < bytes.length ){
                        currByte = bytes[currentByteIdx];
                    }
                }
                offset++;
            }
            return;
        }
        throw new IllegalArgumentException("PncUtils: setBits: Illegal Argument");
    }

    public static void setBitsOne(byte[] bytes, int startIdx, int length){
        int currentByteIdx = (startIdx -1) / 8;

        if (currentByteIdx < bytes.length){
            byte currByte = bytes[currentByteIdx];
            int offset = startIdx ;
            while (offset - startIdx < length){
                int currBitIdx = (offset - 1) % 8;
                currByte = (byte) (currByte | (1 << (7 - currBitIdx)));
                bytes[currentByteIdx] = currByte;
                if (currBitIdx == 7){
                    currentByteIdx++;
                    if (currentByteIdx < bytes.length ){
                        currByte = bytes[currentByteIdx];
                    }
                }
                offset++;
            }
            return;
        }
        throw new IllegalArgumentException("PncUtils: setBits: Illegal Argument");
    }

    public static boolean isBitsOne(byte[] bytes, int startIdx, int length){
        int currentByteIdx = (startIdx -1) / 8;

        if (currentByteIdx < bytes.length){
            byte currByte = bytes[currentByteIdx];
            int offset = startIdx ;
            while (offset - startIdx < length){
                int currBitIdx = (offset - 1) % 8;
                if ( ((0x01 << (7 - currBitIdx)) & currByte) == 0){
                    return false;
                };
                if (currBitIdx == 7){
                    bytes[currentByteIdx] = currByte;
                    currentByteIdx++;
                    if (currentByteIdx < bytes.length ){
                        currByte = bytes[currentByteIdx];
                    }
                }
                offset++;
            }
            return true;
        }
        throw new IllegalArgumentException("PncUtils: setBits: Illegal Argument");
    }
}
