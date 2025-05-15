package com.eazybytes.accounts.mapper;

import com.eazybytes.accounts.dto.AccountsDto;
import com.eazybytes.accounts.entity.Accounts;

public class AccountsMapper {

    public static AccountsDto mapToAccountsDto(Accounts accounts) {
        return new AccountsDto(accounts.getAccountNumber(), accounts.getAccountType(), accounts.getBranchAddress());
    }

    public static Accounts mapToAccounts(AccountsDto accountsDto, Accounts accounts) {
        return accounts.updateFrom(accountsDto);
    }
}
