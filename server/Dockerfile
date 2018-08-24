FROM andrejsavikin/ubuntu-openjdk-8-jre
ADD target/web3auth-exec.jar app.jar


####################################################################################################################
# Ubuntu Packages
RUN apt-get update && apt-get install -y curl jq && apt-get clean

EXPOSE 8080
CMD ["java", "-jar", "app.jar", "--spring.config.location=${WEB3AUTH_CONF}"]