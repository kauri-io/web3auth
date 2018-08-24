package net.consensys.web3auth.common.dto;

import java.io.Serializable;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AccountDetails implements Serializable {

    private static final long serialVersionUID = 7167350123061149069L;

    private String address;
    private Set<Organisation> organisations;
}
