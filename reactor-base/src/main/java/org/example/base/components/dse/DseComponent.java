package org.example.base.components.dse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.example.base.entity.DseAlgorithm;
import org.example.base.repo.DseAlgorithmRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cglib.core.ReflectUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
@RequiredArgsConstructor
public class DseComponent {

    private final DseAlgorithmRepository dseAlgorithmRepository;

    private static final ConcurrentHashMap<String, BaseDseService> CACHE_BASE_DSE_MAP = new ConcurrentHashMap<>();

    @EventListener
    public void readEvent(ApplicationReadyEvent applicationReadyEvent) {
        dseAlgorithmRepository
                .findAll()
                .map(this::computeIfAbsent)
                .onErrorContinue((throwable, o) -> log.error("[{}]发生错误", o, throwable))
                .subscribe();
    }

    private BaseDseService computeIfAbsent(DseAlgorithm dseAlgorithm) {
        return CACHE_BASE_DSE_MAP.computeIfAbsent(dseAlgorithm.getAlgorithm(), key -> {
            try {
                return (BaseDseService) ReflectUtils.newInstance(Class.forName(dseAlgorithm.getImplementation()), new Class[]{DseAlgorithm.class}, new Object[]{dseAlgorithm});
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public Mono<String> encode(String algorithm, byte[] originBytes) {
        return retrieve(algorithm).flatMap(baseDseService -> baseDseService.encode(originBytes));
    }


    public Mono<BaseDseService> retrieve(String algorithm) {
        Mono<BaseDseService> mono = null;
        if (StringUtils.isNotBlank(algorithm)) {
            if (CACHE_BASE_DSE_MAP.containsKey(algorithm)) {
                mono = Mono.just(CACHE_BASE_DSE_MAP.get(algorithm));
            } else {
                mono = dseAlgorithmRepository.findByAlgorithm(algorithm).map(this::computeIfAbsent);

            }
        } else {
            List<String> keyList = CACHE_BASE_DSE_MAP.keySet().stream().toList();
            int size = keyList.size();
            int random = RandomUtils.nextInt(0, size);
            String s = keyList.get(random);
            mono = Mono.just(CACHE_BASE_DSE_MAP.get(s));
        }
        return mono;
    }

    public String choiceAlgorithm(String algorithm) {
        if (StringUtils.isNotBlank(algorithm) && CACHE_BASE_DSE_MAP.containsKey(algorithm)) {
            return algorithm;
        } else {
            List<String> keyList = CACHE_BASE_DSE_MAP.keySet().stream().toList();
            int size = keyList.size();
            int random = RandomUtils.nextInt(0, size);
            return keyList.get(random);
        }
    }


    public Mono<String> encode(String algorithm, String origin) {
        return retrieve(algorithm).flatMap(baseDseService -> baseDseService.encode(origin));
    }

    public Mono<String> decode(String algorithm, String dseInfo) {
        if (CACHE_BASE_DSE_MAP.containsKey(algorithm)) {
            return Mono.just(CACHE_BASE_DSE_MAP.get(algorithm)).flatMap(baseDseService -> baseDseService.decode(dseInfo));
        } else {
            return dseAlgorithmRepository.findByAlgorithm(algorithm).map(this::computeIfAbsent).flatMap(baseDseService -> baseDseService.decode(dseInfo));
        }
    }


}
