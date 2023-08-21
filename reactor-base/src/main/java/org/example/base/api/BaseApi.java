package org.example.base.api;

import lombok.RequiredArgsConstructor;
import org.example.base.biz.IDseBizService;
import org.example.base.req.DseReq;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
@RequiredArgsConstructor
@RequestMapping("/feign")
public class BaseApi {
    private final IDseBizService iDseBizService;

    @PostMapping("/encode")
    public Mono<String> encode(@RequestBody DseReq dseReq) {
        return iDseBizService.encode(dseReq).subscribeOn(Schedulers.boundedElastic());
    }

    @PostMapping("/decode")
    public Mono<String> decode(@RequestBody String dseKey) {
        return iDseBizService.decode(dseKey);
    }

}
