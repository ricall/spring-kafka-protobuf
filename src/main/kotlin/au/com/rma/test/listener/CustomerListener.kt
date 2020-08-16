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

import au.com.rma.dq.model.PhoneNumber
import au.com.rma.dq.model.ScrubRequest
import au.com.rma.dq.model.ScrubResponse
import au.com.rma.test.customer.Address
import au.com.rma.test.customer.Contact
import au.com.rma.test.customer.ContactType
import au.com.rma.test.customer.Customer
import au.com.rma.test.service.DataQualityService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.Disposable
import reactor.core.publisher.Mono
import reactor.kafka.receiver.ReceiverOffset
import reactor.kafka.receiver.ReceiverPartition
import reactor.kafka.receiver.ReceiverRecord
import java.util.*
import java.util.stream.Collectors

@Component
class CustomerListener(val dqService: DataQualityService): TopicListener<Long, Customer> {
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
    return scrub(record.value())
        .doOnNext { customer -> logger.info("Final Customer (presave)\n${customer}") }
        .map { _ -> record.receiverOffset() }
  }

  private fun scrub(customer: Customer): Mono<Customer> {
    val country = customer.addressList.stream()
        .map(Address::getCountryCode)
        .findFirst().orElse(null)
    val request = ScrubRequest.newBuilder()
        .addAllPhoneNumbers(customer.contactList.stream()
            .map { c -> contactToPhoneNumber(c, country) }
            .filter(Objects::nonNull)
            .collect(Collectors.toList()))
        .build()
    logger.info("Sending Scrub Request: $request")

    return dqService.queryDataQuality(request)
        .map { r -> handleResponse(customer, r) }
  }

  private fun contactToPhoneNumber(contact: Contact, country: String) : PhoneNumber? {
    val phone: String? = when(contact.type) {
      ContactType.HOME_PHONE -> contact.text
      ContactType.MOBILE_PHONE -> contact.text
      else -> return null
    }
    logger.info("Phone Number: {}", phone)
    return PhoneNumber.newBuilder()
        .setReference(contact.type.toString())
        .setCountryCode(country)
        .setPhoneNumber(phone)
        .build()
  }

  private fun handleResponse(customer: Customer, response: ScrubResponse): Customer {
    logger.info("Processing scrub response: $response")

    val newContacts = customer.contactList.stream()
        .map { contact ->
          val scrubbed = response.phoneNumbersList.stream()
              .filter { c -> c.reference == contact.type.toString() }
              .findFirst()

          when {
            scrubbed.isPresent -> Contact.newBuilder()
                .setType(contact.type)
                .setText(scrubbed.get().phoneNumber)
                .build()
            else -> contact
          }
        }
        .collect(Collectors.toList())
    return customer.toBuilder()
        .clearContact()
        .addAllContact(newContacts)
        .build();
  }

}