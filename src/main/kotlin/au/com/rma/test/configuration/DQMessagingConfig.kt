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

import au.com.rma.dq.kafka.conversion.ScrubRequestSerializer
import au.com.rma.dq.kafka.conversion.ScrubResponseDeserializer
import au.com.rma.dq.model.ScrubRequest
import au.com.rma.dq.model.ScrubResponse
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerConfig.*
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import reactor.core.publisher.EmitterProcessor
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxSink
import reactor.core.scheduler.Schedulers
import reactor.kafka.receiver.KafkaReceiver
import reactor.kafka.receiver.ReceiverOptions
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderOptions
import java.util.function.Function

@Component
class DQMessagingConfig(val environment: KafkaEnvironment) {
  val logger: Logger = LoggerFactory.getLogger(javaClass)

  @Bean
  fun dqSender() = KafkaSender.create(senderOptions())

  private fun senderOptions(): SenderOptions<String, ScrubRequest> = SenderOptions.create(mapOf(
      Pair(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, environment.bootstrapServers),
      Pair(ProducerConfig.CLIENT_ID_CONFIG, "${environment.clientId}-dq"),
      Pair(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 20),
      Pair(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java.name),
      Pair(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ScrubRequestSerializer::class.java.name),
      Pair(ProducerConfig.RETRIES_CONFIG, environment.producerRetries)
  ))

  @Bean
  fun dqReceiver(): Flux<ConsumerRecord<String, ScrubResponse>> {
    return KafkaReceiver.create(receiverOptions().subscription(listOf(environment.dqResponseTopic)))
        .receiveAutoAck()
        .onErrorContinue { e, o -> logger.error("error: {}", o, e) }
        .concatMap(Function.identity())
        .publish()
        .autoConnect()
  }

  private fun receiverOptions(): ReceiverOptions<String, ScrubResponse> = ReceiverOptions.create(mapOf(
      Pair(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, environment.bootstrapServers),
      Pair(ConsumerConfig.CLIENT_ID_CONFIG, "${environment.clientId}-dq"),
      Pair(GROUP_ID_CONFIG, environment.groupId),
      Pair(MAX_POLL_RECORDS_CONFIG, 10),
      Pair(KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.name),
      Pair(VALUE_DESERIALIZER_CLASS_CONFIG, ScrubResponseDeserializer::class.java.name),
      Pair(ENABLE_AUTO_COMMIT_CONFIG, "true"),
      Pair(AUTO_OFFSET_RESET_CONFIG, "earliest")
  ))
}