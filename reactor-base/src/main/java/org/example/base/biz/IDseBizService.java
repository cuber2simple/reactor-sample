package org.example.base.biz;

import org.example.base.req.DseReq;
import reactor.core.publisher.Mono;

public interface IDseBizService {

    Mono<String> encode(DseReq dseReq);

    Mono<String> decode(String dseKey);
}
