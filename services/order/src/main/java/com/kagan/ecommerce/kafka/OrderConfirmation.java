package com.kagan.ecommerce.kafka;

import com.kagan.ecommerce.customer.CustomerResponse;
import com.kagan.ecommerce.order.PaymentMethod;
import com.kagan.ecommerce.product.PurchaseResponse;

import java.math.BigDecimal;
import java.util.List;

public record OrderConfirmation(
        String orderRefernce,
        BigDecimal totalAmount,
        PaymentMethod paymentMethod,
        CustomerResponse customer,
        List<PurchaseResponse> products

) {
}
