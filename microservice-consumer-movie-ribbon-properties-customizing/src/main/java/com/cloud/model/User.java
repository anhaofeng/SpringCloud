package com.cloud.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class User  {
        private Long id;
        private String userName;
        private String name;
        private Short age;
        private BigDecimal balance;
}
