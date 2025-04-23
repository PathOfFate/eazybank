package com.eazybytes.accounts.mapper;

import com.eazybytes.accounts.dto.CustomerDto;
import com.eazybytes.accounts.entity.Customer;

public class CustomerMapper {

    public static CustomerDto mapToCustomerDto(Customer customer) {
        return new CustomerDto(customer.getName(), customer.getEmail(), customer.getMobileNumber());
    }

    public static Customer mapToCustomer(CustomerDto customerDto, Customer customer) {
        return customer.updateFrom(customerDto);
    }
}
