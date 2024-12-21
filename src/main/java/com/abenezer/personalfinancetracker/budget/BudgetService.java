package com.abenezer.personalfinancetracker.budget;

import com.abenezer.personalfinancetracker.budget.dto.BudgetRequestDTO;
import com.abenezer.personalfinancetracker.budget.dto.BudgetResponseDTO;
import com.abenezer.personalfinancetracker.customer.Customer;
import com.abenezer.personalfinancetracker.customer.CustomerService;
import com.abenezer.personalfinancetracker.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BudgetService {

    private BudgetRepository budgetRepository;
    private CustomerService customerService;

    public BudgetService(BudgetRepository budgetRepository, CustomerService customerService) {
        this.budgetRepository = budgetRepository;
        this.customerService = customerService;
    }

    public BudgetResponseDTO createBudget(BudgetRequestDTO budgetRequestDTO) {

        Customer customer = customerService.getCustomerById(budgetRequestDTO.getCustomerId());

        if (customer == null)
            throw new ResourceNotFoundException("Customer with id " + budgetRequestDTO.getCustomerId() + " not found");

        Budget budget = buildBudget(budgetRequestDTO, customer);

        Budget savedBudget = budgetRepository.save(budget);

        BudgetResponseDTO response = buildBudgetResponse(savedBudget);

        return response;
    }

    public BudgetResponseDTO getBudgetById(Long id) {
        Optional<Budget> budget = budgetRepository.findById(id);

        if (budget.isEmpty()) throw new ResourceNotFoundException("Budget with id " + id + " not found");

        return buildBudgetResponse(budget.get());
    }

    public List<BudgetResponseDTO> getAllBudgets() {
        List<Budget> all = budgetRepository.findAll();

        return all.stream().map(budget -> buildBudgetResponse(budget)).collect(Collectors.toList());
    }

    public BudgetResponseDTO updateBudget(Long id, BudgetRequestDTO budgetRequest) {

        Optional<Budget> budgetOptional = budgetRepository.findById(id);
        if (budgetOptional.isEmpty()) throw new ResourceNotFoundException("Budget with id " + id + " not found");

        Customer customer = customerService.getCustomerById(budgetRequest.getCustomerId());
        if (customer == null) throw new ResourceNotFoundException("Customer with id " + budgetRequest.getCustomerId() + " not found");

        Budget budget = budgetOptional.get();
        budget.setAmount(budgetRequest.getAmount());
        budget.setEndDate(budgetRequest.getEndDate());
        budget.setStartDate(budgetRequest.getStartDate());
        budget.setCustomer(customer);

        budgetRepository.save(budget);
        return buildBudgetResponse(budget);
    }

    private static Budget buildBudget(BudgetRequestDTO budgetRequestDTO, Customer customer) {
        Budget budget = new Budget();
        budget.setAmount(budget.getAmount());
        budget.setStartDate(budgetRequestDTO.getStartDate());
        budget.setEndDate(budgetRequestDTO.getEndDate());
        budget.setCustomer(customer);
        return budget;
    }

    private static BudgetResponseDTO buildBudgetResponse(Budget savedBudget) {
        BudgetResponseDTO response = new BudgetResponseDTO();
        response.setId(savedBudget.getId());
        response.setAmount(savedBudget.getAmount());
        response.setStartDate(savedBudget.getStartDate());
        response.setEndDate(savedBudget.getEndDate());
        response.setCustomerId(savedBudget.getCustomer().getId());
        return response;
    }

    public void deleteBudget(Long id) {
        Optional<Budget> budgetOptional = budgetRepository.findById(id);
        if (budgetOptional.isEmpty()) throw new ResourceNotFoundException("Budget with id " + id + " not found");
        budgetRepository.delete(budgetOptional.get());
    }

    public double getBudgetByCustomer(Long customerId) {

        Optional<Budget> budget = budgetRepository.findByCustomer_Id(customerId);

        if (!budget.isEmpty()) return budget.get().getAmount();
        return 0;
    }
}
