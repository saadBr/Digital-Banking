package ma.enset.ebankingbackend.mappers;

import ma.enset.ebankingbackend.dtos.AccountOperationDTO;
import ma.enset.ebankingbackend.dtos.CurrentAccountDTO;
import ma.enset.ebankingbackend.dtos.CustomerDTO;
import ma.enset.ebankingbackend.dtos.SavingAccountDTO;
import ma.enset.ebankingbackend.entities.AccountOperation;
import ma.enset.ebankingbackend.entities.CurrentAccount;
import ma.enset.ebankingbackend.entities.Customer;
import ma.enset.ebankingbackend.entities.SavingAccount;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class BankAccountMapperImpl {


    public CustomerDTO fromCustomerToCustomerDTO(Customer customer) {
        CustomerDTO dto = new CustomerDTO();
        BeanUtils.copyProperties(customer, dto);
        return dto;
    }

    public Customer fromCustomerDTOToCustomer(CustomerDTO dto) {
        Customer entity = new Customer();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }


    public CurrentAccountDTO fromCurrentAccountToCurrentAccountDTO(CurrentAccount entity) {
        CurrentAccountDTO dto = new CurrentAccountDTO();
        BeanUtils.copyProperties(entity, dto);
        dto.setType("CurrentAccount");
        return dto;
    }


    public SavingAccountDTO fromSavingAccountToSavingAccountDTO(SavingAccount entity) {
        SavingAccountDTO dto = new SavingAccountDTO();
        BeanUtils.copyProperties(entity, dto);
        dto.setType("SavingAccount");
        return dto;
    }


    public AccountOperationDTO fromAccountToAccountOperationDTO(AccountOperation entity) {
        AccountOperationDTO dto = new AccountOperationDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
