package org.example.exchange.feign;

import org.example.exchange.feign.req.DseReq;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

@ReactiveFeignClient(name = "base", path = "/feign")
public interface BaseFeignApi {
    @PostMapping("/encode")
    Mono<String> encode(@RequestBody DseReq dseReq);


    @PostMapping("/decode")
    Mono<String> decode(@RequestBody String dseKey);
}
