package com.eazybytes.accounts.service.impl;

import com.eazybytes.accounts.constants.AccountsConstants;
import com.eazybytes.accounts.dto.AccountsDto;
import com.eazybytes.accounts.dto.AccountsMsgDto;
import com.eazybytes.accounts.dto.CustomerAccountsDto;
import com.eazybytes.accounts.dto.CustomerDto;
import com.eazybytes.accounts.entity.Accounts;
import com.eazybytes.accounts.entity.Customer;
import com.eazybytes.accounts.exception.CustomerAlreadyExistsException;
import com.eazybytes.accounts.exception.ResourceNotFoundException;
import com.eazybytes.accounts.mapper.AccountsMapper;
import com.eazybytes.accounts.mapper.CustomerAccountsMapper;
import com.eazybytes.accounts.mapper.CustomerMapper;
import com.eazybytes.accounts.repository.AccountsRepository;
import com.eazybytes.accounts.repository.CustomerRepository;
import com.eazybytes.accounts.service.IAccountsService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@AllArgsConstructor
public class AccountsServiceImpl implements IAccountsService {

    private static final Logger logger = LoggerFactory.getLogger(AccountsServiceImpl.class);

    private final CustomerRepository customerRepository;
    private final AccountsRepository accountsRepository;
    private final StreamBridge streamBridge;

    @Override
    public void createAccount(CustomerDto customerDto) {
        if (customerRepository.existsCustomerByMobileNumber(customerDto.mobileNumber())) {
            throw new CustomerAlreadyExistsException("Customer with mobile number %s has already exist. ".formatted(customerDto.mobileNumber()));
        }
        var customer = CustomerMapper.mapToCustomer(customerDto, new Customer());
        Customer savedCustomer = customerRepository.save(customer);
        Accounts savedAccount = accountsRepository.save(createNewAccount(savedCustomer));
        sendCommunication(savedAccount, savedCustomer);
    }

    private void sendCommunication(Accounts accounts, Customer customer) {
        var accountsMsgDto = new AccountsMsgDto(
                accounts.getAccountNumber(),
                customer.getName(),
                customer.getEmail(),
                customer.getMobileNumber()
        );

        logger.info("Sending Communication request for the details: {}", accountsMsgDto);
        var result = streamBridge.send("sendCommunication-out-0", accountsMsgDto);
        logger.info("Is the Communication request successfully triggered? : {}", result);
    }

    private Accounts createNewAccount(Customer customer) {
        long randomAccountNumber = 1_000_000_000L + new Random().nextInt(900_000_000);
        return new Accounts(randomAccountNumber, customer.getCustomerId(), AccountsConstants.SAVINGS, AccountsConstants.ADDRESS, null);
    }

    @Override
    public CustomerAccountsDto fetchAccount(String mobileNumber) {
        var customer = customerRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber));
        var account = accountsRepository.findByCustomerId(customer.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Accounts", "customerId", customer.getCustomerId().toString()));

        return CustomerAccountsMapper.mapToCustomerAccountsDto(customer, account);
    }

    @Override
    public boolean updateAccount(CustomerAccountsDto customerAccountsDto) {
        AccountsDto accountsDto = customerAccountsDto.accountsDto();
        if (accountsDto.accountNumber() == null) {
            return false;
        }
        var oldAccounts = accountsRepository.findById(accountsDto.accountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Accounts", "accountNumber", accountsDto.accountNumber().toString()));
        var newAccounts = AccountsMapper.mapToAccounts(customerAccountsDto.extructAccountsDto(), oldAccounts);
        accountsRepository.save(newAccounts);

        var oldCustomer = customerRepository.findById(newAccounts.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "customerId", newAccounts.getCustomerId().toString()));
        var newCustomer = CustomerMapper.mapToCustomer(customerAccountsDto.extructCustomerDto(), oldCustomer);
        customerRepository.save(newCustomer);

        return true;
    }

    @Override
    public boolean deleteAccount(String mobileNumber) {
        var customer = customerRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber));

        accountsRepository.deleteByCustomerId(customer.getCustomerId() );
        customerRepository.deleteById(customer.getCustomerId());

        return true;
    }

    @Override
    public boolean updateCommunicationStatus(Long accountNumber) {
        boolean isUpdated = false;
        if (accountNumber != null) {
            Accounts accounts = accountsRepository.findById(accountNumber)
                    .orElseThrow(() -> new ResourceNotFoundException("Accounts", "accountNumber", accountNumber.toString()));
            Accounts updatedAccounts = accounts.updateConnectionSw(true);
            accountsRepository.save(updatedAccounts);
            isUpdated = true;
        }
        return isUpdated;
    }
}
