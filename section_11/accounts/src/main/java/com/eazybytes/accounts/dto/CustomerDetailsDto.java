package com.eazybytes.accounts.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(
        name = "CustomerDetails",
        description = "Schema to hold Customer, Accounts, Cards and Loans information"
)
public record CustomerDetailsDto(
        @Schema(
                description = "Name of the customer", example = "Eazy Bytes"
        )
        @NotEmpty(message = "Name can not be null or empty.")
        @Size(min = 5, max = 30, message = "The length of the customer name should be between 5 and 30.")
        String name,
        @Schema(
                description = "Email address of the customer", example = "tutor@eazybytes.com"
        )
        @NotEmpty(message = "Email address can not be null or empty.")
        @Email(message = "Email address should be a valid value.")
        String email,
        @Schema(
                description = "Mobile Number of the customer", example = "9345432123"
        )
        @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be 10 digits.")
        String mobileNumber,
        @Schema(description = "Account details of the customer")
        AccountsDto accountsDto,
        @Schema(description = "Card details of the customer")
        CardsDto cardsDto,
        @Schema(description = "Loan details of the customer")
        LoansDto loansDto
) {
}
