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

import au.com.rma.test.configuration.ConversionConfiguration
import au.com.rma.test.customer.Contact
import au.com.rma.test.customer.ContactType
import au.com.rma.test.model.ContactModel
import au.com.rma.test.model.ContactTypeModel
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments.of
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.convert.ConversionService
import org.springframework.core.convert.TypeDescriptor

@SpringBootTest(classes = [ConversionConfiguration::class])
class ContactConversionTest {

  @Autowired
  lateinit var converter: ConversionService
  val contactType = TypeDescriptor.valueOf(Contact::class.java)
  val contactModelType = TypeDescriptor.valueOf(ContactModel::class.java)

  @ParameterizedTest
  @MethodSource("contacts")
  fun `ensure Contact to ContactModel conversion works`(text: String, type: ContactType, expected: ContactTypeModel){
    val source = Contact.newBuilder()
        .setType(type)
        .setText(text)
        .build()
    val contactModel = converter.convert(source, contactType, contactModelType) as ContactModel

    assertEquals(expected, contactModel.type)
    assertEquals(text, contactModel.text)

    val newContact = converter.convert(contactModel, contactModelType, contactType) as Contact

    assertEquals(type, type)
    assertEquals(text, newContact.text)
  }

  companion object {
    @JvmStatic
    fun contacts() = listOf(
        of("07 1000 0001", ContactType.HOME_PHONE, ContactTypeModel.HOME_PHONE),
        of("07 1000 0002", ContactType.MOBILE_PHONE, ContactTypeModel.MOBILE_PHONE),
        of("07 1000 0003", ContactType.EMAIL, ContactTypeModel.EMAIL),
        of("07 1000 0004", ContactType.UNDEFINED_CONTACT_TYPE, ContactTypeModel.UNDEFINED)
    )
  }
}