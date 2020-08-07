## Spring-Kafka-Protobuf
* [About](#about)
* [Technologies](#technologies)
* [Prerequisites](#prerequisites)
* [Quick Start](#quick-start)
* [Application Logic](#application-logic)
* [TODO](#todo)

## About
This project provides a simple demo of how to send and receive messages using Kafka.
	
## Technologies
* Spring Boot
* Apache Kafka
* Google Protocol Buffers
* Jetbrains Kotlin
	
## Prerequisites
You need to install the following applications before you can trail the application:
* Docker
* Docker-Compose
* Gnu Make
 
## Quick Start
Before the application can run it requires a running instance of Kafka

```bash
$ make start
```
This will start Zookeeper + a Kafka Broker

Once Kafka has started you can run the application:

```bash
$ ./gradlew bootRun
```

## Application Logic
The application starts up and starts listening to two topics:
* customer
* customer.DLT

The `customer` listener simulates some processing by waiting 500ms. 
Every message with modulo 5 == 0 will cause an exception to be thrown.
Messages are retried once with a delay of 3000ms then moved onto the `customer.DLT` queue

The `customer.DLQ` listener simply logs out the message at ERROR level

Once the application has started the `startupListener` creates 10 dummy messages which are send to the `customer` topic

### Message Format
Messages are sent using ProtoBuf in binary format.

## TODO
* Add a controller for sending customer messages from a REST api
* Use Dozer (or somthing similar) to translate REST beans to Protobuf beans
* Create a reactive version using reactor-kafka: https://projectreactor.io/docs/kafka/release/reference/
