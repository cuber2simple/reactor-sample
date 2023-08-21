package org.example.base.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * <p>
 * 第二次加密
 * </p>
 *
 * @author cuber
 * @since 2023-07-08
 */
@Data
@Table(name = "t_biz_dse", schema = "public")
public class BizDse {

    @Id
    private Long id;

    /**
     * 原始报文的sha256值
     */
    @Column("origin_sha256")
    private String originSha256;

    /**
     * 原始报文长度
     */
    @Column("origin_length")
    private Long originLength;


    /**
     * 加密算法
     */
    @Column("algorithm")
    private String algorithm;


    /**
     * 加密的原始KEY
     */
    @Column("dse_info")
    private String dseInfo;

    /**
     * @dict(parent_dict_code=normal_status)状态
     */
    @Column("status")
    private String status;


    /**
     * 加密后的消息
     */
    @Column("message")
    private String message;

    /**
     * 加密完成的完成时间
     */
    @Column("completed_at")
    private LocalDateTime completedAt;

}