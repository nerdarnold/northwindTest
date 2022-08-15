package com.etiya.northwind.business.concretes;

import com.etiya.northwind.TestSupport;
import com.etiya.northwind.business.requests.orderDetailRequests.CreateOrderDetailRequest;
import com.etiya.northwind.business.requests.orderDetailRequests.UpdateOrderDetailRequest;
import com.etiya.northwind.business.responses.orderDetails.OrderDetailsListResponse;
import com.etiya.northwind.core.exceptions.BusinessException;
import com.etiya.northwind.core.mapping.ModelMapperManager;
import com.etiya.northwind.core.mapping.ModelMapperService;
import com.etiya.northwind.core.results.DataResult;
import com.etiya.northwind.core.results.SuccessDataResult;
import com.etiya.northwind.dataAccess.abstracts.OrderDetailsRepository;
import com.etiya.northwind.entities.concretes.Order;
import com.etiya.northwind.entities.concretes.OrderDetails;
import com.etiya.northwind.entities.concretes.OrderDetailsId;
import com.etiya.northwind.entities.concretes.Product;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

class OrderDetailsManagerTest {

    ModelMapperService modelMapperService;
    OrderDetailsRepository mockOrderDetailsRepository;
    OrderDetailsManager orderDetailsManager;

    @BeforeEach
    void setup() {

        modelMapperService = new ModelMapperManager(new ModelMapper());
        mockOrderDetailsRepository = mock(OrderDetailsRepository.class);
        orderDetailsManager = new OrderDetailsManager(mockOrderDetailsRepository, modelMapperService);
    }

    @Test
    void addTest_whenOrderDetailsExists_dontAllowDuplicates() {

        CreateOrderDetailRequest orderDetailsRequest = new CreateOrderDetailRequest(
                1, 1,25,42,12
        );
        OrderDetailsId orderDetailsId = new OrderDetailsId();
        orderDetailsId.setOrderId(orderDetailsRequest.getOrderId());
        orderDetailsId.setProductId(orderDetailsRequest.getProductId());
        when(mockOrderDetailsRepository.existsById(orderDetailsId)).thenReturn(true);

        Assertions.assertThrows(BusinessException.class, ()-> orderDetailsManager.add(orderDetailsRequest));

    }

    @Test
    void updateTest_whenOrderDetailsDoesntExist_throwBusinessException(){
        Order order = Order.builder().orderId(1).build();
        Product product = Product.builder().productId(1).build();
        OrderDetails orderDetails = new OrderDetails(1, order,25,product,50,12,0);
        UpdateOrderDetailRequest updateOrderDetailsRequest = this.modelMapperService.forRequest().map(orderDetails, UpdateOrderDetailRequest.class);
        OrderDetailsId orderDetailsId = new OrderDetailsId();
        orderDetailsId.setOrderId(updateOrderDetailsRequest.getOrderId());
        orderDetailsId.setProductId(updateOrderDetailsRequest.getProductId());
        when(mockOrderDetailsRepository.existsById(orderDetailsId)).thenReturn(false);
        Assertions.assertThrows(BusinessException.class, ()-> orderDetailsManager.update(updateOrderDetailsRequest));
    }

    @Test
    void updateTest_whenOrderDetailsIsUpdated_assertDataIntegrity() {
        Order order = Order.builder().orderId(1).build();
        Product product = Product.builder().productId(1).build();
        OrderDetails orderDetails = new OrderDetails(1, order,25,product,50,12,0);
        UpdateOrderDetailRequest updateOrderDetailRequest = this.modelMapperService.forRequest().map(orderDetails, UpdateOrderDetailRequest.class);
        OrderDetailsId orderDetailsId = new OrderDetailsId();
        orderDetailsId.setOrderId(updateOrderDetailRequest.getOrderId());
        orderDetailsId.setProductId(updateOrderDetailRequest.getProductId());
        when(mockOrderDetailsRepository.existsById(orderDetailsId)).thenReturn(true);
        when(mockOrderDetailsRepository.findById(orderDetailsId)).thenReturn(Optional.of(orderDetails));

        orderDetailsManager.update(updateOrderDetailRequest);

        OrderDetailsListResponse expected = this.modelMapperService.forResponse().map(orderDetails, OrderDetailsListResponse.class);
        OrderDetailsListResponse actual = orderDetailsManager.getById(orderDetailsId).getData();

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void getAllUsersTest_itShouldReturnUserDtoList() {
        List<OrderDetails> orderDetailsList = TestSupport.generateOrderDetails();
        when(mockOrderDetailsRepository.findAll()).thenReturn(orderDetailsList);

        List<OrderDetailsListResponse> orderDetailsResponseList =
                orderDetailsList.stream()
                        .map(c -> this.modelMapperService
                                .forResponse()
                                .map(c, OrderDetailsListResponse.class))
                        .collect(Collectors.toList());
        DataResult<List<OrderDetailsListResponse>> expected = new SuccessDataResult<>(orderDetailsResponseList);

        DataResult<List<OrderDetailsListResponse>> actual = orderDetailsManager.getAll();

        Assertions.assertEquals(expected,actual);
        Mockito.verify(mockOrderDetailsRepository).findAll();

    }

    @Test
    void getByIdTest_whenOrderDetailsIdDoesntExist_shouldThrowBusinessException() {
        Order order = Order.builder().orderId(1).build();
        Product product = Product.builder().productId(1).build();
        OrderDetails orderDetails = new OrderDetails(1, order,25,product,50,12,0);
        OrderDetailsId orderDetailsId = new OrderDetailsId();
        orderDetailsId.setOrderId(order.getOrderId());
        orderDetailsId.setProductId(product.getProductId());
        when(mockOrderDetailsRepository.findById(orderDetailsId)).thenReturn(Optional.empty());
        Assertions.assertThrows(BusinessException.class, ()-> orderDetailsManager.getById(orderDetailsId));
    }

    @Test
    void getByIdTest_whenOrderDetailsIdExists_shouldReturnOrderDetailsWithThatId() {
        Order order = Order.builder().orderId(1).build();
        Product product = Product.builder().productId(1).build();
        OrderDetails orderDetails = new OrderDetails(1, order,25,product,50,12,0);
        OrderDetailsId orderDetailsId = new OrderDetailsId();
        orderDetailsId.setOrderId(order.getOrderId());
        orderDetailsId.setProductId(product.getProductId());
        when(mockOrderDetailsRepository.findById(orderDetailsId)).thenReturn(Optional.of(orderDetails));
        OrderDetailsListResponse orderDetailsListResponse = this.modelMapperService.forResponse().map(orderDetails, OrderDetailsListResponse.class);
        DataResult<OrderDetailsListResponse> expected = new SuccessDataResult<>(orderDetailsListResponse);

        DataResult<OrderDetailsListResponse> actual = orderDetailsManager.getById(orderDetailsId);

        Assertions.assertEquals(expected, actual);
    }
}