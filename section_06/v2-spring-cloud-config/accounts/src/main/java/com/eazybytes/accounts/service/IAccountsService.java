package com.eazybytes.accounts.service;

import com.eazybytes.accounts.dto.CustomerAccountsDto;
import com.eazybytes.accounts.dto.CustomerDto;

public interface IAccountsService {

    void createAccount(CustomerDto customerDto);

    CustomerAccountsDto fetchAccount(String mobileNumber);

    boolean updateAccount(CustomerAccountsDto customerDto);

    boolean deleteAccount(String mobileNumber);
}
