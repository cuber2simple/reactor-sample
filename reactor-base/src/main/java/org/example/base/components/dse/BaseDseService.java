package org.example.base.components.dse;

import org.example.base.entity.DseAlgorithm;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

public abstract class BaseDseService {

    protected final DseAlgorithm dseAlgorithm;

    protected final Integer idx;


    public static String getKey(DseAlgorithm dseAlgorithm) {
        return dseAlgorithm.getAccessKey() + "_" + dseAlgorithm.getSecretKey() +
                "_" + dseAlgorithm.getRegionId();
    }

    public BaseDseService(DseAlgorithm dseAlgorithm) {
        this.dseAlgorithm = dseAlgorithm;
        this.idx = Integer.valueOf(dseAlgorithm.getIdx());
    }

    /**
     * 加密
     *
     * @param originBytes 原始数组
     * @return 加密后的信息
     */
    public abstract Mono<String> encode(byte[] originBytes);

    /**
     * 解密
     *
     * @param origin 原始字符串
     * @return 加密后的信息
     */
    public Mono<String> encode(String origin) {
        return encode(origin.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * @param dseInfo 密文信息
     * @return 明文
     */
    public abstract Mono<String> decode(String dseInfo);

}
