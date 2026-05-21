package com.kagan.ecommerce.payment;

import com.kagan.ecommerce.customer.CustomerResponse;
import com.kagan.ecommerce.order.PaymentMethod;

import java.math.BigDecimal;

public record PaymentRequest(
        BigDecimal amount,
        PaymentMethod paymentMethod,
        Integer orderId,
        String OrderReference,
        CustomerResponse customer
) {
}
