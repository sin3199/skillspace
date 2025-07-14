package com.skillspace.sgs.host.payment;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class HostPaymentService {

    private final HostPaymentMapper hostPaymentMapper;

    // 결제 관련 공간 소유자 조회
    public String getPaymentSpaceOwnerUserId(Integer payment_id) {
        return hostPaymentMapper.getPaymentSpaceOwnerUserId(payment_id);
    }

    // 결제상태 변경
    public boolean updatePaymentStatus(Integer payment_id, String status) {
        return hostPaymentMapper.updatePaymentStatus(payment_id, status);
    }


}
