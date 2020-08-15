package au.com.rma.dq

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.kafka.annotation.EnableKafka

@EnableKafka
@SpringBootApplication
@ConfigurationPropertiesScan
class DQApplication

fun main(args: Array<String>) {
	runApplication<DQApplication>(*args)
}
