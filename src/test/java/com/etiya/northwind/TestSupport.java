package com.etiya.northwind;

import com.etiya.northwind.business.responses.customers.CustomerListResponse;
import com.etiya.northwind.entities.concretes.*;
import org.aspectj.weaver.ast.Or;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TestSupport {

    public static List<Customer> generateCustomers(){

        return IntStream.rangeClosed(1,5)
                .mapToObj(i -> Customer
                        .builder()
                        .customerId(String.valueOf(i))
                        .build())
                .collect(Collectors.toList());

    }

    public static List<Employee> generateEmployees(){

        return IntStream.rangeClosed(1,5)
                .mapToObj(i -> Employee
                        .builder()
                        .employeeId(i)
                        .build())
                .collect(Collectors.toList());

    }

    public static List<Supplier> generateSuppliers(){

        return IntStream.rangeClosed(1,5)
                .mapToObj(i -> Supplier
                        .builder()
                        .supplierId(i)
                        .build())
                .collect(Collectors.toList());

    }

    public static List<Product> generateProducts(){

        return IntStream.rangeClosed(1,5)
                .mapToObj(i -> Product
                        .builder()
                        .productId(i)
                        .build())
                .collect(Collectors.toList());

    }

    public static List<Cart> generateCarts(){

        return IntStream.rangeClosed(1,5)
                .mapToObj(i -> Cart
                        .builder()
                        .cartId(i)
                        .build())
                .collect(Collectors.toList());

    }

    public static List<Category> generateCategories(){
        return IntStream.rangeClosed(1,5)
                .mapToObj(i -> Category
                        .builder()
                        .categoryId(i)
                        .build())
                .collect(Collectors.toList());
    }

    public static Employee generateEmployee(){
        return Employee.builder().employeeId(1).firstName("TEST").lastName("TEST").build();
    }
    public static List<CartItem> generateCartItems(){
        Cart cart = Cart.builder().cartId(1).build();
        return IntStream.rangeClosed(1,5)
                .mapToObj(i -> CartItem
                        .builder()
                        .cart(cart)
                        .cartItemId(i)
                        .build())
                .collect(Collectors.toList());
    }
    public static List<Order> generateOrders(){
        Customer customer = Customer.builder().customerId("Ata Mert").build();
        Employee employee = Employee.builder().employeeId(1).build();
        employee.setFirstName("Ata");
        employee.setLastName("Baba");
        return IntStream.rangeClosed(1,5)
                .mapToObj(i -> Order
                        .builder()
                        .customer(customer)
                        .employee(employee)
                        .orderId(i)
                        .build())
                .collect(Collectors.toList());
    }
    public static List<OrderDetails> generateOrderDetails(){
        Order order = Order.builder().orderId(1).build();
        Product product = Product.builder().productId(1).build();
        return IntStream.rangeClosed(1,5)
                .mapToObj(i -> OrderDetails
                        .builder()
                        .order(order)
                        .orderId(order.getOrderId())
                        .productId(product.getProductId())
                        .product(product)
                        .build())
                .collect(Collectors.toList());
    }



  /*  public static List<CustomerListResponse> generateCustomerListResponseList(List<Customer> customerList){

        return customerList.stream()
                .map(customer -> CustomerListResponse
                        .builder()
                        .customerId(customer.getCustomerId())
                        .build())
                .collect(Collectors.toList());
    }*/
}
