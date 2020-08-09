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
import au.com.rma.test.customer.Address
import au.com.rma.test.customer.AddressType
import au.com.rma.test.model.AddressModel
import au.com.rma.test.model.AddressTypeModel
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Import
import org.springframework.core.convert.ConversionService
import org.springframework.core.convert.TypeDescriptor
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(classes = [ConversionConfiguration::class])
class AddressConversionTest {

  @Autowired
  lateinit var converter: ConversionService
  val addressType = TypeDescriptor.valueOf(Address::class.java)
  val addressModelType = TypeDescriptor.valueOf(AddressModel::class.java)

  @Test
  fun `ensure Address to AddressModel conversion works`(){
    val address = Address.newBuilder()
        .setType(AddressType.PHYSICAL)
        .setLine1("line1")
        .setLine2("line2")
        .setLine3("line3")
        .setLine4("line4")
        .setSuburb("suburb")
        .setCity("city")
        .setPostcode("postcode")
        .setState("state")
        .setCountryCode("countryCode")
        .setDpid(12345678L)
        .build()

    val addressModel = converter.convert(address, addressType, addressModelType) as AddressModel

    Assertions.assertEquals(AddressTypeModel.PHYSICAL, addressModel.type)
    Assertions.assertEquals("line1", addressModel.line1)
    Assertions.assertEquals("line2", addressModel.line2)
    Assertions.assertEquals("line3", addressModel.line3)
    Assertions.assertEquals("line4", addressModel.line4)
    Assertions.assertEquals("suburb", addressModel.suburb)
    Assertions.assertEquals("city", addressModel.city)
    Assertions.assertEquals("postcode", addressModel.postcode)
    Assertions.assertEquals("state", addressModel.state)
    Assertions.assertEquals("countryCode", addressModel.countryCode)
    Assertions.assertEquals(12345678L, addressModel.dpid)

    val newAddress = converter.convert(addressModel, addressModelType, addressType) as Address

    Assertions.assertEquals(AddressType.PHYSICAL, newAddress.type)
    Assertions.assertEquals("line1", newAddress.line1)
    Assertions.assertEquals("line2", newAddress.line2)
    Assertions.assertEquals("line3", newAddress.line3)
    Assertions.assertEquals("line4", newAddress.line4)
    Assertions.assertEquals("suburb", newAddress.suburb)
    Assertions.assertEquals("city", newAddress.city)
    Assertions.assertEquals("postcode", newAddress.postcode)
    Assertions.assertEquals("state", newAddress.state)
    Assertions.assertEquals("countryCode", newAddress.countryCode)
    Assertions.assertEquals(12345678L, newAddress.dpid)
  }

}