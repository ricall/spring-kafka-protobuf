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
package au.com.rma.test

import au.com.rma.test.customer.*
import au.com.rma.test.service.impl.DefaultCustomerService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux

@Component
class StartupListener(val service: DefaultCustomerService): CommandLineRunner {
  val logger: Logger = LoggerFactory.getLogger(javaClass)

  override fun run(vararg args: String?) {
    logger.info("-----------> Started <-----------")
    Flux.range(1, 2)
        .flatMap { service.sendCustomerEvent(createTestCustomer(it)) }
        .subscribe()
  }

  private fun createTestCustomer(index: Int): Customer {
    val customer = Customer.newBuilder()
        .setId(1_000_000L + index)
        .addAddress(Address.newBuilder()
            .setType(AddressType.PHYSICAL)
            .setLine1("4/123 MAIN STREET")
            .setSuburb("BRISBANE CBD")
            .setCity("BRISBANE")
            .setCountryCode("AU"))
        .addContact(Contact.newBuilder()
            .setType(ContactType.EMAIL)
            .setText("test@test.com.au"))
        .addContact(Contact.newBuilder()
            .setType(ContactType.MOBILE_PHONE)
            .setText("0400 123 456"))
    if (index % 3 != 0) {
      customer.setIndividual(Individual.newBuilder()
          .setFirstName("JOHN")
          .setMiddleName("M")
          .setLastName("SMITH")
          .setGender(Gender.MALE)
          .setDob(Date.newBuilder()
              .setYear(1985)
              .setMonth(12)
              .setDay(24)))
    } else {
      customer.setOrganisation(Organisation.newBuilder()
          .setName("ACME WIDGETS LTD")
          .setContact(Individual.newBuilder()
              .setFirstName("JOHN")
              .setLastName("ANDERSON")))
    }
    return customer.build()
  }
}