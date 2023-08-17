package org.example.exchange.feign.req;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DseReq {

    private String origin;

    private String algorithm;
}

