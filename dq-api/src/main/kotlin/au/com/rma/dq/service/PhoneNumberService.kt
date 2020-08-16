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
package au.com.rma.dq.service

import au.com.rma.dq.model.PhoneNumber
import au.com.rma.dq.model.ScrubStatus
import au.com.rma.dq.model.ScrubbedPhoneNumber
import org.springframework.stereotype.Component
import java.util.stream.Collectors

@Component
class PhoneNumberService {

  fun scrubPhoneNumbers(numbers: Collection<PhoneNumber>): List<ScrubbedPhoneNumber> = numbers.stream()
      .map(this::scrubPhoneNumber)
      .collect(Collectors.toList())

  fun scrubPhoneNumber(number: PhoneNumber): ScrubbedPhoneNumber {
    var phoneNumber = number.phoneNumber.replace(Regex("[^0-9+]"), "")
    if (phoneNumber.startsWith("0")) {
      phoneNumber = when (number.countryCode.toUpperCase()) {
        "AU" -> "+61${phoneNumber.substring(1)}"
        "NZ" -> "+64${phoneNumber.substring(1)}"
        else -> phoneNumber
      }
    }
    return ScrubbedPhoneNumber.newBuilder()
        .setReference(number.reference)
        .setPhoneNumber(phoneNumber)
        .setScrubStatus(when {
          phoneNumber.startsWith("+") -> ScrubStatus.SCRUBBED
          else -> ScrubStatus.UNSCRUBBED
        })
        .build()
  }
}
