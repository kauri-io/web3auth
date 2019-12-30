package net.consensys.web3auth.service.wallet.integration.eventeum.model;

import java.util.List;
import lombok.Data;
import net.consensys.web3auth.service.wallet.integration.eventeum.model.ParameterDefinition;

@Data
public class ContractEventSpecification {

    private String eventName;

    private List<ParameterDefinition> indexedParameterDefinitions;

    private List<ParameterDefinition> nonIndexedParameterDefinitions;

    public ContractEventSpecification(String eventName, List<ParameterDefinition> indexedParameterDefinitions,
            List<ParameterDefinition> nonIndexedParameterDefinitions) {
        this.eventName = eventName;
        this.indexedParameterDefinitions = indexedParameterDefinitions;
        this.nonIndexedParameterDefinitions = nonIndexedParameterDefinitions;
    }
}
