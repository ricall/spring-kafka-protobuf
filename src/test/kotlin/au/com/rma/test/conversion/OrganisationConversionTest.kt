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
import au.com.rma.test.customer.Gender
import au.com.rma.test.customer.Individual
import au.com.rma.test.customer.Organisation
import au.com.rma.test.model.GenderModel
import au.com.rma.test.model.OrganisationModel
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.convert.ConversionService
import org.springframework.core.convert.TypeDescriptor
import java.time.LocalDate
import au.com.rma.test.customer.Date as ProtobufDate

@SpringBootTest(classes = [ConversionConfig::class])
class OrganisationConversionTest {
  @Autowired
  lateinit var converter: ConversionService
  val protocolType = TypeDescriptor.valueOf(Organisation::class.java)
  val modelType = TypeDescriptor.valueOf(OrganisationModel::class.java)

  @Test
  fun `ensure Organisation to OrganisationModel conversion works`(){
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

    val model = converter.convert(organisation, protocolType, modelType) as OrganisationModel

    assertEquals("ACME WIDGETS", model.name)
    assertEquals("JOHN", model.contact?.firstName)
    assertEquals("ANDREW", model.contact?.middleName)
    assertEquals("SMITH", model.contact?.lastName)
    assertEquals(GenderModel.MALE, model.contact?.gender)
    assertEquals(LocalDate.of(1992, 6, 13), model.contact?.dateOfBirth)

    val newObject = converter.convert(model, modelType, protocolType) as Organisation

    assertEquals("ACME WIDGETS", newObject.name)
    assertEquals("JOHN", newObject.contact.firstName)
    assertEquals("ANDREW", newObject.contact.middleName)
    assertEquals("SMITH", newObject.contact.lastName)
    assertEquals(Gender.MALE, newObject.contact.gender)
    assertEquals(dob.day, newObject.contact.dob.day)
    assertEquals(dob.month, newObject.contact.dob.month)
    assertEquals(dob.year, newObject.contact.dob.year)
  }

}