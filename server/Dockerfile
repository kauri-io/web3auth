FROM openjdk:8-jre
ADD target/web3auth-exec.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar", "--spring.config.location=${WEB3AUTH_CONF}"]