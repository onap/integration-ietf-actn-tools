package org.onap.integration.actninterfacetools.globalapi;


import org.onosproject.yang.runtime.impl.*;

import java.net.URISyntaxException;
import java.util.Map;
import java.util.ServiceLoader;

public interface GlobalService {
    String getServiceName();
    void initialize() throws URISyntaxException;
    ActnClientService getActnClientService(ActnDataConverter actnDataConverter) throws URISyntaxException;
    DefaultYangModelRegistry getModelRegistry();
    DefaultYangSerializerRegistry getSerializerRegistry();
    DefaultYangRuntimeHandler getRuntimeService();
    DefaultModelConverter getModelConverter();
    DefaultSchemaContextProvider getSchemaContextProvider();

    Map<String, ClassLoader> getClassLoaders();
    static ActnClientService getActnClientSvc(ActnDataConverter actnDataConverter) throws URISyntaxException {
        ServiceLoader<GlobalService> serviceLoader = ServiceLoader.load(GlobalService.class);
        GlobalService globalService = serviceLoader.iterator().next();
        return globalService.getActnClientService(actnDataConverter);
    }

}
