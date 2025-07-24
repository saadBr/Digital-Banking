package ma.enset.ebankingbackend.web;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.enset.ebankingbackend.dtos.BankAccountDTO;
import ma.enset.ebankingbackend.dtos.CustomerDTO;
import ma.enset.ebankingbackend.exceptions.CustomerNotFoundException;
import ma.enset.ebankingbackend.services.ActionLogServiceImpl;
import ma.enset.ebankingbackend.services.BankAccountService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
public class CustomerRestController {
    private BankAccountService bankAccountService;
    private ActionLogServiceImpl actionLogServiceImpl;

    @GetMapping("/customers")
    @PreAuthorize("hasRole('USER')")
    public List<CustomerDTO> customers() {
        return bankAccountService.getCustomers();
    }

    @GetMapping("/customers/{id}")
    @PreAuthorize("hasRole('USER')")
    public CustomerDTO getCustomer(@PathVariable(name = "id") String customerId) throws CustomerNotFoundException {
        return bankAccountService.getCustomerById(customerId);
    }

    @GetMapping("/customers/search")
    @PreAuthorize("hasRole('USER')")
    public List<CustomerDTO> searchCustomers(@RequestParam(name = "keyword", defaultValue = "") String keyword) {
        return bankAccountService.searchCustomer(keyword);
    }

    @PostMapping("/customers")
    @PreAuthorize("hasRole('USER')")
    public CustomerDTO createCustomer(@RequestBody CustomerDTO customerDTO, Principal principal) {
        CustomerDTO saved = bankAccountService.saveCustomer(customerDTO);
        actionLogServiceImpl.log(principal.getName(),
                "CREATE_CUSTOMER",
                "Created customer with ID " + saved.getId() + " and name " + saved.getName());
        return saved;
    }

    @PutMapping("/customers/{customerID}")
    @PreAuthorize("hasRole('USER')")
    public CustomerDTO updateCustomer(@PathVariable String customerID, @RequestBody CustomerDTO customerDTO, Principal principal) throws CustomerNotFoundException {
        customerDTO.setId(customerID);
        CustomerDTO updated = bankAccountService.updateCustomer(customerDTO);
        actionLogServiceImpl.log(principal.getName(),
                "UPDATE_CUSTOMER",
                "Updated customer ID " + updated.getId() + " with name " + updated.getName());
        return updated;
    }

    @DeleteMapping("/customers/{customerID}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCustomer(@PathVariable String customerID, Principal principal) throws CustomerNotFoundException {
        bankAccountService.deleteCustomer(customerID);
        actionLogServiceImpl.log(principal.getName(),
                "DELETE_CUSTOMER",
                "Deleted customer with ID " + customerID);
    }

    @GetMapping("/customers/{customerID}/accounts")
    @PreAuthorize("hasRole('USER')")
    public List<BankAccountDTO> getAccountsByCustomer(@PathVariable String customerID) throws CustomerNotFoundException {
        return bankAccountService.getAccountsByCustomerId(customerID);
    }
}
