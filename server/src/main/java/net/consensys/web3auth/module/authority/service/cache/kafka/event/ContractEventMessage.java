package net.consensys.web3auth.module.authority.service.cache.kafka.event;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ContractEventMessage extends AbstractMessage<ContractEventDetails> {
    public static final String TYPE = "CONTRACT_EVENT";

    public ContractEventMessage() { }

    public ContractEventMessage(ContractEventDetails contractEvent) {
        super(contractEvent.getId(), TYPE, contractEvent);
    }
}
