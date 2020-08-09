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
package au.com.rma.test.service

import au.com.rma.test.configuration.KafkaEnvironment
import au.com.rma.test.customer.Customer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.internals.RecordHeader
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderRecord
import reactor.kafka.sender.SenderResult
import java.nio.charset.Charset
import java.util.*

@Service
class CustomerService(
    val customerSender: KafkaSender<Long, Customer>,
    val environment: KafkaEnvironment
) {
  val logger: Logger = LoggerFactory.getLogger(javaClass)

  fun sendCustomerEvent(customer: Customer?): Mono<Customer> {
    if (customer == null) {
      logger.warn("No customer to send")
      return Mono.empty()
    }

    // We need to send a correlation id header
    val correlationId = UUID.randomUUID().toString()
    logger.info("Sending: ${customer.id}($correlationId) to ${environment.customerTopic}")

    val producerRecord = ProducerRecord(
        environment.customerTopic,
        null,
        null,
        customer.id,
        customer,
        listOf(RecordHeader("CORRELATION_ID", correlationId.toByteArray(Charset.defaultCharset())))
    )
    val event = SenderRecord.create(producerRecord, customer)

    return customerSender.send(Mono.just(event))
        .flatMap { result -> handleResult(result) }
        .next()
  }

  fun handleResult(result: SenderResult<Customer>): Mono<Customer> {
    val exception = result.exception()
    if (exception != null) {
      logger.error("Send Failed", exception)
      // customerSender.close()
      return Mono.error(exception)
    }
    return Mono.just(result.correlationMetadata())
  }
}
