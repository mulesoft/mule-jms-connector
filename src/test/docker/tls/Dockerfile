FROM openjdk:8-jre

ENV ACTIVEMQ_VERSION 5.15.16
ENV ACTIVEMQ apache-activemq-$ACTIVEMQ_VERSION
ENV ACTIVEMQ_TCP=61616 ACTIVEMQ_AMQP=5672 ACTIVEMQ_STOMP=61613 ACTIVEMQ_MQTT=1883 ACTIVEMQ_WS=61614 ACTIVEMQ_UI=8161
ENV SHA512_VAL=5aaa73047b3f80f1a79984fe1a4b84645bd1d31ae2de3b41b774616d8290e5ff9647a0e38fd4a753e472c56efba42025be67561ac9f652442e7b226aa62236af
ENV ACTIVEMQ_HOME /opt/activemq
ENV ACTIVEMQ_SSL_OPTS=-Djavax.net.ssl.keyStore=/opt/activemq/certs/ActiveMq/broker.ks -Djavax.net.ssl.keyStorePassword=racing

RUN curl "https://archive.apache.org/dist/activemq/$ACTIVEMQ_VERSION/$ACTIVEMQ-bin.tar.gz" -o $ACTIVEMQ-bin.tar.gz

# Validate checksum
RUN if [ "$SHA512_VAL" != "$(sha512sum $ACTIVEMQ-bin.tar.gz | awk '{print($1)}')" ];\
    then \
        echo "sha512 values doesn't match! exiting."  && \
        exit 1; \
    fi;


RUN tar xzf $ACTIVEMQ-bin.tar.gz -C  /opt && \
    ln -s /opt/$ACTIVEMQ $ACTIVEMQ_HOME && \
    useradd -r -M -d $ACTIVEMQ_HOME activemq && \
    chown -R activemq:activemq /opt/$ACTIVEMQ && \
    chown -h activemq:activemq $ACTIVEMQ_HOME

COPY activemq.xml /opt/activemq/conf/activemq.xml
COPY broker-keystore.ks /opt/activemq/certs/ActiveMq/broker.ks
COPY client-truststore.ks /opt/activemq/certs/ActiveMq/broker.ts


USER activemq

WORKDIR $ACTIVEMQ_HOME
EXPOSE $ACTIVEMQ_TCP $ACTIVEMQ_SSL $ACTIVEMQ_AMQP $ACTIVEMQ_STOMP $ACTIVEMQ_MQTT $ACTIVEMQ_WS $ACTIVEMQ_UI

CMD ["/bin/sh", "-c", "bin/activemq console"]
