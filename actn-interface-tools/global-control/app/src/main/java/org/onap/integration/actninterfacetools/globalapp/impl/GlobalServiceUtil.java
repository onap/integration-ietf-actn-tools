package org.onap.integration.actninterfacetools.globalapp.impl;

import com.google.auto.service.AutoService;
import org.onap.integration.actninterfacetools.actnclient.impl.ActnClientServiceImpl;
import org.onap.integration.actninterfacetools.globalapi.ActnClientService;
import org.onap.integration.actninterfacetools.globalapi.ActnDataConverter;
import org.onap.integration.actninterfacetools.globalapi.GlobalService;
import org.onap.integration.actninterfacetools.globalapp.AbstractYangModelRegistrator;
import org.onap.integration.actninterfacetools.globalapp.ActnModelRegistrator;
import org.onap.integration.actninterfacetools.globalapp.YangClassLoaderRegistry;
import org.onap.integration.actninterfacetools.yangutils.CodecConverter;
import org.onap.integration.actninterfacetools.yangutils.YangToolsUtil;
import org.onosproject.yang.model.*;
import org.onosproject.yang.runtime.*;
import org.onosproject.yang.runtime.impl.*;
import org.onosproject.yang.serializers.json.JsonSerializer;
import org.onosproject.yang.serializers.xml.XmlSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.google.common.base.Preconditions.checkNotNull;


