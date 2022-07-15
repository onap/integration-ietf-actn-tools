/*
 *   ============LICENSE_START=======================================================
 *   Actn Interface Tools
 *   ================================================================================
 *   Copyright (C) 2022 Huawei Canada Limited.
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

package org.onap.integration.actninterfacetools.actnclient.api;

/**
 * Base class for OSS internal message
 */
public class OssMessage {
    protected int xid;

    public OssMessage() {

    }

    public int getXid() {
        return xid;
    }

    public OssMessage setXid(int xid) {
        this.xid = xid;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof OssMessage)) {
            return false;
        }
        OssMessage other = (OssMessage) obj;

        if (xid != other.xid) {
            return false;
        }
        return true;
    }
}