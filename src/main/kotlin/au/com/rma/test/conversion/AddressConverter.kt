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

import au.com.rma.test.customer.Address
import au.com.rma.test.customer.AddressType
import au.com.rma.test.model.AddressModel
import au.com.rma.test.model.AddressTypeModel

object AddressConverter {
  fun toModel(address: Address) = AddressModel(
      type = when (address.type) {
        AddressType.PHYSICAL -> AddressTypeModel.PHYSICAL
        AddressType.POSTAL -> AddressTypeModel.POSTAL
        else -> AddressTypeModel.UNKNOWN
      },
      line1 = address.line1,
      line2 = address.line2,
      line3 = address.line3,
      line4 = address.line4,
      suburb = address.suburb,
      city = address.city,
      postcode = address.postcode,
      state = address.state,
      countryCode = address.countryCode,
      dpid = address.dpid
  )

  fun fromModel(address: AddressModel): Address = Address.newBuilder()
      .setType(when (address.type) {
        AddressTypeModel.PHYSICAL -> AddressType.PHYSICAL
        AddressTypeModel.POSTAL -> AddressType.POSTAL
        else -> AddressType.UNDEFINED_ADDRESS_TYPE
      })
      .setLine1(address.line1)
      .setLine2(address.line2 ?: "")
      .setLine3(address.line3 ?: "")
      .setLine4(address.line4 ?: "")
      .setSuburb(address.suburb ?: "")
      .setCity(address.city ?: "")
      .setPostcode(address.postcode ?: "")
      .setState(address.state ?: "")
      .setCountryCode(address.countryCode ?: "")
      .setDpid(address.dpid)
      .build()
}