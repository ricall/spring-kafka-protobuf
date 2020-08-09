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
package au.com.rma.test.configuration

import au.com.rma.test.conversion.*
import au.com.rma.test.customer.*
import au.com.rma.test.model.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.format.support.DefaultFormattingConversionService

@Configuration
class ConversionConfiguration {

  @Bean
  fun conversionService() = DefaultFormattingConversionService().apply {
    addConverter(customerConverter())
  }

  fun customerConverter() = SimpleConverter().apply {
    registerBidirectional(
        Customer::class.java,
        CustomerModel::class.java,
        CustomerConverter::toModel,
        CustomerConverter::fromModel)
    registerBidirectional(
        Individual::class.java,
        IndividualModel::class.java,
        IndividualConverter::toModel,
        IndividualConverter::fromModel)
    registerBidirectional(
        Organisation::class.java,
        OrganisationModel::class.java,
        OrganisationConverter::toModel,
        OrganisationConverter::fromModel)
    registerBidirectional(
        Contact::class.java,
        ContactModel::class.java,
        ContactConverter::toModel,
        ContactConverter::fromModel)
    registerBidirectional(
        Address::class.java,
        AddressModel::class.java,
        AddressConverter::toModel,
        AddressConverter::fromModel)
  }
}