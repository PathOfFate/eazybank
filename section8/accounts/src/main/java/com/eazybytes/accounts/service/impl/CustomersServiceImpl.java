package com.eazybytes.accounts.service.impl;

import com.eazybytes.accounts.dto.CustomerDetailsDto;
import com.eazybytes.accounts.exception.ResourceNoFoundException;
import com.eazybytes.accounts.mapper.AccountsMapper;
import com.eazybytes.accounts.mapper.CustomerMapper;
import com.eazybytes.accounts.repository.AccountsRepository;
import com.eazybytes.accounts.repository.CustomerRepository;
import com.eazybytes.accounts.service.ICustomersService;
import com.eazybytes.accounts.service.client.CardsFeignClient;
import com.eazybytes.accounts.service.client.LoansFeignClient;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomersServiceImpl implements ICustomersService {

    private final AccountsRepository accountsRepository;
    private final CustomerRepository customerRepository;
    private final CardsFeignClient cardsClient;
    private final LoansFeignClient loansClient;

    @Override
    public CustomerDetailsDto fetchCustomerDetails(String mobileNumber) {
        var customer = customerRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new ResourceNoFoundException("Customer", "mobileNumber", mobileNumber));
        var accounts = accountsRepository.findByCustomerId(customer.getCustomerId())
                .orElseThrow(() -> new ResourceNoFoundException("Accounts", "customerId", customer.getCustomerId().toString()));

        var cards = cardsClient.fetchCardDetail(mobileNumber);
        var loans = loansClient.fetchLoanDetails(mobileNumber);

        return CustomerMapper.mapToCustomerDetailsDto(
                CustomerMapper.mapToCustomerDto(customer),
                AccountsMapper.mapToAccountsDto(accounts),
                cards.getBody(),
                loans.getBody()
        );
    }
}
