FROM java:8-jre-alpine

ENV JAVA_APP_JAR ola-0.0.1-SNAPSHOT-fat.jar

EXPOSE 8080

ADD target/$JAVA_APP_JAR /app/
ADD src/openshift /app/
RUN chmod 777 /app/

WORKDIR /app/
ENTRYPOINT ["sh", "-c"]
CMD ["java -Dvertx.zookeeper.hosts=${ZOOKEEPER_SERVICE_HOST} -jar $JAVA_APP_JAR -cluster -cp . -conf config.json"]

