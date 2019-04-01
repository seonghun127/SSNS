package com.ssns.Accounts;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class AccountService {

    private AccountRepository accountRepository;

    private ModelMapper modelMapper;

    /* create new account */
    public Account createAccount(AccountDto.Create dto) {
        Account account = modelMapper.map(dto, Account.class);

        if(accountRepository.findByEmail(account.getEmail()) != null){
            log.error("account name is duplicated! {}", account.getEmail());
            throw new AccountDuplicatedException(account.getEmail());
        }

        return accountRepository.save(account);
    }

    /* find a account with id */
    public Account getAccount(Long id) {
        Optional<Account> option =  accountRepository.findById(id);

        if(!option.isPresent()){
            log.error("해당 id에 대한 계정이 존재하지 않습니다.");
            throw new AccountNotFoundException(id);
        }

        return option.get();
    }

    /* update account's information */
    public Account updateAccount(Long id, AccountDto.Update updateAccount) {
        Account account = getAccount(id);
        account.setName(updateAccount.getName());
        return accountRepository.save(account);
    }

    /* delete a account */
    public void deleteAccount(Long id) {
        Account account = getAccount(id);
        accountRepository.deleteById(account.getId());
    }
}
