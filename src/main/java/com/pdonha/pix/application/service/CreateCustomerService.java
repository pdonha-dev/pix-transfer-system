package com.pdonha.pix.application.service;

import com.pdonha.pix.application.dto.command.CreateCustomerCommand;
import com.pdonha.pix.application.dto.result.CustomerResult;
import com.pdonha.pix.domain.model.Customer;
import com.pdonha.pix.domain.port.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CreateCustomerService {
    private final CustomerRepository customerRepository;

    public CreateCustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional
    public CustomerResult execute(CreateCustomerCommand command) {
        Customer customer = new Customer(UUID.randomUUID(), command.name(), command.cpf());
        customerRepository.save(customer);
        return new CustomerResult(customer.getId(), customer.getName(), customer.getCpf());
    }
}
