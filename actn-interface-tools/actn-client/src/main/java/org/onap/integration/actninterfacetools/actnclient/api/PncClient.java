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

import java.util.concurrent.Future;

public interface PncClient {

    <T> Future<YangValue<T>> asyncGets(String key, Transcoder<T> tc);

    Future<YangValue<Object>> asyncGets(String key);

    <T> Future<Boolean> put(String key, T o, Transcoder<T> tc);

    Future<Boolean> put(String key, Object o);
}
