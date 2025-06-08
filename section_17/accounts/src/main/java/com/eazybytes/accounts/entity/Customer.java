package com.eazybytes.accounts.entity;

import com.eazybytes.accounts.dto.CustomerDto;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@ToString
@NoArgsConstructor
public class Customer extends AuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerId;
    private String name;
    private String email;
    private String mobileNumber;

    private Customer(Long customerId, String name, String email, String mobileNumber) {
        this.customerId = customerId;
        this.name = name;
        this.email = email;
        this.mobileNumber = mobileNumber;
    }

    public Customer updateFrom(CustomerDto customerDto) {
        Customer newCustomer = new Customer(
                customerId,
                customerDto.name() != null
                        ? customerDto.name()
                        : this.name,
                customerDto.email() != null
                        ? customerDto.email()
                        : this.email,
                customerDto.mobileNumber() != null
                        ? customerDto.mobileNumber()
                        : this.mobileNumber
        );
        newCustomer.createdAt = this.createdAt;
        newCustomer.createdBy = this.createdBy;
        newCustomer.updatedAt = this.updatedAt;
        newCustomer.updatedBy = this.updatedBy;
        return newCustomer;
    }
}
