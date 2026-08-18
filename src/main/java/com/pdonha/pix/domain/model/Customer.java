package com.pdonha.pix.domain.model;

import java.util.UUID;

public final class Customer {
    private final UUID id;
    private final String name;
    private final String cpf;


    public Customer(UUID id, String name, String cpf) {
        if (id == null) {
            throw new IllegalArgumentException("Id não pode ser nulo");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Nome não pode ser vazio");
        }
        if (cpf == null || !cpf.matches("\\d{11}")) {
            throw new IllegalArgumentException("CPF inválido");
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
