package com.rony.orderms.service;

import com.rony.orderms.entity.OrderEntity;
import com.rony.orderms.factory.OrderCreatedFactory;
import com.rony.orderms.factory.OrderEntityFactory;
import com.rony.orderms.repository.OrderRepository;
import org.bson.Document;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    
    @Mock
    OrderRepository orderRepository;
    
    @Mock
    MongoTemplate mongoTemplate;
    
    @InjectMocks
    OrderService orderService;

    @Captor
    ArgumentCaptor<OrderEntity> orderEntityArgumentCaptor;

    @Captor
    ArgumentCaptor<Aggregation> aggregationArgumentCaptor;
    
    @Nested
    class Save{

        @Test
        void shouldCallRepositorySave() {
            //ARRANGE
            var event = OrderCreatedFactory.buildWithOneItem();

            //ACT
            orderService.save(event);

            //ASSERT
            verify(orderRepository,times(1)).save(any());
        }

        @Test
        void shouldMapEventToEntityWithSuccess() {
            //ARRANGE
            var event = OrderCreatedFactory.buildWithOneItem();

            //ACT
            orderService.save(event);

            //ASSERT
            verify(orderRepository,times(1)).save(orderEntityArgumentCaptor.capture());
            var orderEntity = orderEntityArgumentCaptor.getValue();

            assertEquals(event.codigoPedido(), orderEntity.getOrderId());
            assertEquals(event.codigoCliente(), orderEntity.getCustomerId());
            assertNotNull(orderEntity.getTotal());
            assertEquals(event.itens().getFirst().produto(), orderEntity.getItems().getFirst().getProduct());
            assertEquals(event.itens().getFirst().preco(), orderEntity.getItems().getFirst().getPrice());
            assertEquals(event.itens().getFirst().quantidade(), orderEntity.getItems().getFirst().getQuantity());
        }

        @Test
        void shouldCalculateOrderTotalWithSuccess() {
            //ARRANGE
            var event = OrderCreatedFactory.buildWithTwoItens();
            var totalItem1 = event.itens().getFirst().preco().multiply(BigDecimal.valueOf(event.itens().getFirst().quantidade()));
            var totalItem2 = event.itens().getLast().preco().multiply(BigDecimal.valueOf(event.itens().getLast().quantidade()));
            var orderTotal = totalItem1.add(totalItem2);

            //ACT
            orderService.save(event);

            //ASSERT
            verify(orderRepository,times(1)).save(orderEntityArgumentCaptor.capture());
            var orderEntity = orderEntityArgumentCaptor.getValue();

            assertNotNull(orderEntity.getTotal());
            assertEquals(orderTotal, orderEntity.getTotal());

        }
    }

    @Nested
    class FindAllByCustomerId{
        @Test
        void shouldCallRepository() {
            //ARRANGE
            var customerId = 1L;
            var pageRequest = PageRequest.of(0, 10);
            doReturn(OrderEntityFactory.buildWithPage())
                    .when(orderRepository).findAllByCustomerId(eq(customerId), eq(pageRequest));

            //ACT
            var response = orderService.findAllByCustomerId(customerId, pageRequest);

            //ASSERT
            verify(orderRepository,times(1)).findAllByCustomerId(eq(customerId), eq(pageRequest));
        }

        @Test
        void shouldMapResponse() {
            //ARRANGE
            var customerId = 1L;
            var pageRequest = PageRequest.of(0, 10);
            var page = OrderEntityFactory.buildWithPage();
            doReturn(OrderEntityFactory.buildWithPage())
                    .when(orderRepository).findAllByCustomerId(anyLong(), any());


            //ACT
            var response = orderService.findAllByCustomerId(customerId, pageRequest);

            //ASSERT
            assertEquals(page.getTotalPages(), response.getTotalPages());
            assertEquals(page.getTotalElements(), response.getTotalElements());
            assertEquals(page.getSize(), response.getSize());
            assertEquals(page.getNumber(), response.getNumber());

            assertEquals(page.getContent().getFirst().getOrderId(), response.getContent().getFirst().orderId());
            assertEquals(page.getContent().getFirst().getCustomerId(), response.getContent().getFirst().customerId());
            assertEquals(page.getContent().getFirst().getTotal(), response.getContent().getFirst().total());
        }
    }

    @Nested
    class FindTotalOnOrdersByCustomerId{

        @Test
        void shouldCallMongoTemplate() {
            //ARRANGE
            var customerId = 1L;
            var totalExpected = BigDecimal.valueOf(1);
            var aggregationResults = mock(AggregationResults.class);
            doReturn(new Document("total", 1)).when(aggregationResults).getUniqueMappedResult();
            doReturn(aggregationResults).when(mongoTemplate).aggregate(any(Aggregation.class), anyString(), eq(Document.class));

            //ACT
            var total = orderService.findTotalOnOrdersByCustomerId(customerId);


            //ASSERT
            verify(mongoTemplate, times(1)).aggregate(any(Aggregation.class), anyString(), eq(Document.class));
            assertEquals(totalExpected,total);
        }

        @Test
        void shouldUseCorrectAggregation() {
            //ARRANGE
            var customerId = 1L;
            var totalExpected = BigDecimal.valueOf(1);
            var aggregationResults = mock(AggregationResults.class);
            doReturn(new Document("total", 1)).when(aggregationResults).getUniqueMappedResult();
            doReturn(aggregationResults).when(mongoTemplate).aggregate(aggregationArgumentCaptor.capture(), anyString(), eq(Document.class));

            //ACT
            var total = orderService.findTotalOnOrdersByCustomerId(customerId);


            //ASSERT
            var aggregation = aggregationArgumentCaptor.getValue();
            var aggregationExpected = newAggregation(
                    match(Criteria.where("customerId").is(customerId)),
                    group().sum("total").as("total")
            );

            assertEquals(aggregationExpected.toString(), aggregation.toString());
        }

        @Test
        void shouldQueryCorrectTable() {
            //ARRANGE
            var customerId = 1L;
            var totalExpected = BigDecimal.valueOf(1);
            var aggregationResults = mock(AggregationResults.class);
            doReturn(new Document("total", 1)).when(aggregationResults).getUniqueMappedResult();
            doReturn(aggregationResults).when(mongoTemplate).aggregate(any(Aggregation.class),  eq("tb_orders"), eq(Document.class));

            //ACT
            var total = orderService.findTotalOnOrdersByCustomerId(customerId);


            //ASSERT
            verify(mongoTemplate, times(1)).aggregate(any(Aggregation.class), eq("tb_orders"), eq(Document.class));
        }
    }

}