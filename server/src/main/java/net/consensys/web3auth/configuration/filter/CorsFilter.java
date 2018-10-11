package net.consensys.web3auth.configuration.filter;

import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_MAX_AGE;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD;
import static org.springframework.http.HttpHeaders.ORIGIN;
import static org.springframework.http.HttpMethod.OPTIONS;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.annotation.PostConstruct;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.web3j.utils.Strings;

import lombok.extern.slf4j.Slf4j;

/**
 * Filter to enable Cross-Origin requests
 * @author Gregoire Jeanmart <gregoire.jeanmart@consensys.net>
 *
 */
@Component
@Slf4j
public class CorsFilter extends OncePerRequestFilter {

    public static final String WILDCARD = "*";

    @Value("#{'${web3auth.cors.origins}'.split(',')}")
    public List<String> allowedOrigin;
    @Value("#{'${web3auth.cors.methods}'.split(',')}")
    public List<String> allowedMethods;
    @Value("#{'${web3auth.cors.headers}'.split(',')}")
    public List<String> allowedHeaders;
    @Value("${web3auth.cors.credentials}") 
    public boolean allowCredentials;
    
    private int maxAge = 3600;
    
    public List<Pattern> allowedOriginPatterns;
    
    public CorsFilter() {}
    
    public CorsFilter(
	    @Value("#{'${cors.origins}'.split(',')}") List<String> allowedOrigin, 
	    @Value("#{'${cors.methods}'.split(',')}") List<String> allowedMethods, 
	    @Value("#{'${cors.headers}'.split(',')}") List<String> allowedHeaders, 
	    @Value("${cors.credentials}") boolean allowCredentials) {
        
	this.allowedOrigin = allowedOrigin;
	this.allowedMethods = allowedMethods;;
	this.allowedHeaders = allowedHeaders;
	this.allowCredentials = allowCredentials;

	this.initialize();
    }
    

