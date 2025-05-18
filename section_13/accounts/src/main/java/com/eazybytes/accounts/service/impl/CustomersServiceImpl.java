package com.eazybytes.accounts.service.impl;

import com.eazybytes.accounts.dto.CardsDto;
import com.eazybytes.accounts.dto.CustomerDetailsDto;
import com.eazybytes.accounts.dto.LoansDto;
import com.eazybytes.accounts.exception.ResourceNotFoundException;
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
    public CustomerDetailsDto fetchCustomerDetails(String correlationId, String mobileNumber) {
        var customer = customerRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber));
        var accounts = accountsRepository.findByCustomerId(customer.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Accounts", "customerId", customer.getCustomerId().toString()));

        var cardsResponse = cardsClient.fetchCardDetail(correlationId, mobileNumber);
        CardsDto cards = null;
        if (cardsResponse != null) {
            cards = cardsResponse.getBody();
        }

        var loansResponse = loansClient.fetchLoanDetails(correlationId, mobileNumber);
        LoansDto loans = null;
        if (loansResponse != null) {
            loans = loansResponse.getBody();
        }

        return CustomerMapper.mapToCustomerDetailsDto(
                CustomerMapper.mapToCustomerDto(customer),
                AccountsMapper.mapToAccountsDto(accounts),
                cards,
                loans
        );
    }
}
