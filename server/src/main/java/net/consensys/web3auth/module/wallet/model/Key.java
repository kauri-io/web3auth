package net.consensys.web3auth.module.wallet.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
public class Key {

    public enum KeyRole { 
        ACTION(0), MANAGEMENT(0);
        public int code;
        KeyRole(int code) { this.code = code; }
        
    }
    
    private String key;
    private KeyRole role;
    
}
