package com.eazybytes.accounts.mapper;

import com.eazybytes.accounts.dto.CustomerAccountsDto;
import com.eazybytes.accounts.entity.Accounts;
import com.eazybytes.accounts.entity.Customer;

public class CustomerAccountsMapper {

    public static CustomerAccountsDto mapToCustomerAccountsDto(Customer customer, Accounts accounts) {
        return CustomerAccountsDto.builder()
                .customerDto(CustomerMapper.mapToCustomerDto(customer))
                .accountsDto(AccountsMapper.mapToAccountsDto(accounts))
                .build();
    }
}
