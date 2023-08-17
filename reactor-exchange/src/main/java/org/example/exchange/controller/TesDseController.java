package org.example.exchange.controller;

import lombok.AllArgsConstructor;
import org.example.exchange.entity.TestDse;
import org.example.exchange.feign.BaseFeignApi;
import org.example.exchange.feign.req.DseReq;
import org.example.exchange.repo.TestDseRepository;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/testDse")
@AllArgsConstructor
public class TesDseController {

    private final TestDseRepository testDseRepository;

    private final BaseFeignApi baseFeignApi;

    @PostMapping("/testDseMono")
    public Mono<TestDse> testDseMono(@RequestBody TestDse testDse) {
        return baseFeignApi.encode(DseReq.builder().origin(testDse.getDseTest()).algorithm("jasypt_gcm").build())
                .flatMap(s -> {
                    testDse.setDseTest(s);
                    return testDseRepository.save(testDse);
                });
    }

    @GetMapping("/view")
    public Mono<String> view(@RequestParam("id") long id) {
        return testDseRepository.findById(id)
                .flatMap(testDse -> baseFeignApi.decode(testDse.getDseTest()));
    }

}
