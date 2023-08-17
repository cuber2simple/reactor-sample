package org.example.base.repo;

import org.example.base.entity.DseAlgorithm;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

/**
 * <p>
 * 加绝密算法 repository 接口
 * </p>
 *
 * @author cuber
 * @since 2023-07-12
 */
public interface DseAlgorithmRepository extends ReactiveCrudRepository<DseAlgorithm, Long> {

    Mono<DseAlgorithm> findByAlgorithm(String algorithm);
}

