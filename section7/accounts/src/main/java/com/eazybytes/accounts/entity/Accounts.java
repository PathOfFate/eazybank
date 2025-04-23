package com.eazybytes.accounts.entity;

import com.eazybytes.accounts.dto.AccountsDto;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Accounts extends AuditEntity {
    @Id
    private Long accountNumber;
    private Long customerId;
    private String accountType;
    private String branchAddress;

    public Accounts updateFrom(AccountsDto accountsDto) {
        Accounts newAccounts = new Accounts(
                accountNumber,
                customerId,
                accountsDto.accountType() != null
                        ? accountsDto.accountType()
                        : this.accountType,

                accountsDto.branchAddress() != null
                        ? accountsDto.branchAddress()
                        : this.branchAddress
        );
        newAccounts.createdAt = this.createdAt;
        newAccounts.createdBy = this.createdBy;
        newAccounts.updatedAt = this.updatedAt;
        newAccounts.updatedBy = this.updatedBy;
        return newAccounts;
    }
}
