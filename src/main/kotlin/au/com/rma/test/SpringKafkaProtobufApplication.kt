package au.com.rma.test

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.kafka.annotation.EnableKafka
import reactor.core.publisher.Hooks

@EnableKafka
@SpringBootApplication
class SpringKafkaProtobufApplication

fun main(args: Array<String>) {
	Hooks.onOperatorDebug()
	runApplication<SpringKafkaProtobufApplication>(*args)
}
