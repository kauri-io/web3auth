/**
 * 
 */
package net.consensys.web3auth.common.dto.exception;

import java.io.IOException;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.ResponseErrorHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Gregoire Jeanmart <gregoire.jeanmart@consensys.net>
 *
 */
public class APIErrorHandler implements ResponseErrorHandler {

    private ResponseErrorHandler errorHandler = new DefaultResponseErrorHandler();
    private ObjectMapper objectMapper = new ObjectMapper();
    
    public void handleError(ClientHttpResponse response) throws IOException {

        APIErrorDetails error = objectMapper.readValue(response.getBody(), APIErrorDetails.class);
        
        switch (response.getStatusCode().series()) {
            case CLIENT_ERROR:
                throw new HTTPClientException(error.getMessage());
            case SERVER_ERROR:
                throw new HTTPServerException(error.getMessage());
            default:
                throw new HTTPServerException(error.getMessage());
        }
    }

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
       return errorHandler.hasError(response);
    }
  }