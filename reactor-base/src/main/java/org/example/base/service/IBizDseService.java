package org.example.base.service;

import org.example.base.entity.BizDse;
import reactor.core.publisher.Mono;

public interface IBizDseService {
    Mono<BizDse> save(String origin, String algorithm);
}
