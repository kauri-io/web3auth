/**
 * 
 */
package net.consensys.web3auth.module.adapter.springsecurity.deserialization;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import net.consensys.web3auth.module.adapter.springsecurity.AuthenticationToken;
import net.consensys.web3auth.module.adapter.springsecurity.authentication.IdentifiedAuthenticationToken;

/**
 * @author Gregoire Jeanmart <gregoire.jeanmart@consensys.net>
 *
 */
public class AuthenticationTokenSerializer extends JsonDeserializer<AuthenticationToken> {

    @Override
    public AuthenticationToken deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException {

        ObjectCodec oc = jsonParser.getCodec();
        JsonNode node = oc.readTree(jsonParser);
        
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        Iterator<JsonNode> elements = node.get("authorities").elements();
        while (elements.hasNext()) {
            JsonNode next = elements.next();
            JsonNode authority = next.get("authority");
            
            authorities.add(new SimpleGrantedAuthority(authority.asText()));
        }

        return new IdentifiedAuthenticationToken(
                node.get("name").asText(),
                node.get("token").asText(),
                node.get("remoteAddress").asText(),
                authorities);
    }

}
