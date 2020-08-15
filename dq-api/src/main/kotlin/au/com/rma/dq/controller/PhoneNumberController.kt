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
package au.com.rma.dq.controller

import au.com.rma.dq.kafka.model.ScrubRequestModel
import au.com.rma.dq.kafka.model.ScrubResponseModel
import au.com.rma.dq.kafka.model.fromModel
import au.com.rma.dq.kafka.model.toModel
import au.com.rma.dq.model.ScrubResponse
import au.com.rma.dq.model.ScrubbedPhoneNumber
import au.com.rma.dq.service.PhoneNumberService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.time.Duration

@RestController
@RequestMapping("/api/scrub")
class PhoneNumberController(
    val service: PhoneNumberService
) {

  @PostMapping
  fun scrubPhone(@RequestBody request: ScrubRequestModel): Mono<ScrubResponseModel> {
    val protobufRequest = fromModel(request)

    // Send customer
    return Mono.just(toModelResponse(service.scrubPhoneNumbers(protobufRequest.phoneNumbersList)))
        .delayElement(Duration.ofMillis(500))
  }

  fun toModelResponse(numbers: List<ScrubbedPhoneNumber>) = toModel(ScrubResponse.newBuilder()
      .addAllPhoneNumbers(numbers)
      .build())

}