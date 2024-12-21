package com.abenezer.personalfinancetracker.customer;

import com.abenezer.personalfinancetracker.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    private final PasswordEncoder passwordEncoder;
    private final CustomerRepository customerRepository;

    public Customer getCustomerById(Long id) {
        if (id <=0 ) throw new ResourceNotFoundException("customer id is invalid");

        return customerRepository.findById(id). orElse(null);
    }

    @Autowired
    public CustomerService(PasswordEncoder passwordEncoder, CustomerRepository customerRepository) {
        this.passwordEncoder = passwordEncoder;
        this.customerRepository = customerRepository;
    }

    public void registerCustomer(Customer customer) {
        String encodedPassword = passwordEncoder.encode(customer.getPassword());
        customer.setPassword(encodedPassword); // Set the encoded password
        customerRepository.save(customer); // Save the customer with encoded password
    }
}
