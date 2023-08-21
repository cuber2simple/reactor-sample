package org.example.base.repo;

import org.example.base.entity.BizDse;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

/**
 * <p>
 * 第二次加密 repository 接口
 * </p>
 *
 * @author cuber
 * @since 2023-07-08
 */
public interface BizDseRepository extends ReactiveCrudRepository<BizDse, Long> {
    Mono<BizDse> findByAlgorithmAndOriginSha256AndOriginLength(String algorithm, String originSha256, Long originLength);
}

