package com.pdonha.pix.adapter.out.persistence.converter;

import com.pdonha.pix.adapter.out.persistence.entity.CustomerJpaEntity;
import com.pdonha.pix.domain.model.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerConverter {
    public Customer toDomain(CustomerJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Customer(entity.getId(), entity.getName(), entity.getCpf());
    }

    public CustomerJpaEntity toJpaEntity(Customer customer) {
        if (customer == null) {
            return null;
        }
        CustomerJpaEntity entity = new CustomerJpaEntity();
        entity.setId(customer.getId());
        entity.setName(customer.getName());
        entity.setCpf(customer.getCpf());
        return entity;
    }
}