public class GlobalServiceUtil implements YangModelRegistry,
        YangSerializerRegistry, YangRuntimeService, ModelConverter,
        SchemaContextProvider, YangClassLoaderRegistry {
    private static volatile GlobalServiceUtil globalServiceInstance = null;
    private static Map<ActnDataConverter, ActnClientService> dataConverter2ActnClientServiceMap = new ConcurrentHashMap();
    private static volatile boolean initialized = false;
    private static final String APP_ID = "org.onosproject.yang";
    private static final Logger log = LoggerFactory.getLogger(GlobalServiceUtil.class);
    private static volatile DefaultYangModelRegistry modelRegistry = null;
    private static volatile DefaultYangSerializerRegistry serializerRegistry = null;
    private static volatile DefaultYangRuntimeHandler runtimeService = null;
    private static volatile DefaultModelConverter modelConverter = null;
    private static volatile DefaultSchemaContextProvider schemaContextProvider = null;

    private static Map<String, ClassLoader> classLoaders = null;
    private GlobalServiceUtil(){
    }
    public String getServiceName(){
        return "GlobalServiceUtil";
    }
    public static GlobalServiceUtil getNewGlobalService(){
        if(globalServiceInstance != null){
            return globalServiceInstance;
        }
        synchronized (ActnClientServiceImpl.class) {
            if(globalServiceInstance == null){
                globalServiceInstance = new GlobalServiceUtil();

            }
            return globalServiceInstance;
        }
    }


    public synchronized void initialize() throws URISyntaxException {
        if(!initialized){
            if(serializerRegistry == null){
                serializerRegistry = new DefaultYangSerializerRegistry();
            }
            if(modelRegistry == null){
                modelRegistry = new DefaultYangModelRegistry();
            }
            if(runtimeService == null){
                runtimeService = new DefaultYangRuntimeHandler(serializerRegistry, modelRegistry);
            }
            if(schemaContextProvider == null){
                schemaContextProvider = new DefaultSchemaContextProvider(modelRegistry);
            }
            serializerRegistry.registerSerializer(new JsonSerializer());
            serializerRegistry.registerSerializer(new XmlSerializer());
            if(modelConverter == null){
                modelConverter = new DefaultModelConverter(modelRegistry);
            }
            if(classLoaders == null){
                classLoaders = new ConcurrentHashMap<>();
            }
            AbstractYangModelRegistrator abstractYangModelRegistrator = new ActnModelRegistrator();
            abstractYangModelRegistrator.activate();
            CodecConverter.active();
            YangToolsUtil.active();
            log.info("Initialization Finished");
            initialized = true;
        }
    }


    public synchronized ActnClientService getActnClientService(ActnDataConverter actnDataConverter) throws URISyntaxException {
        if(!initialized){
            initialize();
        }
        if(dataConverter2ActnClientServiceMap.get(actnDataConverter)!=null){
            return dataConverter2ActnClientServiceMap.get(actnDataConverter);
        }else{
            ActnClientService actnClientService = new ActnClientServiceImpl(actnDataConverter);
            dataConverter2ActnClientServiceMap.put(actnDataConverter, actnClientService);
            return actnClientService;
        }
    }

    public DefaultYangModelRegistry getModelRegistry(){
        return modelRegistry;
    }
    public DefaultYangSerializerRegistry getSerializerRegistry(){
        return serializerRegistry;
    }
    public DefaultYangRuntimeHandler getRuntimeService(){
        return runtimeService;
    }
    public DefaultModelConverter getModelConverter(){
        return modelConverter;
    }
    public DefaultSchemaContextProvider getSchemaContextProvider(){
        return schemaContextProvider;
    }

    public Map<String, ClassLoader> getClassLoaders(){
        return classLoaders;
    }


    @Override
    public ClassLoader getClassLoader(String modelId) {
        return classLoaders.get(modelId);
    }

    @Override
    public void registerClassLoader(String modelId, ClassLoader classLoader) {
        classLoaders.put(modelId, classLoader);
    }

    @Override
    public void unregisterClassLoader(String modelId) {
        classLoaders.remove(modelId);
    }

    @Override
    public ModelObjectData createModel(ResourceData resourceData) {
        return modelConverter.createModel(resourceData);
    }

    @Override
    public ResourceData createDataNode(ModelObjectData modelObjectData) {
        return modelConverter.createDataNode(modelObjectData);
    }

    @Override
    public SchemaContext getSchemaContext(ResourceId resourceId) {
        checkNotNull(resourceId, " resource id can't be null.");
        NodeKey key = resourceId.nodeKeys().get(0);
        if (resourceId.nodeKeys().size() == 1 &&
                "/".equals(key.schemaId().name())) {
            return modelRegistry;
        }
        log.info("To be implemented.");
        return null;
    }

    @Override
    public RpcContext getRpcContext(ResourceId resourceId) {
        return schemaContextProvider.getRpcContext(resourceId);
    }

    @Override
    public void registerModel(ModelRegistrationParam modelRegistrationParam) throws IllegalArgumentException {
        modelRegistry.registerModel(modelRegistrationParam);
    }


    @Override
    public void registerAnydataSchema(ModelObjectId parentModId, ModelObjectId childModId) throws IllegalArgumentException {
        modelRegistry.registerAnydataSchema(parentModId, childModId);
    }

    @Override
    public void unregisterAnydataSchema(Class parentClass, Class childClass) throws IllegalArgumentException {
        modelRegistry.unregisterAnydataSchema(parentClass, childClass);
    }

    @Override
    public void unregisterModel(ModelRegistrationParam modelRegistrationParam) {
        modelRegistry.unregisterModel(modelRegistrationParam);
    }

    @Override
    public Set<YangModel> getModels() {
        return modelRegistry.getModels();
    }

    @Override
    public YangModel getModel(String s) {
        return modelRegistry.getModel(s);
    }

    @Override
    public YangModule getModule(YangModuleId yangModuleId) {
        return modelRegistry.getModule(yangModuleId);
    }

    @Override
    public CompositeData decode(CompositeStream cs, RuntimeContext rc) {
        return runtimeService.decode(cs, rc);
    }

    @Override
    public CompositeStream encode(CompositeData cd, RuntimeContext rc) {
        return runtimeService.encode(cd, rc);
    }

    @Override
    public void registerSerializer(YangSerializer ys) {
        serializerRegistry.registerSerializer(ys);
    }

    @Override
    public void unregisterSerializer(YangSerializer ys) {
        serializerRegistry.unregisterSerializer(ys);
    }

    @Override
    public Set<YangSerializer> getSerializers() {
        return serializerRegistry.getSerializers();
    }
}
