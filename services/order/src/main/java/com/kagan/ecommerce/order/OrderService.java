package com.kagan.ecommerce.order;

import com.kagan.ecommerce.customer.CustomerClient;
import com.kagan.ecommerce.exception.BusinessException;
import com.kagan.ecommerce.orderline.OrderLineRequest;
import com.kagan.ecommerce.orderline.OrderLineService;
import com.kagan.ecommerce.product.ProductClient;
import com.kagan.ecommerce.product.PurchaseRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository repository;
    private final CustomerClient customerClient;
    private final ProductClient productClient;
    private final OrderMapper mapper;
    private final OrderLineService orderLineService;

    public Integer createdOrder(OrderRequest request) {
        var customer = this.customerClient.findCustomerById(request.customerId())
                .orElseThrow(() -> new BusinessException("Cannot create order:: No customer exists with the provided ID"));

        this.productClient.purchaseProducts(request.products());

        var order = this.repository.save(mapper.toOrder(request));

        for(PurchaseRequest purchaseRequest: request.products()) {
            orderLineService.saveOrderLine(
                    new OrderLineRequest(
                            null,
                            order.getId(),
                            purchaseRequest.productId(),
                            purchaseRequest.quantity()
                    )
            );
        }

        //todo start payment process

        // send the order confirmation --> notification-ms (kafka)
        return null;
    }
}
