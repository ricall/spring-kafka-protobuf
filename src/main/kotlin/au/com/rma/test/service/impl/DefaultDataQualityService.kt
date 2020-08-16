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
package au.com.rma.test.service.impl

import au.com.rma.dq.model.ScrubRequest
import au.com.rma.dq.model.ScrubResponse
import au.com.rma.test.configuration.KafkaEnvironment
import au.com.rma.test.service.DataQualityService
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.kafka.receiver.ReceiverRecord
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderRecord
import reactor.kotlin.core.publisher.toMono
import java.time.Duration
import java.util.*

@Component
class DefaultDataQualityService(
    val environment: KafkaEnvironment,
    @Qualifier("dqSender") val sender: KafkaSender<String, ScrubRequest>,
    @Qualifier("dqReceiver") val dqReceiver: Flux<ConsumerRecord<String, ScrubResponse>>
) : DataQualityService {
  private val logger: Logger = LoggerFactory.getLogger(javaClass)
  private val emptyResponse = ScrubResponse.newBuilder().build()

  override fun queryDataQuality(request: ScrubRequest): Mono<ScrubResponse> {
    var correlationKey = UUID.randomUUID().toString()
    val producerRecord = ProducerRecord(environment.dqTopic, correlationKey, request)
    val event = SenderRecord.create(producerRecord, request)

    sender.send(Mono.just(event))
        .subscribe()
    return dqReceiver
        .doOnEach { r -> logger.info("RECEIVED: {}", r) }
        .filter { r ->
          logger.info("Filter: ${r.key()} == ${correlationKey}")
          r.key() == correlationKey
        }
        .map { r -> r.value() }
        .toMono()
        .timeout(Duration.ofMillis(5000))
        .onErrorReturn(emptyResponse)
  }
}