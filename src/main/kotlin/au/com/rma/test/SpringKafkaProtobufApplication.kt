package au.com.rma.test

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.kafka.annotation.EnableKafka

@EnableKafka
@SpringBootApplication
class SpringKafkaProtobufApplication

fun main(args: Array<String>) {
	runApplication<SpringKafkaProtobufApplication>(*args)
}
