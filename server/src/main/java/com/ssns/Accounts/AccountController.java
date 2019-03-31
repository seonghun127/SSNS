package com.ssns.Accounts;

import com.ssns.commons.ErrorResponse;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
public class AccountController {

    private AccountService accountService;

    private AccountRepository accountRepository;

    private ModelMapper modelMapper;

    // ======================================================================================================== //

    /**
     * create new account method
     * @param createDto
     * @param result
     * @return ResponseEntity
     */
    @PostMapping("/accounts")
    public ResponseEntity createAccount(@RequestBody @Valid AccountDto.Create createDto, BindingResult result){
        if(result.hasErrors()){
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setMessage("잘못된 요청입니다.");
            errorResponse.setCode("bad.request");
            return new ResponseEntity<> (errorResponse, HttpStatus.BAD_REQUEST);
        }

        Account newAccount = accountService.createAccount(createDto);
        return new ResponseEntity<>(modelMapper.map(newAccount, AccountDto.Respose.class), HttpStatus.CREATED);
    }

    @ExceptionHandler(AccountDuplicatedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleAccountDuplicatedException(AccountDuplicatedException e){
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage("["+e.getEmail()+"] 중복된 이메일입니다.");
        errorResponse.setCode("duplicated.email.exception");
        return errorResponse;
    }

    // ======================================================================================================== //

    /**
     * get accounts by page
     * @param pageable
     * @return
     */
    @GetMapping("/accounts")
    @ResponseStatus(HttpStatus.OK)
    public PageImpl getAccounts(Pageable pageable){
        Page<Account> page = accountRepository.findAll(pageable);
        List<AccountDto.Respose> list = page.getContent().stream()
                                                .map(account -> modelMapper.map(account, AccountDto.Respose.class))
                                                .collect(Collectors.toList());
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    // ======================================================================================================== //

    /**
     * get a account with id
     * @param id
     * @return
     */
    @GetMapping("/accounts/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AccountDto.Respose getAccount(@PathVariable Long id){
        Account account = accountService.getAccount(id);
        return modelMapper.map(account, AccountDto.Respose.class);
    }

    @ExceptionHandler(AccountNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleAccountNotFoundException(AccountNotFoundException e){
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage("["+e.getId()+"]에 해당하는 계정이 없습니다.");
        errorResponse.setCode("account.not.found.exception");
        return errorResponse;
    }

    // ======================================================================================================== //

    /**
     * update account's information (name)
     * @param id
     * @param updateAccount
     * @param result
     * @return
     */
    @PutMapping("/accounts/{id}")
    public ResponseEntity updateAccount(@PathVariable Long id, @RequestBody @Valid AccountDto.Update updateAccount,
                                        BindingResult result) {
        if(result.hasErrors()){
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setMessage("잘못된 요청입니다.");
            errorResponse.setCode("bad.request");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        Account account = accountService.updateAccount(id, updateAccount);
        return new ResponseEntity<>(modelMapper.map(account, AccountDto.Respose.class), HttpStatus.OK);
    }

    // ======================================================================================================== //

    /**
     * delete a account
     * @param id
     * @return
     */
    @DeleteMapping("/accounts/{id}")
    public ResponseEntity deleteAccount(@PathVariable Long id){
        accountService.deleteAccount(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // ======================================================================================================== //
}
