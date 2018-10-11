package net.consensys.web3auth.module.authority.service.cache.kafka.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ContractEventMessage.class, name = ContractEventMessage.TYPE)
    })
@JsonInclude(JsonInclude.Include.NON_NULL)
public interface Message<T> {
    String getId();

    String getType();

    T getDetails();
}
