package com.pdonha.pix.domain.port;

import com.pdonha.pix.domain.model.Customer;

import java.util.UUID;

public interface CustomerRepository {
    Customer findById(UUID id);
    void save(Customer customer);
}
