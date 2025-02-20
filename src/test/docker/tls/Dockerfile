FROM openjdk:8-jre

ENV ACTIVEMQ_VERSION 5.15.9
ENV ACTIVEMQ apache-activemq-$ACTIVEMQ_VERSION
ENV FILENAME $ACTIVEMQ-bin.tar.gz
ENV ACTIVEMQ_TCP=61616 ACTIVEMQ_AMQP=5672 ACTIVEMQ_STOMP=61613 ACTIVEMQ_MQTT=1883 ACTIVEMQ_WS=61614 ACTIVEMQ_UI=8161
ENV SHA512_VAL=35cae4258e38e47f9f81e785f547afc457fc331d2177bfc2391277ce24123be1196f10c670b61e30b43b7ab0db0628f3ff33f08660f235b7796d59ba922d444f
ENV ACTIVEMQ_HOME /opt/activemq
ENV ACTIVEMQ_SSL_OPTS=-Djavax.net.ssl.keyStore=/opt/activemq/certs/ActiveMq/broker.ks -Djavax.net.ssl.keyStorePassword=racing

COPY $FILENAME $FILENAME

RUN tar xzf $FILENAME -C  /opt && \
    ln -s /opt/$ACTIVEMQ $ACTIVEMQ_HOME && \
    useradd -r -M -d $ACTIVEMQ_HOME activemq && \
    chown -R activemq:activemq /opt/$ACTIVEMQ && \
    chown -h activemq:activemq $ACTIVEMQ_HOME

COPY activemq.xml /opt/activemq/conf/activemq.xml
COPY broker-keystore.bcfks /opt/activemq/certs/ActiveMq/broker.bcfks
COPY client-truststore.bcfks /opt/activemq/certs/ActiveMq/broker.ts.bcfks


USER activemq

WORKDIR $ACTIVEMQ_HOME
EXPOSE $ACTIVEMQ_TCP $ACTIVEMQ_SSL $ACTIVEMQ_AMQP $ACTIVEMQ_STOMP $ACTIVEMQ_MQTT $ACTIVEMQ_WS $ACTIVEMQ_UI

CMD ["/bin/sh", "-c", "bin/activemq console"]
