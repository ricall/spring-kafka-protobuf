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
package au.com.rma.test.listener

import au.com.rma.test.customer.Customer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kafka.receiver.ReceiverOffset
import reactor.kafka.receiver.ReceiverPartition
import reactor.kafka.receiver.ReceiverRecord
import java.time.Duration.ofMillis
import java.time.Duration.ofSeconds

@Component
class CustomerListener: TopicListener<Long, Customer> {
  private val logger: Logger = LoggerFactory.getLogger(javaClass)

  override fun onAssignPartitions(partitions: Collection<ReceiverPartition>) {
    logger.info("onAssignPartitions {}", partitions)
  }

  override fun onRevokePartitions(partitions: Collection<ReceiverPartition>) {
    logger.info("onRevokePartitions {}", partitions)
  }

  override fun onRecord(record: ReceiverRecord<Long, Customer>): Mono<ReceiverOffset> {
    logger.info("onRecord({}) partition: {}, offset: {}, timestamp: {}", record.key(), record.partition(), record.offset(), record.timestamp())
    record.headers().forEach { h -> logger.info("{} = {}", h.key(), String(h.value())) }
    logger.info("\n{}", record.value())

    if (record.key().toInt() % 5 == 0) {
      return Mono.error(IllegalStateException("Record rejected..."))
    }

    return Mono.just(record.receiverOffset())
        .delayElement(ofMillis(200))
  }
}