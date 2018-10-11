package net.consensys.web3auth.module.authority.service.cache.kafka.event;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A textual based EventParameter, represented by a String.
 *
 * @author Craig Williams <craig.williams@consensys.net>
 */
@Data
@NoArgsConstructor
public class StringParameter extends AbstractEventParameter<String> {

    public StringParameter(String type, String value) {
        super(type, value);
    }

    @Override
    public String getValueString() {
        return getValue();
    }
}
