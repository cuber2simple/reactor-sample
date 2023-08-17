package org.example.exchange.repo;


import org.example.exchange.entity.TestDse;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

/**
 * <p>
 * 系统字典 repository 接口
 * </p>
 *
 * @author cuber
 * @since 2023-07-08
 */
public interface TestDseRepository extends ReactiveCrudRepository<TestDse, Long> {

}

