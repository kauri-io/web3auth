package net.consensys.web3auth.common.dto;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Organisation implements Serializable {

    private static final long serialVersionUID = -2161172913558756817L;
     
    private String name;
    private String role;
    
}
