## Spring-Kafka-Protobuf
* [About](#about)
* [Technologies](#technologies)
* [Prerequisites](#prerequisites)
* [Quick Start](#quick-start)
* [Application Logic](#application-logic)
* [Tasks](#tasks)

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

### Testing the application
You can test the application using the following command

```bash
$ ./gradlew clean check
```

### Running the application
Once Kafka has started you can run the application:

```bash
$ ./gradlew bootRun
```

When the application starts it will send a couple of messages to verify everything is working.

### Sending Messages manually
Using your favourite REST client like Postman or Insomnia:

#### Send an Individual
`POST http://localhost:8181/api/customer`
```json
{
	"id": 1234,
	"individual": {
		"firstName": "JOHN",
		"middleName": null,
		"lastName": "SMITH",
		"gender": "MALE"
	},
	"contacts": [
		{
			"type": "EMAIL",
			"text": "test@test.com"
		},
		{
			"type": "MOBILE_PHONE",
			"text": "0400 123 456"
		}
	],
	"addresses": [
		{
			"type": "PHYSICAL",
			"line1": "123 SMITH STREET",
			"line2": null,
			"line3": null,
			"line4": null,
			"suburb": "BRISBANE CBD",
			"city": "BRISBANE",
			"postcode": "4000",
			"state": "QLD",
			"countryCode": "AU"
		}
	]
}
```

#### Send an organisation
`POST http://localhost:8181/api/customer`
```json
{
	"id": 1239,
	"organisation": {
		"name": "Acme Widgets",
		"contact": {
			"firstName": "JOHN",
			"middleName": null,
			"lastName": "SMITH",
			"gender": "MALE"
		}
	},
	"contacts": [
		{
			"type": "EMAIL",
			"text": "test@test.com"
		},
		{
			"type": "MOBILE_PHONE",
			"text": "0400 123 456"
		}
	],
	"addresses": [
		{
			"type": "PHYSICAL",
			"line1": "123 SMITH STREET",
			"line2": null,
			"line3": null,
			"line4": null,
			"suburb": "BRISBANE CBD",
			"city": "BRISBANE",
			"postcode": "4000",
			"state": "QLD",
			"countryCode": "AU"
		}
	]
}
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

# Tasks
* Created mapping framework for converting between protobuf <-> model classes
* Added a controller for sending customer messages from a REST api
* Create a reactive version using reactor-kafka: https://projectreactor.io/docs/kafka/release/reference/

## NOTES
* The Java protobuf classes use a builder pattern for setting values, this combined with "oneof" make using 
  automated bean mapping difficult. (Tried Dozer + MapStruct)
* Protobuf + `NULL` values: https://itnext.io/protobuf-and-null-support-1908a15311b6