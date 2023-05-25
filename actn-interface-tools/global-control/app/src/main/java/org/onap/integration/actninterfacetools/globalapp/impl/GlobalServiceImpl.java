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
package org.onap.integration.actninterfacetools.globalapp.impl;

import com.google.auto.service.AutoService;
import org.onap.integration.actninterfacetools.actnclient.impl.ActnClientServiceImpl;
import org.onap.integration.actninterfacetools.globalapi.ActnClientService;
import org.onap.integration.actninterfacetools.globalapi.ActnDataConverter;
import org.onap.integration.actninterfacetools.globalapi.GlobalService;
import org.onap.integration.actninterfacetools.globalapp.YangClassLoaderRegistry;
import org.onosproject.yang.model.ModelConverter;
import org.onosproject.yang.model.ModelObjectData;
import org.onosproject.yang.model.ResourceData;
import org.onosproject.yang.model.SchemaContextProvider;
import org.onosproject.yang.model.SchemaContext;
import org.onosproject.yang.model.ResourceId;
import org.onosproject.yang.model.RpcContext;
import org.onosproject.yang.model.ModelObjectId;
import org.onosproject.yang.model.YangModel;
import org.onosproject.yang.model.YangModule;
import org.onosproject.yang.model.YangModuleId;
import org.onosproject.yang.runtime.ModelRegistrationParam;
import org.onosproject.yang.runtime.YangModelRegistry;
import org.onosproject.yang.runtime.YangRuntimeService;
import org.onosproject.yang.runtime.YangSerializerRegistry;
import org.onosproject.yang.runtime.CompositeData;
import org.onosproject.yang.runtime.CompositeStream;
import org.onosproject.yang.runtime.RuntimeContext;
import org.onosproject.yang.runtime.YangSerializer;
import org.onosproject.yang.runtime.impl.DefaultModelConverter;
import org.onosproject.yang.runtime.impl.DefaultYangModelRegistry;
import org.onosproject.yang.runtime.impl.DefaultYangRuntimeHandler;
import org.onosproject.yang.runtime.impl.DefaultYangSerializerRegistry;
import org.onosproject.yang.runtime.impl.DefaultSchemaContextProvider;

import java.net.URISyntaxException;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
@AutoService(GlobalService.class)
public class GlobalServiceImpl implements GlobalService, YangModelRegistry,
        YangSerializerRegistry, YangRuntimeService, ModelConverter,
        SchemaContextProvider, YangClassLoaderRegistry {
    private static volatile GlobalServiceImpl globalService = null;
      private static GlobalServiceUtil globalServiceUtil= GlobalServiceUtil.getNewGlobalService();

    public GlobalServiceImpl() {
    }
    public static GlobalServiceImpl getNewGlobalService(){
        if(globalService != null){
            return globalService;
        }
        synchronized (ActnClientServiceImpl.class) {
            if(globalService == null){
                globalService = new GlobalServiceImpl();

            }
            return globalService;
        }
    }

    @Override
    public String getServiceName() {
        return "GlobalServiceImpl";
    }

    @Override
    public void initialize() throws URISyntaxException {
        globalServiceUtil.initialize();
    }

    @Override
    public ActnClientService getActnClientService(ActnDataConverter actnDataConverter) throws URISyntaxException {
        return globalServiceUtil.getActnClientService(actnDataConverter);
    }

    public DefaultYangModelRegistry getModelRegistry(){

        return globalServiceUtil.getModelRegistry();
    }
    public DefaultYangSerializerRegistry getSerializerRegistry(){
        return globalServiceUtil.getSerializerRegistry();
    }
    public DefaultYangRuntimeHandler getRuntimeService(){
        return globalServiceUtil.getRuntimeService();
    }
    public DefaultModelConverter getModelConverter(){
        return globalServiceUtil.getModelConverter();
    }
    public DefaultSchemaContextProvider getSchemaContextProvider(){
        return globalServiceUtil.getSchemaContextProvider();
    }

    public Map<String, ClassLoader> getClassLoaders(){
        return globalServiceUtil.getClassLoaders();
    }


    @Override
    public ClassLoader getClassLoader(String modelId) {
        return globalServiceUtil.getClassLoader(modelId);
    }

    @Override
    public void registerClassLoader(String modelId, ClassLoader classLoader) {
        globalServiceUtil.registerClassLoader(modelId, classLoader);
    }

    @Override
    public void unregisterClassLoader(String modelId) {
        globalServiceUtil.unregisterClassLoader(modelId);
    }

    @Override
    public ModelObjectData createModel(ResourceData resourceData) {
        return globalServiceUtil.createModel(resourceData);
    }

    @Override
    public ResourceData createDataNode(ModelObjectData modelObjectData) {
        return globalServiceUtil.createDataNode(modelObjectData);
    }

    @Override
    public SchemaContext getSchemaContext(ResourceId resourceId) {
        return globalServiceUtil.getSchemaContext(resourceId);
    }

    @Override
    public RpcContext getRpcContext(ResourceId resourceId) {
        return globalServiceUtil.getRpcContext(resourceId);
    }

    @Override
    public void registerModel(ModelRegistrationParam modelRegistrationParam) throws IllegalArgumentException {
        globalServiceUtil.registerModel(modelRegistrationParam);
    }


    @Override
    public void registerAnydataSchema(ModelObjectId parentModId, ModelObjectId childModId) throws IllegalArgumentException {
        globalServiceUtil.registerAnydataSchema(parentModId, childModId);
    }

    @Override
    public void unregisterAnydataSchema(Class parentClass, Class childClass) throws IllegalArgumentException {
        globalServiceUtil.unregisterAnydataSchema(parentClass, childClass);
    }

    @Override
    public void unregisterModel(ModelRegistrationParam modelRegistrationParam) {
        globalServiceUtil.unregisterModel(modelRegistrationParam);
    }

    @Override
    public Set<YangModel> getModels() {
        return globalServiceUtil.getModels();
    }

    @Override
    public YangModel getModel(String s) {
        return globalServiceUtil.getModel(s);
    }

    @Override
    public YangModule getModule(YangModuleId yangModuleId) {
        return globalServiceUtil.getModule(yangModuleId);
    }

    @Override
    public CompositeData decode(CompositeStream cs, RuntimeContext rc) {
        return globalServiceUtil.decode(cs, rc);
    }

    @Override
    public CompositeStream encode(CompositeData cd, RuntimeContext rc) {
        return globalServiceUtil.encode(cd, rc);
    }

    @Override
    public void registerSerializer(YangSerializer ys) {
        globalServiceUtil.registerSerializer(ys);
    }

    @Override
    public void unregisterSerializer(YangSerializer ys) {
        globalServiceUtil.unregisterSerializer(ys);
    }

    @Override
    public Set<YangSerializer> getSerializers() {
        return globalServiceUtil.getSerializers();
    }
}
