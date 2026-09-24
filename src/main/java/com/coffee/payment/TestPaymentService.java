package com.coffee.payment;

public class TestPaymentService implements PaymentService {

    @Override
    public boolean processPayment(double amount, String paymentId) {

        System.out.println("TEST PAYMENT");
        System.out.println("Amount: ₹" + amount);
        System.out.println("Payment ID: " + paymentId);

        // Simulated successful payment
        return true;
    }
}