    @PostConstruct
    public void initialize() {
	this.allowedOriginPatterns = new ArrayList<>();
	
        if (allowedOrigin != null) {
            for (String allowedUri : allowedOrigin) {
                try {
                    allowedOriginPatterns.add(Pattern.compile(allowedUri));
                    log.debug(String.format("URI '%s' is allowed for a CORS requests.", allowedUri));
                } catch (PatternSyntaxException patternSyntaxException) {
                    log.error("Invalid regular expression pattern in cors. allowed.uris: " + allowedUri, patternSyntaxException);
                }
            }
        }
    }


    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
            final FilterChain filterChain) throws ServletException, IOException {

        if (!isCrossOriginRequest(request)) {
            //if the Origin header is not present.
            //Process as usual
            filterChain.doFilter(request, response);
            return;
        }

        log.debug("CORS Processing request: "+getRequestInfo(request));
        
        handleRequest(request, response, filterChain);
        
       log.debug("CORS processing completed for: "+getRequestInfo(request)+" Status:"+response.getStatus());
    }

    protected boolean handleRequest(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, ServletException {

        boolean isPreflightRequest = OPTIONS.toString().equals(request.getMethod());

        //Validate if this CORS request is allowed for this method
        String method = request.getMethod();
        if (!isPreflightRequest && !isAllowedMethod(method)) {
            log.debug(String.format("Request with invalid method was rejected: %s", method));
            response.sendError(METHOD_NOT_ALLOWED.value(), "Illegal method.");
            return true;
        }


        // Validate the origin so we don't reflect back any potentially dangerous content.
        String origin = request.getHeader(ORIGIN);
        // While origin can be a comma delimited list, we don't allow it for CORS
        URI originURI;
        try {
            originURI = new URI(origin);
        } catch(URISyntaxException e) {
            log.debug(String.format("Request with invalid origin was rejected: %s", origin));
            response.sendError(FORBIDDEN.value(), "Invalid origin");
            return true;
        }

        if (!isAllowedOrigin(origin)) {
            log.debug(String.format("Request with origin: %s was rejected because it didn't match allowed origins", origin));
            response.sendError(FORBIDDEN.value(), "Illegal origin");
            return true;
        }

        if (allowCredentials) {
            //if we allow credentials, send back the actual origin
            response.addHeader(ACCESS_CONTROL_ALLOW_ORIGIN, originURI.toString());
        } else {
            //send back a wildcard, this will prevent credentials
            response.addHeader(ACCESS_CONTROL_ALLOW_ORIGIN, WILDCARD);
        }
        response.addHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS, String.valueOf(allowCredentials));


        if (isPreflightRequest) {
            log.debug(String.format("Request is a pre-flight request"));
            buildCorsPreFlightResponse(request, response);
        } else {
            log.debug(String.format("Request cross origin request has passed validation."));
            filterChain.doFilter(request, response);
}

        return false;
    }


    /**
     * Returns true if the `Origin` header is present and has any value
     * @param request the HTTP servlet request
     * @return true if the `Origin` header is present
     */
    protected boolean isCrossOriginRequest(final HttpServletRequest request) {
        //TODO what about SAME origin requests that actually have the Origin header present?
        //presence of the origin header indicates CORS request
        return StringUtils.hasText(request.getHeader(ORIGIN));
    }

    protected void buildCorsPreFlightResponse(final HttpServletRequest request,
                                              final HttpServletResponse response) throws IOException {
        String accessControlRequestMethod = request.getHeader(ACCESS_CONTROL_REQUEST_METHOD);

        //preflight requires the Access-Control-Request-Method header
        if (null == accessControlRequestMethod) {
            response.sendError(BAD_REQUEST.value(), "Access-Control-Request-Method header is missing");
            return;
        }

        if (!isAllowedMethod(accessControlRequestMethod)) {
            response.sendError(METHOD_NOT_ALLOWED.value(), "Illegal method requested");
            return;
        }

        //add all methods that we allow
        response.addHeader(ACCESS_CONTROL_ALLOW_METHODS, buildCommaDelimitedString(allowedMethods));

        //we require Access-Control-Request-Headers header
        String accessControlRequestHeaders = request.getHeader(ACCESS_CONTROL_REQUEST_HEADERS);
        if (null == accessControlRequestHeaders) {
            response.sendError(BAD_REQUEST.value(),"Missing "+ACCESS_CONTROL_REQUEST_HEADERS+" header.");
            return;
        }
        if (!headersAllowed(accessControlRequestHeaders)) {
            response.sendError(FORBIDDEN.value(), "Illegal header requested");
            return;
        }

        //echo back what the client requested
        response.addHeader(ACCESS_CONTROL_ALLOW_HEADERS, accessControlRequestHeaders);
        //send back our configuration value
        response.addHeader(ACCESS_CONTROL_MAX_AGE, String.valueOf(maxAge));
    }

    protected boolean containsHeader(final String accessControlRequestHeaders, final String header) {
        List<String> headers = splitCommaDelimitedString(accessControlRequestHeaders);
        return containsIgnoreCase(Strings.join(headers, ","), header);
    }

    protected boolean headersAllowed(final String accessControlRequestHeaders) {
        List<String> headers = splitCommaDelimitedString(accessControlRequestHeaders);
        for (String header : headers) {
            if (!containsIgnoreCase(Strings.join(allowedHeaders, ","), header)) {
                return false;
            }
        }
        return true;
    }

    protected boolean isAllowedMethod(final String method) {
        return containsIgnoreCase(String.join(",", allowedMethods), method);
    }

    protected boolean isAllowedOrigin(final String origin) {
        for (Pattern pattern : allowedOriginPatterns) {
            // Making sure that the pattern matches
            if (pattern.matcher(origin).find()) {
                return true;
            }
        }
        log.debug(String.format("The '%s' origin is not allowed to make CORS requests.",origin));
        return false;
    }
     
    
    //----------------REQUEST INFO ----------------------------------------------//
    public String getRequestInfo(HttpServletRequest request) {
        return String.format("URI: %s; Scheme: %s; Host: %s; Port: %s; Origin: %s; Method: %s",
                             request.getRequestURI(),
                             request.getScheme(),
                             request.getServerName(),
                             request.getServerPort(),
                             request.getHeader("Origin"),
                             request.getMethod());
    }

    //----------------UTILS ---------------------------------------------//
    protected String buildCommaDelimitedString(List<String> list) {
        StringBuilder builder = new StringBuilder();
        for (String s : list) {
            if (builder.length()>0) {
                builder.append(", ");
            }
            builder.append(s);
        }
        return builder.toString();
    }

    protected List<String> splitCommaDelimitedString(String s) {
        String[] list = s.replace(" ", "").split(",");
        if (list==null || list.length==0) {
            return Collections.emptyList();
        }
        return Arrays.asList(list);
    }
    
    public static boolean containsIgnoreCase(String str, String searchStr)     {
	if(str == null || searchStr == null) return false;

	final int length = searchStr.length();
	if (length == 0)
	    return true;

	for (int i = str.length() - length; i >= 0; i--) {
	    if (str.regionMatches(true, i, searchStr, 0, length))
		return true;
	}
	return false;
    } 

}