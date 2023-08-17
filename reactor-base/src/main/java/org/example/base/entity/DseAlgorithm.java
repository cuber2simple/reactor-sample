package org.example.base.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * <p>
 * 加绝密算法
 * </p>
 *
 * @author cuber
 * @since 2023-07-12
 */
@Data
@Table(name = "t_dse_algorithm", schema = "public")
public class DseAlgorithm {

    @Id
    private Long id;
    /**
     * 算法名称
     */
    @Column("algorithm")
    private String algorithm;

    /**
     * 实现类
     */
    @Column("implementation")
    private String implementation;

    /**
     * 访问密钥
     */
    @Column("access_key")
    private String accessKey;

    /**
     * 密钥
     */
    @Column("secret_key")
    private String secretKey;

    /**
     * 属于哪个region_id云加密
     */
    @Column("region_id")
    private String regionId;

    /**
     * 密钥ID
     */
    @Column("key_info")
    private String keyInfo;

    /**
     * 算法额外扩展信息
     */
    @Column("algorithm_extra")
    private String algorithmExtra;

    /**
     * 优先级
     */
    @Column("idx")
    private Short idx;

}