FROM andrejsavikin/ubuntu-openjdk-8-jre
ADD target/web3auth-exec.jar app.jar


####################################################################################################################
# Ubuntu Packages
RUN apt-get update && apt-get install -y curl jq && apt-get clean

####################################################################################################################
# APM
ENV ELASTIC_APM_VERSION "1.8.0"
ENV ELASTIC_APM_SERVER "http://localhost:8200"
RUN wget -O apm-agent.jar https://search.maven.org/remotecontent?filepath=co/elastic/apm/elastic-apm-agent/$ELASTIC_APM_VERSION/elastic-apm-agent-$ELASTIC_APM_VERSION.jar


EXPOSE 8080
CMD ["java", "-jar", "app.jar", "--spring.config.additional-location=${WEB3AUTH_CONF}"]