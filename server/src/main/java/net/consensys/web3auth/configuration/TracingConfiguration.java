package net.consensys.web3auth.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import co.elastic.apm.opentracing.ElasticApmTracer;
import io.opentracing.Tracer;

@Configuration
public class TracingConfiguration {

    @Bean
    public Tracer elasticApmTracer() {
        return new ElasticApmTracer();
    }
    
}
