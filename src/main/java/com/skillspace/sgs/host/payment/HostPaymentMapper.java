package com.skillspace.sgs.host.payment;

import org.apache.ibatis.annotations.Param;

public interface HostPaymentMapper {

    // 결제관련 공간 소유자 조회
    String getPaymentSpaceOwnerUserId(Integer payment_id);

    // 결제상태 변경
    boolean updatePaymentStatus(
                @Param("payment_id") Integer payment_id, 
                @Param("status") String status);

}
