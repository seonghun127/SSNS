package com.ssns.Accounts;

public class AccountNotFoundException extends RuntimeException {
    private Long id;

    public AccountNotFoundException(Long id) {
        this.id = id;
    }

    public Long getId(){
        return id;
    }
}
