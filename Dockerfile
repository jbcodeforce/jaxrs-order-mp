### Stage 1 - Java Build
FROM ibmjava:8-sdk
LABEL maintainer="IBM Cloud Architecture Solution Engineering at IBM Cloud"

RUN apt-get update && \
 apt-get install -y maven

 # attach volumes
VOLUME /volume/gitrepo

# create working directory
RUN mkdir -p /local/gitrepo
WORKDIR /local/gitrepo
COPY src/ /local/gitrepo/src
COPY pom.xml /local/gitrepo/pom.xml

RUN mvn install -DskipTests=true

### Stage 2 - Docker Build
FROM open-liberty:microProfile3-java11
LABEL maintainer="IBM Cloud Architecture Solution Engineering at IBM Cloud"

USER 1001
COPY --from=0 /local/gitrepo/target/jaxrs-order-mp.war /config/dropins

# Upgrade to production license if URL to JAR provided
ARG LICENSE_JAR_URL
RUN \
  if [ $LICENSE_JAR_URL ]; then \
    wget $LICENSE_JAR_URL -O /tmp/license.jar \
    && java -jar /tmp/license.jar -acceptLicense /opt/ibm \
    && rm /tmp/license.jar; \
  fi

