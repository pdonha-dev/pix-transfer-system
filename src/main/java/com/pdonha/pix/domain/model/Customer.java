package com.pdonha.pix.domain.model;

import com.pdonha.pix.domain.exception.InvalidCpfException;
import com.pdonha.pix.domain.exception.InvalidCustomerException;

import java.util.UUID;

public final class Customer {
    private final UUID id;
    private final String name;
    private final String cpf;

    public Customer(UUID id, String name, String cpf) {
        if (id == null) {
            throw new InvalidCustomerException("Customer ID cannot be null");
        }
        if (name == null || name.isBlank()) {
            throw new InvalidCustomerException("Customer name cannot be null or empty");
        }
        if (cpf == null || !cpf.matches("\\d{11}")) {
            throw new InvalidCpfException("CPF must contain exactly 11 digits", cpf);
        }

        this.id = id;
        this.name = name;
        this.cpf = cpf;
    }

    public String getName() {
        return name;
    }

    public UUID getId() {
        return id;
    }

    public String getCpf() {
        return cpf;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Customer customer = (Customer) obj;
        return id.equals(customer.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
