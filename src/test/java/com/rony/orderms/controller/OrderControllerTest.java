package com.rony.orderms.controller;

import com.rony.orderms.factory.OrderResponseFactory;
import com.rony.orderms.service.OrderService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatusCode;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {
    
    @Mock
    OrderService orderService;
    
    @InjectMocks
    OrderController orderController;

    @Captor
    ArgumentCaptor<Long> customerIdCaptor;

    @Captor
    ArgumentCaptor<PageRequest> pageRequestCaptor;
    
    @Nested
    class ListOrders{

        @Test
        void shouldReturnHttpOk() {
            //ARRENGE - prepara todos os mocks para a execução do teste
            var customerId = 1L;
            var page = 0;
            var pageSize = 10;

            doReturn(OrderResponseFactory.buildWhitOneItem())
                    .when(orderService).findAllByCustomerId(anyLong(),any());

            doReturn(BigDecimal.valueOf(20.50))
                    .when(orderService).findTotalOnOrdersByCustomerId(anyLong());

            //ACT - executar o metodo que será testado
            var response = orderController.listOrders(customerId, page, pageSize);

            //ASSERT - verificar se o resultado é o esperado
            assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        }

        @Test
        void shouldPassCorrectParamtersToService() {
            //ARRENGE - prepara todos os mocks para a execução do teste
            var customerId = 1L;
            var page = 0;
            var pageSize = 10;

            doReturn(OrderResponseFactory.buildWhitOneItem())
                    .when(orderService).findAllByCustomerId(customerIdCaptor.capture(),pageRequestCaptor.capture());

            doReturn(BigDecimal.valueOf(20.50))
                    .when(orderService).findTotalOnOrdersByCustomerId(customerIdCaptor.capture());

            //ACT - executar o metodo que será testado
            var response = orderController.listOrders(customerId, page, pageSize);

            //ASSERT - verificar se o resultado é o esperado
            assertEquals(2, customerIdCaptor.getAllValues().size());
            assertEquals(customerId, customerIdCaptor.getAllValues().get(0));
            assertEquals(customerId, customerIdCaptor.getAllValues().get(1));
            assertEquals(page,pageRequestCaptor.getValue().getPageNumber());
            assertEquals(pageSize,pageRequestCaptor.getValue().getPageSize());
        }

        @Test
        void shouldReturnResponseBodyCorrectly() {
            //ARRENGE - prepara todos os mocks para a execução do teste
            var customerId = 1L;
            var page = 0;
            var pageSize = 10;
            var totalOnOrders = BigDecimal.valueOf(20.50);
            var pagination = OrderResponseFactory.buildWhitOneItem();

            doReturn(pagination)
                    .when(orderService).findAllByCustomerId(anyLong(),any());

            doReturn(totalOnOrders)
                    .when(orderService).findTotalOnOrdersByCustomerId(anyLong());

            //ACT - executar o metodo que será testado
            var response = orderController.listOrders(customerId, page, pageSize);

            //ASSERT - verificar se o resultado é o esperado
            assertNotNull(response);
            assertNotNull(response.getBody());
            assertNotNull(response.getBody().data());
            assertNotNull(response.getBody().pagination());
            assertNotNull(response.getBody().sumary());

            assertEquals(totalOnOrders, response.getBody().sumary().get("totalOnOrders"));
            assertEquals(pagination.getTotalElements(), response.getBody().pagination().totalElements());
            assertEquals(pagination.getTotalPages(), response.getBody().pagination().totalPages());
            assertEquals(pagination.getNumber(), response.getBody().pagination().page());
            assertEquals(pagination.getSize(), response.getBody().pagination().pageSize());

            assertEquals(pagination.getContent(),response.getBody().data());
        }
    }
}