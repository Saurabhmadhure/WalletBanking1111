package com.walleto.model.controller;

public class EmailMessage {
    private String to;
    private Integer otp;

    public EmailMessage(String to, Integer otp) {
        this.to = to;
        this.otp = otp;
    }

    public String getTo() {
        return to;
    }

    public Integer getOtp() {
        return otp;
    }
}
