package com.pdonha.pix.adapter.out.persistence.repository;

import com.pdonha.pix.adapter.out.persistence.converter.CustomerConverter;
import com.pdonha.pix.adapter.out.persistence.entity.CustomerJpaEntity;
import com.pdonha.pix.domain.model.Customer;
import com.pdonha.pix.domain.port.CustomerRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

interface SpringDataCustomerRepository extends JpaRepository<CustomerJpaEntity, UUID> {
}

@Component
public class JpaCustomerRepository implements CustomerRepository {
    private final SpringDataCustomerRepository springRepository;
    private final CustomerConverter converter;

    public JpaCustomerRepository(SpringDataCustomerRepository springRepository,
                                 CustomerConverter converter) {
        this.springRepository = springRepository;
        this.converter = converter;
    }

    @Override
    public Customer findById(UUID id) {
        return springRepository.findById(id).map(converter::toDomain).orElse(null);
    }

    @Override
    public void save(Customer customer) {
        springRepository.saveAndFlush(converter.toJpaEntity(customer));
    }
}
