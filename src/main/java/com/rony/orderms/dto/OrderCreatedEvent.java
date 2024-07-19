package com.rony.orderms.dto;

import com.rony.orderms.entity.OrderItem;

import java.util.List;

public record OrderCreatedEvent(
        Long codigoPedido,
        Long codigoCliente,
        List<OrderItemEvent> itens
) {
}
