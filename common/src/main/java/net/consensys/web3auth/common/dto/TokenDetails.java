package net.consensys.web3auth.common.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TokenDetails implements Serializable {

    private static final long serialVersionUID = 7167350123061149069L;

    private String address;
}
