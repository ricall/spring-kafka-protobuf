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
import au.com.rma.test.model.GenderModel
import au.com.rma.test.model.IndividualModel
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.convert.ConversionService
import org.springframework.core.convert.TypeDescriptor
import java.time.LocalDate
import au.com.rma.test.customer.Date as ProtobufDate

@SpringBootTest(classes = [ConversionConfig::class])
class IndividualConversionTest {
  @Autowired
  lateinit var converter: ConversionService
  val individualType = TypeDescriptor.valueOf(Individual::class.java)
  val individualModelType = TypeDescriptor.valueOf(IndividualModel::class.java)

  @Test
  fun `ensure Individual to IndividualModel conversion works`(){
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

    val individualModel = converter.convert(individual, individualType, individualModelType) as IndividualModel

    Assertions.assertEquals("JOHN", individualModel.firstName)
    Assertions.assertEquals("ANDREW", individualModel.middleName)
    Assertions.assertEquals("SMITH", individualModel.lastName)
    Assertions.assertEquals(GenderModel.MALE, individualModel.gender)
    Assertions.assertEquals(LocalDate.of(1992, 6, 13), individualModel.dateOfBirth)

    val newIndividual = converter.convert(individualModel, individualModelType, individualType) as Individual

    Assertions.assertEquals("JOHN", newIndividual.firstName)
    Assertions.assertEquals("ANDREW", newIndividual.middleName)
    Assertions.assertEquals("SMITH", newIndividual.lastName)
    Assertions.assertEquals(Gender.MALE, newIndividual.gender)
    Assertions.assertEquals(dob.day, newIndividual.dob.day)
    Assertions.assertEquals(dob.month, newIndividual.dob.month)
    Assertions.assertEquals(dob.year, newIndividual.dob.year)
  }

}