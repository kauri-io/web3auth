package net.consensys.web3auth.common.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Organisation {

    private String name;
    private List<String> privileges;
    
}
