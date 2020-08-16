package au.com.rma.test.service

import au.com.rma.dq.model.ScrubRequest
import au.com.rma.dq.model.ScrubResponse
import reactor.core.publisher.Mono

interface DataQualityService {
  fun queryDataQuality(request: ScrubRequest): Mono<ScrubResponse>
}