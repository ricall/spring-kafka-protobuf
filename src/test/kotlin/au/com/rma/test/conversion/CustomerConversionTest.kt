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

import au.com.rma.test.configuration.ConversionConfig
import au.com.rma.test.customer.*
import au.com.rma.test.model.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.convert.ConversionService
import org.springframework.core.convert.TypeDescriptor
import au.com.rma.test.customer.Date as ProtobufDate

@SpringBootTest(classes = [ConversionConfig::class])
class CustomerConversionTest {
  @Autowired
  lateinit var converter: ConversionService
  val protocolType = TypeDescriptor.valueOf(Customer::class.java)
  val modelType = TypeDescriptor.valueOf(CustomerModel::class.java)

  val dob = ProtobufDate.newBuilder()
      .setDay(13)
      .setMonth(6)
      .setYear(1992)
  val individual = Individual.newBuilder()
      .setFirstName("JOHN")
      .setMiddleName("ANDREW")
      .setLastName("SMITH")
      .setGender(Gender.MALE)
      .setDob(dob)
      .build()
  val organisation = Organisation.newBuilder()
      .setName("ACME WIDGETS")
      .setContact(individual)
      .build()
  val mobile = Contact.newBuilder()
      .setType(ContactType.MOBILE_PHONE)
      .setText("0400123456")
      .build()
  val email = Contact.newBuilder()
      .setType(ContactType.EMAIL)
      .setText("test@test.com")
      .build()
  val physicalAddress = Address.newBuilder()
      .setType(AddressType.PHYSICAL)
      .setLine1("PHYSICAL ADDRESS")
      .build()
  val postalAddress = Address.newBuilder()
      .setType(AddressType.POSTAL)
      .setLine1("POSTAL ADDRESS")
      .build()

  @Test
  fun `ensure Customer conversion for individuals works`(){
    val customer = Customer.newBuilder()
        .setId(1234L)
        .setIndividual(individual)
        .addContact(mobile)
        .addContact(email)
        .addAddress(physicalAddress)
        .addAddress(postalAddress)
        .build()
    val model = converter.convert(customer, protocolType, modelType) as CustomerModel

    assertEquals(1234L, model.id)
    assertEquals("JOHN", model.individual?.firstName)
    assertEquals(ContactTypeModel.MOBILE_PHONE, model.contacts[0].type)
    assertEquals(ContactTypeModel.EMAIL, model.contacts[1].type)
    assertEquals(AddressTypeModel.PHYSICAL, model.addresses[0].type)
    assertEquals(AddressTypeModel.POSTAL, model.addresses[1].type)

    val newCustomer = converter.convert(model, modelType, protocolType) as Customer

    assertEquals(1234L, newCustomer.id)
    assertEquals("JOHN", newCustomer.individual.firstName)
    assertEquals(ContactType.MOBILE_PHONE, newCustomer.contactList[0].type)
    assertEquals(ContactType.EMAIL, newCustomer.contactList[1].type)
    assertEquals(AddressType.PHYSICAL, newCustomer.addressList[0].type)
    assertEquals(AddressType.POSTAL, newCustomer.addressList[1].type)
  }

  @Test
  fun `ensure Customer conversion for organisations works`(){
    val customer = Customer.newBuilder()
        .setId(1234L)
        .setOrganisation(organisation)
        .addContact(mobile)
        .addContact(email)
        .addAddress(physicalAddress)
        .addAddress(postalAddress)
        .build()
    val model = converter.convert(customer, protocolType, modelType) as CustomerModel

    assertEquals(1234L, model.id)
    assertEquals("ACME WIDGETS", model.organisation?.name)
    assertEquals(ContactTypeModel.MOBILE_PHONE, model.contacts[0].type)
    assertEquals(ContactTypeModel.EMAIL, model.contacts[1].type)
    assertEquals(AddressTypeModel.PHYSICAL, model.addresses[0].type)
    assertEquals(AddressTypeModel.POSTAL, model.addresses[1].type)

    val newCustomer = converter.convert(model, modelType, protocolType) as Customer

    assertEquals(1234L, newCustomer.id)
    assertEquals("ACME WIDGETS", newCustomer.organisation?.name)
    assertEquals(ContactType.MOBILE_PHONE, newCustomer.contactList[0].type)
    assertEquals(ContactType.EMAIL, newCustomer.contactList[1].type)
    assertEquals(AddressType.PHYSICAL, newCustomer.addressList[0].type)
    assertEquals(AddressType.POSTAL, newCustomer.addressList[1].type)
  }

}