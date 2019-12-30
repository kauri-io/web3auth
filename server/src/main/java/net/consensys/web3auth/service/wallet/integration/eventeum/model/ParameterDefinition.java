package net.consensys.web3auth.service.wallet.integration.eventeum.model;

import lombok.Data;
import net.consensys.eventeum.dto.event.filter.ParameterType;
import net.consensys.web3auth.service.wallet.integration.eventeum.model.ParameterDefinition;

@Data
public class ParameterDefinition implements Comparable<net.consensys.eventeum.dto.event.filter.ParameterDefinition> {

    private Integer position;

    private ParameterType type;

    @Override
    public int compareTo(net.consensys.eventeum.dto.event.filter.ParameterDefinition o) {
        return this.position.compareTo(o.getPosition());
    }

    public ParameterDefinition(Integer position, ParameterType address) {
        this.position = position;
        this.type = address;
    }
}
