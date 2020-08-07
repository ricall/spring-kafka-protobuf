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
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.listener.adapter.ConsumerRecordMetadata
import org.springframework.stereotype.Component
import java.lang.IllegalStateException

@Component
class CustomerListener {
  val logger: Logger = LoggerFactory.getLogger(javaClass)

  @KafkaListener(id = "customer-listener", topics = ["customers"])
  fun onCustomer(meta: ConsumerRecordMetadata, customer: Customer) {
    logger.info("meta offset: ${meta.offset()}")
    logger.info("meta partiton: ${meta.partition()}")
    logger.info("meta topic: ${meta.topic()}")
    logger.info("customer:\n${customer}")

    // Simulate some processing
    Thread.sleep(500)

    if (customer.id % 5 == 0L) {
      throw IllegalStateException("Cannot process customer ${customer.id}")
    }
  }

  @KafkaListener(id = "customer-dlt-listener", topics = ["customers.DLT"])
  fun onCustomerDLQ(meta: ConsumerRecordMetadata, customer: Customer) {
    logger.error("meta offset: ${meta.offset()}")
    logger.error("meta partiton: ${meta.partition()}")
    logger.error("meta topic: ${meta.topic()}")
    logger.error("customer:\n${customer}")
  }
}