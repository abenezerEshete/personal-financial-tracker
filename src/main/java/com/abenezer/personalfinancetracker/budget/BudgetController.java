package com.abenezer.personalfinancetracker.budget;

import com.abenezer.personalfinancetracker.budget.dto.BudgetRequestDTO;
import com.abenezer.personalfinancetracker.budget.dto.BudgetResponseDTO;
import com.abenezer.personalfinancetracker.exception.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    // Create a new Budget
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BudgetResponseDTO> createBudget(@RequestBody BudgetRequestDTO budgetRequest) {

        validateBudgetRequest(budgetRequest);

        BudgetResponseDTO createdBudget = budgetService.createBudget(budgetRequest);
        return ResponseEntity.status(201).body(createdBudget);
    }

    // Get a Budget by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<BudgetResponseDTO> getBudgetById(@PathVariable Long id) {
        BudgetResponseDTO budget = budgetService.getBudgetById(id);
        return ResponseEntity.ok(budget);
    }

    // Get all Budgets
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<BudgetResponseDTO>> getAllBudgets() {
        List<BudgetResponseDTO> budgets = budgetService.getAllBudgets();
        return ResponseEntity.ok(budgets);
    }

    // Update a Budget by ID
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BudgetResponseDTO> updateBudget(@PathVariable Long id, @RequestBody BudgetRequestDTO budgetRequest) {

        validateBudgetRequest(budgetRequest);

        BudgetResponseDTO updatedBudget = budgetService.updateBudget(id, budgetRequest);
        return ResponseEntity.ok(updatedBudget); // HTTP 200 OK
    }

    // Delete a Budget by ID
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Void> deleteBudget(@PathVariable Long id) {
        budgetService.deleteBudget(id);
        return ResponseEntity.noContent().build();
    }


    private static void validateBudgetRequest(BudgetRequestDTO budgetRequestDTO) {
        if (budgetRequestDTO.getAmount() <= 0)
            throw new IllegalArgumentException("budgetAmount must be greater than zero");
        if (budgetRequestDTO.getStartDate() == null) throw new IllegalArgumentException("startDate must not be null");
        if (budgetRequestDTO.getEndDate() == null) throw new IllegalArgumentException("endDate must not be null");
        if (budgetRequestDTO.getCustomerId() <= 0) throw new ResourceNotFoundException("Invalid customer id");
        if (budgetRequestDTO.getEndDate().isBefore(budgetRequestDTO.getStartDate()))
            throw new IllegalArgumentException("endDate must be before startDate");
    }

}
