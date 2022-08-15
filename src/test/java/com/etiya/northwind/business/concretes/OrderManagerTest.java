package com.etiya.northwind.business.concretes;

import com.etiya.northwind.TestSupport;
import com.etiya.northwind.business.abstracts.CartItemService;
import com.etiya.northwind.business.abstracts.CartService;
import com.etiya.northwind.business.abstracts.CustomerService;
import com.etiya.northwind.business.abstracts.OrderDetailsService;
import com.etiya.northwind.business.requests.customerRequests.CreateCustomerRequest;
import com.etiya.northwind.business.requests.customerRequests.UpdateCustomerRequest;
import com.etiya.northwind.business.requests.orderRequests.CreateOrderRequest;
import com.etiya.northwind.business.requests.orderRequests.UpdateOrderRequest;
import com.etiya.northwind.business.responses.customers.CustomerListResponse;
import com.etiya.northwind.business.responses.orders.OrderListResponse;
import com.etiya.northwind.core.exceptions.BusinessException;
import com.etiya.northwind.core.mapping.ModelMapperManager;
import com.etiya.northwind.core.mapping.ModelMapperService;
import com.etiya.northwind.core.results.DataResult;
import com.etiya.northwind.core.results.SuccessDataResult;
import com.etiya.northwind.dataAccess.abstracts.*;
import com.etiya.northwind.entities.concretes.Customer;
import com.etiya.northwind.entities.concretes.Order;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

class OrderManagerTest {

    ModelMapperService modelMapperService;
    OrderRepository mockOrderRepository;
    CartRepository mockCartRepository;
    CustomerRepository mockCustomerRepository;
    CartItemsRepository mockCartItemsRepository;
    OrderDetailsRepository mockOrderDetailsRepository;
    OrderDetailsService orderDetailsService;
    CartService cartService;
    CartItemService cartItemService;
    CustomerService customerService;
    OrderManager orderManager;


    @BeforeEach
    void setup() {

        modelMapperService = new ModelMapperManager(new ModelMapper());
        mockOrderRepository = mock(OrderRepository.class);
        mockOrderDetailsRepository = mock(OrderDetailsRepository.class);
        orderDetailsService = new OrderDetailsManager(mockOrderDetailsRepository,modelMapperService);
        customerService = new CustomerManager(mockCustomerRepository,modelMapperService);
        cartService = new CartManager(mockCartRepository,modelMapperService,customerService);
        cartItemService = new CartItemManager(mockCartItemsRepository,modelMapperService);
        orderManager = new OrderManager(mockOrderRepository, modelMapperService,orderDetailsService,cartService,cartItemService);
    }

    @Test
    void addTest_whenOrderExists_dontAllowDuplicates() {

        CreateOrderRequest orderRequest = CreateOrderRequest.builder().orderId(1).build();

        when(mockOrderRepository.existsById(orderRequest.getOrderId())).thenReturn(true);

        Assertions.assertThrows(BusinessException.class, ()-> orderManager.add(orderRequest));

    }

    @Test
    void updateTest_whenOrderDoesntExist_throwBusinessException(){
        Order order =  Order.builder().orderId(1).build();
        UpdateOrderRequest updateOrderRequest = this.modelMapperService.forRequest().map(order, UpdateOrderRequest.class);
        when(mockOrderRepository.existsById(updateOrderRequest.getOrderId())).thenReturn(false);
        Assertions.assertThrows(BusinessException.class, ()-> orderManager.update(updateOrderRequest));
    }

    @Test
    void updateTest_whenOrderIsUpdated_assertDataIntegrity() {
        Order order =  Order.builder().orderId(1).build();
        when(mockOrderRepository.existsById(order.getOrderId())).thenReturn(true);
        when(mockOrderRepository.findById(order.getOrderId())).thenReturn(Optional.of(order));
        UpdateOrderRequest updateRequest = this.modelMapperService.forRequest().map(order, UpdateOrderRequest.class);
        orderManager.update(updateRequest);

        OrderListResponse expected = this.modelMapperService.forResponse().map(order, OrderListResponse.class);
        OrderListResponse actual = orderManager.getById(order.getOrderId()).getData();

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void getAllUsersTest_itShouldReturnUserDtoList() {
        List<Order> orderList = TestSupport.generateOrders();
        when(mockOrderRepository.findAll()).thenReturn(orderList);

        List<OrderListResponse> orderResponseList =
                orderList.stream()
                        .map(c -> this.modelMapperService
                                .forResponse()
                                .map(c, OrderListResponse.class))
                        .collect(Collectors.toList());
        orderResponseList.forEach(orderListResponse -> orderListResponse.setEmployeeName("Ata Baba"));
        DataResult<List<OrderListResponse>> expected = new SuccessDataResult<>(orderResponseList);

        DataResult<List<OrderListResponse>> actual = orderManager.getAll();

        Assertions.assertEquals(expected,actual);
        Mockito.verify(mockOrderRepository).findAll();

    }

    @Test
    void getByIdTest_whenOrderIdDoesntExist_shouldThrowBusinessException() {

        when(mockOrderRepository.findById(1)).thenReturn(Optional.empty());
        Assertions.assertThrows(BusinessException.class, ()-> orderManager.getById(1));
    }

    @Test
    void getByIdTest_whenOrderIdExists_shouldReturnOrderWithThatId() {
        Order order =  Order.builder().orderId(1).build();
        when(mockOrderRepository.findById(order.getOrderId())).thenReturn(Optional.of(order));
        OrderListResponse orderListResponse = this.modelMapperService.forResponse().map(order, OrderListResponse.class);
        DataResult<OrderListResponse> expected = new SuccessDataResult<>(orderListResponse);

        DataResult<OrderListResponse> actual = orderManager.getById(order.getOrderId());

        Assertions.assertEquals(expected, actual);
    }
}