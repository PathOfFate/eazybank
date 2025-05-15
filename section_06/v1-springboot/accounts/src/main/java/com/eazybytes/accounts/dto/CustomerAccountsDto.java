package com.eazybytes.accounts.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(
        name = "CustomerAccounts",
        description = "Schema to hold Customer and Account information"
)
@Builder
public record CustomerAccountsDto(
        CustomerDto customerDto,
        AccountsDto accountsDto
) {
    public CustomerDto extructCustomerDto() {
        return customerDto;
    }

    public AccountsDto extructAccountsDto() {
        return accountsDto;
    }
}
