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
package org.onap.integration.actninterfacetools.sampleapp.mpiconverter;


import org.onap.integration.actninterfacetools.globalapp.impl.GlobalServiceImpl;

import java.util.concurrent.CountDownLatch;

/**
 * In Order to test Multiple OSS Apps using OSS ACTN SDK in the same time
 */
public class OssAppsExcuter {
    GlobalServiceImpl globalService;
    public static void main(String[] args) throws InterruptedException {
        usingCountDownLatch();
    }
    private static void usingCountDownLatch() throws InterruptedException {
    System.out.println("===============================================");
    System.out.println("        >>> Using CountDownLatch <<<<");
    System.out.println("===============================================");

    CountDownLatch latch = new CountDownLatch(1);

    OssWorkThreadA ossWorkThreadA = new OssWorkThreadA("OssWorkThreadA", latch);
    OssWorkThreadB ossWorkThreadB = new OssWorkThreadB("OssWorkThreadB", latch);

    ossWorkThreadA.start();
    ossWorkThreadB.start();

    Thread.sleep(10);//simulation of some actual work

    System.out.println("-----------------------------------------------");
    System.out.println(" Now release the latch:");
    System.out.println("-----------------------------------------------");
    latch.countDown();
}
}
