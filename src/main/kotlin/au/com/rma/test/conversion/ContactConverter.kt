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

import au.com.rma.test.customer.Contact
import au.com.rma.test.customer.ContactType
import au.com.rma.test.model.ContactModel
import au.com.rma.test.model.ContactTypeModel

object ContactConverter {

  fun toModel(contact: Contact) = ContactModel(
      type = when (contact.type) {
        ContactType.EMAIL -> ContactTypeModel.EMAIL
        ContactType.MOBILE_PHONE -> ContactTypeModel.MOBILE_PHONE
        ContactType.HOME_PHONE -> ContactTypeModel.HOME_PHONE
        else -> ContactTypeModel.UNDEFINED
      },
      text = contact.text
  )

  fun fromModel(contact: ContactModel): Contact = Contact.newBuilder()
      .setType(when (contact.type) {
        ContactTypeModel.EMAIL -> ContactType.EMAIL
        ContactTypeModel.MOBILE_PHONE -> ContactType.MOBILE_PHONE
        ContactTypeModel.HOME_PHONE -> ContactType.HOME_PHONE
        else -> ContactType.UNDEFINED_CONTACT_TYPE
      })
      .setText(contact.text)
      .build()
}