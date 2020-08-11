/*
 * Copyright (c) 2020 Richard Allwood
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package au.com.rma.test.configuration

import au.com.rma.test.customer.Customer
import au.com.rma.test.kafka.CustomerDeserializer
import au.com.rma.test.kafka.CustomerSerializer
import au.com.rma.test.listener.CustomerListener
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerConfig.*
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerConfig.*
import org.apache.kafka.common.serialization.LongDeserializer
import org.apache.kafka.common.serialization.LongSerializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.Disposable
import reactor.core.publisher.Signal
import reactor.kafka.receiver.KafkaReceiver
import reactor.kafka.receiver.ReceiverOffset
import reactor.kafka.receiver.ReceiverOptions
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderOptions

@Configuration
class MessagingConfiguration(
    val environment: KafkaEnvironment
) {
  val logger: Logger = LoggerFactory.getLogger(javaClass)

  @Bean
  fun customerSender() = KafkaSender.create(senderOptions())

  private fun senderOptions(): SenderOptions<Long, Customer> = SenderOptions.create(mapOf(
      Pair(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, environment.bootstrapServers),
      Pair(ProducerConfig.CLIENT_ID_CONFIG, environment.clientId),
      Pair(MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 20),
      Pair(KEY_SERIALIZER_CLASS_CONFIG, LongSerializer::class.java.name),
      Pair(VALUE_SERIALIZER_CLASS_CONFIG, CustomerSerializer::class.java.name),
      Pair(RETRIES_CONFIG, environment.producerRetries)
  ))

  @Bean
  fun customerReceiver(customerListener: CustomerListener): Disposable {
    val receiver = KafkaReceiver.create(receiverOptions()
        .subscription(listOf(environment.customerTopic))
        .addAssignListener(customerListener::onAssignPartitions)
        .addRevokeListener(customerListener::onRevokePartitions))

    // We need smarter logic where each record partition:offset is collected before onRecord()
    // Then errors are handled by putting the message on a retry queue
    // Finally we acknowledge the partition:offset
    //
    // NOTE: If the onRecord() Mono responses are out of order we need to retain the offsets until we get all
    //   records in the correct order and acknowledge the highest number
    return receiver.receive()
        .concatMap(customerListener::onRecord)
        .doOnEach { r: Signal<ReceiverOffset> -> r.get()?.acknowledge() }
        .onErrorContinue { e, o -> logger.error("error: {}", o, e) }
        .subscribe()
  }

  private fun receiverOptions(): ReceiverOptions<Long, Customer> = ReceiverOptions.create(mapOf(
      Pair(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, environment.bootstrapServers),
      Pair(ConsumerConfig.CLIENT_ID_CONFIG, environment.clientId),
      Pair(GROUP_ID_CONFIG, environment.groupId),
      Pair(MAX_POLL_RECORDS_CONFIG, 10),
      Pair(KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer::class.java.name),
      Pair(VALUE_DESERIALIZER_CLASS_CONFIG, CustomerDeserializer::class.java.name),
      Pair(ENABLE_AUTO_COMMIT_CONFIG, "false"),
      Pair(AUTO_OFFSET_RESET_CONFIG, "earliest")
  ))
}