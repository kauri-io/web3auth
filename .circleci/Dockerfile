FROM nimmis/ubuntu:16.04
MAINTAINER Gregoire Jeanmart <gregoire.jeanmart@consensys.net>
LABEL version="0.0.1"

# disable interactive functions
ENV DEBIAN_FRONTEND noninteractive

USER root

# Install java
ENV JAVA_VERSION_MAJOR=8 \
    JAVA_VERSION_MINOR=151 \
    JAVA_HOME=/usr/lib/jvm/default-jvm \
    PATH=${PATH}:/usr/lib/jvm/default-jvm/bin/

RUN add-apt-repository ppa:openjdk-r/ppa -y && \

    # update data from repositories
    apt-get update && \

    # upgrade OS
    apt-get -y dist-upgrade && \

    # Make info file about this build
    printf "Build of nimmis/java:openjdk-8-jdk, date: %s\n"  `date -u +"%Y-%m-%dT%H:%M:%SZ"` > /etc/BUILDS/java && \

    # install application
    apt-get install -y --no-install-recommends openjdk-8-jdk && \

    # fix default setting
    ln -s java-8-openjdk-amd64  /usr/lib/jvm/default-jvm && \

    # remove apt cache from image
	apt-get clean all

# Install solc

RUN add-apt-repository ppa:ethereum/ethereum && \

    # update data from repositories
    apt-get update --allow-unauthenticated && \

    # install application
    apt-get install -y solc && \

    # remove apt cache from image
	apt-get clean all


# Install Maven

ARG MAVEN_VERSION=3.5.4
ARG USER_HOME_DIR="/root"
ARG SHA=ce50b1c91364cb77efe3776f756a6d92b76d9038b0a0782f7d53acf1e997a14d
ARG BASE_URL=https://apache.osuosl.org/maven/maven-3/${MAVEN_VERSION}/binaries

RUN mkdir -p /usr/share/maven /usr/share/maven/ref \
  && curl -fsSL -o /tmp/apache-maven.tar.gz ${BASE_URL}/apache-maven-${MAVEN_VERSION}-bin.tar.gz \
  && echo "${SHA}  /tmp/apache-maven.tar.gz" | sha256sum -c - \
  && tar -xzf /tmp/apache-maven.tar.gz -C /usr/share/maven --strip-components=1 \
  && rm -f /tmp/apache-maven.tar.gz \
  && ln -s /usr/share/maven/bin/mvn /usr/bin/mvn

ENV MAVEN_HOME /usr/share/maven
ENV MAVEN_CONFIG "$USER_HOME_DIR/.m2"
