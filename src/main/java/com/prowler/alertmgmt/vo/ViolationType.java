package com.prowler.alertmgmt.vo;

public enum ViolationType {
    CREDIT_CARD_NUMBER("CREDIT_CARD_NUMBER"),
    MOBILE_NUMBER("MOBILE_NUMBER"),
    BANK_ACCOUNT_NUMBER("BANK_ACCOUNT_NUMBER");

    private final String val;

    ViolationType(String val) {
        this.val = val;
    }
}
