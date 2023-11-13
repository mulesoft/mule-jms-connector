#!/bin/bash
RUNTIME_VERSION=4.4.0-20230522
MUNIT_JVM=/Users/jfisicaro/jdk-11.0.2.jdk/Contents/Home/bin/java
mvn clean
mkdir target
mvn verify \
    -DruntimeProduct=MULE_EE \
    -DruntimeVersion=$RUNTIME_VERSION \
    -Dmunit.jvm=$MUNIT_JVM \
    -Dmtf.javaopts="--illegal-access=warn" > ./target/test.log

cat ./target/test.log | grep "WARNING: Illegal reflective access by" > ./target/illegal-access.log
