package com.eazybytes.accounts.service.impl;

import com.eazybytes.accounts.constants.AccountsConstants;
import com.eazybytes.accounts.dto.AccountsDto;
import com.eazybytes.accounts.dto.CustomerAccountsDto;
import com.eazybytes.accounts.dto.CustomerDto;
import com.eazybytes.accounts.entity.Accounts;
import com.eazybytes.accounts.entity.Customer;
import com.eazybytes.accounts.exception.CustomerAlreadyExistsException;
import com.eazybytes.accounts.exception.ResourceNoFoundException;
import com.eazybytes.accounts.mapper.AccountsMapper;
import com.eazybytes.accounts.mapper.CustomerAccountsMapper;
import com.eazybytes.accounts.mapper.CustomerMapper;
import com.eazybytes.accounts.repository.AccountsRepository;
import com.eazybytes.accounts.repository.CustomerRepository;
import com.eazybytes.accounts.service.IAccountsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@AllArgsConstructor
public class AccountsServiceImpl implements IAccountsService {

    private final CustomerRepository customerRepository;
    private final AccountsRepository accountsRepository;

    @Override
    public void createAccount(CustomerDto customerDto) {
        if (customerRepository.existsCustomerByMobileNumber(customerDto.mobileNumber())) {
            throw new CustomerAlreadyExistsException("Customer with mobile number %s has already exist. ".formatted(customerDto.mobileNumber()));
        }
        var customer = CustomerMapper.mapToCustomer(customerDto, new Customer());
        Customer savedCustomer = customerRepository.save(customer);
        accountsRepository.save(createNewAccount(savedCustomer));
    }

    private Accounts createNewAccount(Customer customer) {
        long randomAccountNumber = 1_000_000_000L + new Random().nextInt(900_000_000);
        return new Accounts(randomAccountNumber, customer.getCustomerId(), AccountsConstants.SAVINGS, AccountsConstants.ADDRESS);
    }

    @Override
    public CustomerAccountsDto fetchAccount(String mobileNumber) {
        var customer = customerRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new ResourceNoFoundException("Customer", "mobileNumber", mobileNumber));
        var account = accountsRepository.findByCustomerId(customer.getCustomerId())
                .orElseThrow(() -> new ResourceNoFoundException("Accounts", "customerId", customer.getCustomerId().toString()));

        return CustomerAccountsMapper.mapToCustomerAccountsDto(customer, account);
    }

    @Override
    public boolean updateAccount(CustomerAccountsDto customerAccountsDto) {
        AccountsDto accountsDto = customerAccountsDto.accountsDto();
        if (accountsDto.accountNumber() == null) {
            return false;
        }
        var oldAccounts = accountsRepository.findById(accountsDto.accountNumber())
                .orElseThrow(() -> new ResourceNoFoundException("Accounts", "accountNumber", accountsDto.accountNumber().toString()));
        var newAccounts = AccountsMapper.mapToAccounts(customerAccountsDto.extructAccountsDto(), oldAccounts);
        accountsRepository.save(newAccounts);

        var oldCustomer = customerRepository.findById(newAccounts.getCustomerId())
                .orElseThrow(() -> new ResourceNoFoundException("Customer", "customerId", newAccounts.getCustomerId().toString()));
        var newCustomer = CustomerMapper.mapToCustomer(customerAccountsDto.extructCustomerDto(), oldCustomer);
        customerRepository.save(newCustomer);

        return true;
    }

    @Override
    public boolean deleteAccount(String mobileNumber) {
        var customer = customerRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new ResourceNoFoundException("Customer", "mobileNumber", mobileNumber));

        accountsRepository.deleteByCustomerId(customer.getCustomerId() );
        customerRepository.deleteById(customer.getCustomerId());

        return true;
    }
}
