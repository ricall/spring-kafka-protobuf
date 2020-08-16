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
package au.com.rma.dq.kafka.conversion

import org.apache.kafka.common.serialization.Serializer
import au.com.rma.dq.model.ScrubRequest
import au.com.rma.dq.model.ScrubResponse
import org.apache.kafka.common.serialization.Deserializer

class ScrubRequestSerializer: Serializer<ScrubRequest> {
  override fun serialize(topic: String?, data: ScrubRequest?) = data?.toByteArray()
}

class ScrubRequestDeserializer: Deserializer<ScrubRequest> {
  override fun deserialize(topic: String?, data: ByteArray?) = ScrubRequest.parseFrom(data)
}

class ScrubResponseSerializer: Serializer<ScrubResponse> {
  override fun serialize(topic: String?, data: ScrubResponse?) = data?.toByteArray()
}

class ScrubResponseDeserializer: Deserializer<ScrubResponse> {
  override fun deserialize(topic: String?, data: ByteArray?) = ScrubResponse.parseFrom(data)
}
