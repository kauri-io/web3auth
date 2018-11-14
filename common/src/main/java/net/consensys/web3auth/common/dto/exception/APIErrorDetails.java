/**
 * 
 */
package net.consensys.web3auth.common.dto.exception;

import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class APIErrorDetails {
    private Date timestamp;
    private String message;
    private String details;
    
    public APIErrorDetails(String message, String details) {
        this.timestamp = new Date();
        this.message = message;
        this.details = details;
    }
    
    
    
}