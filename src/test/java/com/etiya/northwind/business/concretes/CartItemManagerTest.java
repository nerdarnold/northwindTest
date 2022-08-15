package com.etiya.northwind.business.concretes;

import com.etiya.northwind.TestSupport;
import com.etiya.northwind.business.requests.cartItemRequests.CreateCartItemRequest;
import com.etiya.northwind.business.requests.cartItemRequests.UpdateCartItemRequest;
import com.etiya.northwind.business.responses.cartItems.CartItemListResponse;
import com.etiya.northwind.core.exceptions.BusinessException;
import com.etiya.northwind.core.mapping.ModelMapperManager;
import com.etiya.northwind.core.mapping.ModelMapperService;
import com.etiya.northwind.core.results.DataResult;
import com.etiya.northwind.core.results.SuccessDataResult;
import com.etiya.northwind.dataAccess.abstracts.CartItemsRepository;
import com.etiya.northwind.entities.concretes.CartItem;
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

class CartItemManagerTest {

    ModelMapperService modelMapperService;
    CartItemsRepository mockCartItemsRepository;
    CartItemManager cartItemManager;

    @BeforeEach
    void setup() {

        modelMapperService = new ModelMapperManager(new ModelMapper());
        mockCartItemsRepository = mock(CartItemsRepository.class);
        cartItemManager = new CartItemManager(mockCartItemsRepository, modelMapperService);
    }

//    @Test
//    void addTest_whenCartItemExists_dontAllowDuplicates() {

//        CreateCartItemRequest cartItemRequest = CreateCartItemRequest.builder().cartItemId(1).build();
//
//        when(mockCartItemsRepository.existsById(cartItemRequest.getCartItemId())).thenReturn(true);
//
//        Assertions.assertThrows(BusinessException.class, ()-> cartItemManager.add(cartItemRequest));
//
//    }

    @Test
    void updateTest_whenCartItemDoesntExist_throwBusinessException(){
        CartItem cartItem = CartItem.builder().cartItemId(1).build();
        UpdateCartItemRequest updateCartItemRequest = this.modelMapperService.forRequest().map(cartItem, UpdateCartItemRequest.class);
        when(mockCartItemsRepository.existsById(cartItem.getCartItemId())).thenReturn(false);
        Assertions.assertThrows(BusinessException.class, ()-> cartItemManager.update(cartItem.getCartItemId(),updateCartItemRequest));
    }

    @Test
    void updateTest_whenCartItemIsUpdated_assertDataIntegrity() {
        CartItem cartItem = CartItem.builder().cartItemId(1).build();
        when(mockCartItemsRepository.findById(cartItem.getCartItemId())).thenReturn(Optional.of(cartItem));
        UpdateCartItemRequest updateRequest = this.modelMapperService.forRequest().map(cartItem, UpdateCartItemRequest.class);
        cartItemManager.update(cartItem.getCartItemId(),updateRequest);

        CartItemListResponse expected = this.modelMapperService.forResponse().map(cartItem, CartItemListResponse.class);
        CartItemListResponse actual = cartItemManager.getById(cartItem.getCartItemId()).getData();

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void getAllUsersTest_itShouldReturnUserDtoList() {
        List<CartItem> cartItemList = TestSupport.generateCartItems();
        when(mockCartItemsRepository.findAll()).thenReturn(cartItemList);

        List<CartItemListResponse> cartItemResponseList =
                cartItemList.stream()
                        .map(c -> this.modelMapperService
                                .forResponse()
                                .map(c, CartItemListResponse.class))
                        .collect(Collectors.toList());
        cartItemResponseList.forEach(cartItemListResponse -> cartItemListResponse.setCartId(1));
        DataResult<List<CartItemListResponse>> expected = new SuccessDataResult<>(cartItemResponseList);

        DataResult<List<CartItemListResponse>> actual = cartItemManager.getAll();

        Assertions.assertEquals(expected,actual);
        Mockito.verify(mockCartItemsRepository).findAll();

    }

    @Test
    void getByIdTest_whenCartItemIdDoesntExist_shouldThrowBusinessException() {

        when(mockCartItemsRepository.findById(1)).thenReturn(Optional.empty());
        Assertions.assertThrows(BusinessException.class, ()-> cartItemManager.getById(1));
    }

    @Test
    void getByIdTest_whenCartItemIdExists_shouldReturnCartItemWithThatId() {
        CartItem cartItem = CartItem.builder().cartItemId(1).build();
        when(mockCartItemsRepository.findById(cartItem.getCartItemId())).thenReturn(Optional.of(cartItem));
        CartItemListResponse cartItemListResponse = this.modelMapperService.forResponse().map(cartItem, CartItemListResponse.class);
        DataResult<CartItemListResponse> expected = new SuccessDataResult<>(cartItemListResponse);

        DataResult<CartItemListResponse> actual = cartItemManager.getById(cartItem.getCartItemId());

        Assertions.assertEquals(expected, actual);
    }
}