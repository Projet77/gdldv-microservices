package com.gdldv.user.dto;

public enum RefundPolicy {
    FULL_REFUND,      // >48h: 100%
    PARTIAL_REFUND,   // 24-48h: 75%
    LOW_REFUND,       // <24h: 50%
    NO_REFUND         // Après début: 0%
}
