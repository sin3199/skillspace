package com.skillspace.sgs.host.timeslots;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TimeSlotsDTO {

    private Integer timeslotId;
    private Integer productId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String isReserved;
    // private BigDecimal additionalAmount;     // 보류
    // private BigDecimal discountAmount;       // 보류
    private Integer maxHeadcount; 
    private Integer currentHeadcount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
