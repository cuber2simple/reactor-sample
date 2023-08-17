package org.example.exchange.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table(name = "t_test_dse", schema = "public")
public class TestDse {

    @Id
    private Long id;

    @Column("dse_test")
    private String dseTest;
}
