package org.example.base.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.example.base.components.dse.DseComponent;
import org.example.base.entity.BizDse;
import org.example.base.repo.BizDseRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class BizDseServiceImpl implements IBizDseService {

    private final BizDseRepository bizDseRepository;

    private final DseComponent dseComponent;

    @Override
    public Mono<BizDse> save(String origin, String algorithm) {
        if (StringUtils.isNotBlank(origin)) {
            String salt = StringUtils.substring(origin, 0, 6);
            String sha256 = DigestUtils.sha256Hex(salt + origin);
            Long length = (long) origin.length();
            return bizDseRepository.findByAlgorithmAndOriginSha256AndOriginLength(algorithm, sha256, length)
                    .switchIfEmpty(Mono.defer(() -> generator(origin, algorithm, sha256, length)));
        } else {
            return Mono.empty();
        }
    }

    private Mono<BizDse> generator(String origin, String algorithm, String sha256, Long length) {
        log.info("需要加绝密一下:{}", origin);
        BizDse bizDse = new BizDse();
        bizDse.setAlgorithm(algorithm);
        bizDse.setOriginLength(length);
        bizDse.setOriginSha256(sha256);
        bizDse.setStatus("initiated");
        return bizDseRepository.save(bizDse).flatMap(dse -> {
            dse.setStatus("processing");
            return bizDseRepository.save(dse).then(dseComponent.encode(algorithm, origin).flatMap(dseInfo -> {
                dse.setDseInfo(dseInfo);
                dse.setStatus("succeed");
                return bizDseRepository.save(dse);
            }).onErrorResume(e -> {
                dse.setStatus("failed");
                dse.setMessage(e.getMessage());
                return bizDseRepository.save(dse).then(Mono.error(e));
            }).then(Mono.just(dse)));
        });

    }
}
