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

import com.google.common.collect.ImmutableSet
import org.springframework.core.convert.TypeDescriptor
import org.springframework.core.convert.converter.GenericConverter
import org.springframework.core.convert.converter.GenericConverter.ConvertiblePair

class SimpleConverter: GenericConverter {
  private val converters = mutableMapOf<ConvertiblePair, (Any?) -> Any?>()

  @Suppress("UNCHECKED_CAST")
  fun <T,R> register(source: Class<T>, target: Class<R>, function: (T) -> R) {
    converters[ConvertiblePair(source,target)] = function as (Any?) -> Any?
  }

  fun <T,R> registerBidirectional(
      source: Class<T>,
      target: Class<R>,
      sourceToTarget: (T) -> R,
      targetToSource: (R) -> T) {
    register(source, target, sourceToTarget)
    register(target, source, targetToSource)
  }

  override fun getConvertibleTypes(): MutableSet<ConvertiblePair> = ImmutableSet.copyOf(converters.keys)

  override fun convert(source: Any?, sourceType: TypeDescriptor, targetType: TypeDescriptor): Any? {
    val converter = converters[ConvertiblePair(sourceType.type, targetType.type)]
    if (converter != null) {
      return converter(source)
    }
    return null
  }
}
