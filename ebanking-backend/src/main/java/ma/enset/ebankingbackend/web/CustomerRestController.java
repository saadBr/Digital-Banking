package ma.enset.ebankingbackend.web;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.enset.ebankingbackend.dtos.BankAccountDTO;
import ma.enset.ebankingbackend.dtos.CustomerDTO;
import ma.enset.ebankingbackend.entities.Customer;
import ma.enset.ebankingbackend.exceptions.CustomerNotFoundException;
import ma.enset.ebankingbackend.services.BankAccountService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
public class CustomerRestController {
    private BankAccountService bankAccountService;
    @GetMapping("/customers")
    @PreAuthorize("hasRole('USER')")
    public List<CustomerDTO> customers() {
        return bankAccountService.getCustomers();
    }
    @GetMapping("/customers/{id}")
    @PreAuthorize("hasRole('USER')")
    public CustomerDTO getCustomer(@PathVariable(name = "id") Long customerId) throws CustomerNotFoundException {
        return bankAccountService.getCustomerById(customerId);
    }
    @GetMapping("/customers/search")
    @PreAuthorize("hasRole('USER')")
    public List<CustomerDTO> searchCustomers(@RequestParam(name = "keyword",defaultValue = "")String keyword) {

        return bankAccountService.searchCustomer("%"+keyword+"%");
    }
    @PostMapping("/customers")
    @PreAuthorize("hasRole('ADMIN')")
    public CustomerDTO createCustomer(@RequestBody CustomerDTO customerDTO) {
        return bankAccountService.saveCustomer(customerDTO);
    }

    @PutMapping("/customers/{customerID}")
    @PreAuthorize("hasRole('ADMIN')")
    public CustomerDTO updateCustomer(@PathVariable long customerID, @RequestBody CustomerDTO customerDTO) throws CustomerNotFoundException {
        customerDTO.setId(customerID);
        return bankAccountService.updateCustomer(customerDTO);
    }
    @DeleteMapping("/customers/{customerID}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCustomer(@PathVariable long customerID) throws CustomerNotFoundException {
        bankAccountService.deleteCustomer(customerID);
    }
    @GetMapping("/customers/{customerID}/accounts")
    @PreAuthorize("hasRole('USER')")
    public List<BankAccountDTO> getAccountsByCustomer(@PathVariable Long customerID) throws CustomerNotFoundException {
        return bankAccountService.getAccountsByCustomerId(customerID);
    }

}
