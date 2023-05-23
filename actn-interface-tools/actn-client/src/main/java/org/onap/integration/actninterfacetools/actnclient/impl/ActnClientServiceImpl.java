package org.onap.integration.actninterfacetools.actnclient.impl;

import com.google.auto.service.AutoService;
import org.onap.integration.actninterfacetools.globalapi.ActnClientService;
import org.onap.integration.actninterfacetools.globalapi.ActnDataConverter;
import org.onap.integration.actninterfacetools.globalapi.PncClient;
import org.onap.integration.actninterfacetools.protocol.restconf.PncInstance;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@AutoService(ActnClientService.class)
public class ActnClientServiceImpl implements ActnClientService {
//    private static volatile ActnClientServiceImpl actnClientService_instance = null;
    private ActnDataConverter actnDataConverter;

    private Map<PncInstance, PncClientInstance> pncClientMap = new ConcurrentHashMap();


    public ActnClientServiceImpl() {
    }
    public ActnClientServiceImpl(ActnDataConverter actnDataConverter) {
        this.actnDataConverter = actnDataConverter;
    }


    @Override
    public void registerDataConverter(ActnDataConverter converter) {
        this.actnDataConverter = converter;
    }

    @Override
    public PncClient getPncClient(PncInstance pncInstance) {
        if(pncClientMap.get(pncInstance)!=null){
            return pncClientMap.get(pncInstance);
        }else{
            PncClientInstance pncClientInstance = new PncClientInstance(pncInstance, actnDataConverter);
            this.pncClientMap.put(pncInstance, pncClientInstance);
            return pncClientInstance;
        }

    }
}
