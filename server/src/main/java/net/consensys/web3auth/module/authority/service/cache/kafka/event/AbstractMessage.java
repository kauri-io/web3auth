package net.consensys.web3auth.module.authority.service.cache.kafka.event;

import lombok.Data;

@Data
public abstract class AbstractMessage<T> implements Message<T> {

    private String id;

    private String type;

    private T details;

    public AbstractMessage() {

    }

    public AbstractMessage(String id, String type, T details) {
        this.id = id;
        this.type = type;
        this.details = details;
    }
}