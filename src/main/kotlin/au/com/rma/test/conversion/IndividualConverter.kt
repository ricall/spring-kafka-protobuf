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

import au.com.rma.test.customer.Date
import au.com.rma.test.customer.Gender
import au.com.rma.test.customer.Individual
import au.com.rma.test.model.GenderModel
import au.com.rma.test.model.IndividualModel
import java.time.LocalDate

object IndividualConverter {
  fun toModel(individual: Individual) = IndividualModel(
      firstName = individual.firstName,
      middleName = individual.middleName,
      lastName = individual.lastName,
      gender = when (individual.gender) {
        Gender.MALE -> GenderModel.MALE
        Gender.FEMALE -> GenderModel.FEMALE
        else -> GenderModel.UNKNOWN
      },
      dateOfBirth = toModelDate(individual.dob)
  )

  private fun toModelDate(date: Date) = when (date.day) {
    0 -> null
    else -> LocalDate.of(date.year, date.month, date.day)
  }

  fun fromModel(individual: IndividualModel): Individual = Individual.newBuilder()
      .setFirstName(individual.firstName)
      .setMiddleName(individual.middleName ?: "")
      .setLastName(individual.lastName)
      .setGender(when (individual.gender) {
        GenderModel.MALE -> Gender.MALE
        GenderModel.FEMALE -> Gender.FEMALE
        else -> Gender.UNDEFINED_GENDER
      })
      .setDob(fromModelDate(individual.dateOfBirth))
      .build()

  private fun fromModelDate(date: LocalDate?) = when (date == null) {
    true -> Date.newBuilder().build()
    false -> Date.newBuilder()
        .setDay(date.dayOfMonth)
        .setMonth(date.monthValue)
        .setYear(date.year)
        .build()
  }
}