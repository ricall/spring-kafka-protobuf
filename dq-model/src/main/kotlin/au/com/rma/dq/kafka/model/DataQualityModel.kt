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
package au.com.rma.dq.kafka.model

import au.com.rma.dq.model.PhoneNumber
import au.com.rma.dq.model.ScrubRequest
import au.com.rma.dq.model.ScrubResponse
import au.com.rma.dq.model.ScrubStatus
import java.util.stream.Collectors

enum class ScrubStatusModel {
  SCRUBBED,
  UNSCRUBBED
}

data class PhoneNumberModel (
    var reference: String,
    var countryCode: String?,
    var phoneNumber: String
)

data class ScrubRequestModel(
    var phoneNumbers: List<PhoneNumberModel>
)

data class ScrubbedPhoneNumberModel(
    var reference: String,
    var scrubStatus: ScrubStatusModel,
    val phoneNumber: String
)

data class ScrubResponseModel(
    var phoneNumbers: List<ScrubbedPhoneNumberModel>
)

fun fromModel(request: ScrubRequestModel): ScrubRequest = ScrubRequest.newBuilder()
    .addAllPhoneNumbers(request.phoneNumbers.stream()
        .map { pn -> PhoneNumber.newBuilder()
            .setReference(pn.reference)
            .setCountryCode(pn.countryCode)
            .setPhoneNumber(pn.phoneNumber)
            .build() }
        .collect(Collectors.toList()))
    .build()

fun toModel(response: ScrubResponse): ScrubResponseModel = ScrubResponseModel(
    phoneNumbers = response.phoneNumbersList.stream()
        .map { pn -> ScrubbedPhoneNumberModel(
            reference = pn.reference,
            scrubStatus = when(pn.scrubStatus) {
              ScrubStatus.SCRUBBED -> ScrubStatusModel.SCRUBBED
              else -> ScrubStatusModel.UNSCRUBBED
            },
            phoneNumber = pn.phoneNumber
        )}
        .collect(Collectors.toList())
)