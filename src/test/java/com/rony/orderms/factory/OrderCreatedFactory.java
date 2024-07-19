package com.rony.orderms.factory;

import com.rony.orderms.dto.OrderCreatedEvent;
import com.rony.orderms.dto.OrderItemEvent;

import java.math.BigDecimal;
import java.util.List;

public class OrderCreatedFactory {

    public static OrderCreatedEvent buildWithOneItem(){

        var itens = new OrderItemEvent("notebook", 1, BigDecimal.valueOf(1000.0));

        var event = new OrderCreatedEvent(1L,2L, List.of(itens));

        return event;
    }

    public static OrderCreatedEvent buildWithTwoItens(){

        var item1 = new OrderItemEvent("notebook", 1, BigDecimal.valueOf(1000.0));
        var item2 = new OrderItemEvent("desktop", 1, BigDecimal.valueOf(2500.0));

        var event = new OrderCreatedEvent(1L,2L, List.of(item1, item2));

        return event;
    }
}
