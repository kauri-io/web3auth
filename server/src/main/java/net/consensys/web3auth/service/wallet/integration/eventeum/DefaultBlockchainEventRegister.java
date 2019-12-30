package net.consensys.web3auth.service.wallet.integration.eventeum;

import java.util.Arrays;
import java.util.Collections;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import net.consensys.eventeum.dto.event.filter.ContractEventFilter;
import net.consensys.eventeum.dto.event.filter.ParameterType;
import net.consensys.eventeum.service.SubscriptionService;
import net.consensys.web3auth.service.wallet.integration.eventeum.model.ContractEventSpecification;
import net.consensys.web3auth.service.wallet.integration.eventeum.model.ParameterDefinition;

@Component
public class DefaultBlockchainEventRegister implements BlockchainEventRegister {
    
    private static final ContractEventSpecification ADDED_OWNER_EVENT = new ContractEventSpecification(
            "AddedOwner", 
            Collections.emptyList(), 
            Arrays.asList(new ParameterDefinition(0, ParameterType.build(ParameterType.ADDRESS))));
    
    private static final ContractEventSpecification REMOVED_OWNER_EVENT = new ContractEventSpecification(
            "RemovedOwner", 
            Collections.emptyList(), 
            Arrays.asList(new ParameterDefinition(0, ParameterType.build(ParameterType.ADDRESS))));
    
    private static final ContractEventSpecification PROXY_CREATION_EVENT = new ContractEventSpecification(
            "ProxyCreation", 
            Collections.emptyList(), 
            Arrays.asList(new ParameterDefinition(0, ParameterType.build(ParameterType.ADDRESS))));

    private final SubscriptionService subscriptionService;
    private final ModelMapper mapper;

    public DefaultBlockchainEventRegister(SubscriptionService subscriptionService) {
        this.mapper = new ModelMapper();
        this.subscriptionService = subscriptionService;
    }

    @Override
    public void registerAddedOwnerEvent(String address) {
        subscriptionService.registerContractEventFilter(buildContractEventFilter(address, ADDED_OWNER_EVENT), false);
    }

    @Override
    public void registerRemovedOwnerEvent(String address) {
        subscriptionService.registerContractEventFilter(buildContractEventFilter(address, REMOVED_OWNER_EVENT), false);
    }

    @Override
    public void registerProxyCreationEvent(String address) {
        subscriptionService.registerContractEventFilter(buildContractEventFilter(address, PROXY_CREATION_EVENT), false);
    }
    
    private ContractEventFilter buildContractEventFilter(String address, ContractEventSpecification spec) {
        final ContractEventFilter filter = new ContractEventFilter();
        filter.setContractAddress(address);
        filter.setId(spec.getEventName() + "-" + address);
        filter.setEventSpecification(mapper.map(spec, net.consensys.eventeum.dto.event.filter.ContractEventSpecification.class));

        return filter;
    }
    
}
