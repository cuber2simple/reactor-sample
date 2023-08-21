package org.example.base.biz;

import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.base.components.dse.DseComponent;
import org.example.base.repo.BizDseRepository;
import org.example.base.req.DseReq;
import org.example.base.service.IBizDseService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class DseBizServiceImpl implements IDseBizService {

    private final IBizDseService iBizDseService;


    private final DseComponent dseComponent;


    private final BizDseRepository bizDseRepository;

    @Override
    public Mono<String> encode(DseReq dseReq) {
        String origin = dseReq.getOrigin();
        String algorithm = dseComponent.choiceAlgorithm(dseReq.getAlgorithm());
        return iBizDseService.save(origin, algorithm).flatMap(bizDse -> {
            if (StringUtils.equals(bizDse.getStatus(), "failed")) {
                return Mono.error(new IllegalArgumentException());
            } else {
                return Mono.just(String.valueOf(bizDse.getId()));
            }
        }).switchIfEmpty(Mono.error(new IllegalArgumentException()));
    }

    @Override
    public Mono<String> decode(String dseKey) {
        return bizDseRepository.findById(Long.parseLong(dseKey))
                .flatMap(bizDse -> dseComponent.decode(bizDse.getAlgorithm(), bizDse.getDseInfo()));
    }

}
