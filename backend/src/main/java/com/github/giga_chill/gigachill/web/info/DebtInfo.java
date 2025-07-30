package com.github.giga_chill.gigachill.web.info;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DebtInfo {
    private UserInfo user;
    private BigDecimal amount;
}
