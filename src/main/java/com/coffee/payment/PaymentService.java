package com.coffee.payment;

public interface PaymentService {

    boolean processPayment(double amount, String paymentId);

}