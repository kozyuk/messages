package com.skf.messages.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TimeInterval {
    private long from;
    private long to;
}
