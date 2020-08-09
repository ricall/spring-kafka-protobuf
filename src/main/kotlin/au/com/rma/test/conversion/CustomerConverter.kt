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
package au.com.rma.test.conversion

import au.com.rma.test.customer.Customer
import au.com.rma.test.model.CustomerModel
import java.util.stream.Collectors

object CustomerConverter {
  fun toModel(customer: Customer) = CustomerModel(
      id = customer.id,
      individual = when (customer.hasIndividual()) {
        true -> IndividualConverter.toModel(customer.individual)
        else -> null
      },
      organisation = when (customer.hasOrganisation()) {
        true -> OrganisationConverter.toModel(customer.organisation)
        else -> null
      },
      contacts = customer.contactList.stream()
          .map(ContactConverter::toModel)
          .collect(Collectors.toList()),
      addresses = customer.addressList.stream()
          .map(AddressConverter::toModel)
          .collect(Collectors.toList())
  )

  fun fromModel(customer: CustomerModel): Customer {
    val builder = Customer.newBuilder()
        .setId(customer.id)

    if (customer.individual != null) {
      builder.setIndividual(IndividualConverter.fromModel(customer.individual!!))
    }
    if (customer.organisation != null) {
      builder.setOrganisation(OrganisationConverter.fromModel(customer.organisation!!))
    }
    customer.contacts.stream()
        .map(ContactConverter::fromModel)
        .forEach { contact -> builder.addContact(contact) }
    customer.addresses.stream()
        .map(AddressConverter::fromModel)
        .forEach { address -> builder.addAddress(address) }

    return builder.build()
  }
}