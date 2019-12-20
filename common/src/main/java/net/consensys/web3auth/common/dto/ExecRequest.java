package net.consensys.web3auth.common.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ExecRequest {
    
    @NotNull 
    @Size(min=40, max=42)
    private String to;
    
    @NotNull 
    @Size(min=1)
    private String data;
    
    private String signature;

}
