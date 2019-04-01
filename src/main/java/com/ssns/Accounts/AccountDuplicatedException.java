package com.ssns.Accounts;

public class AccountDuplicatedException extends RuntimeException {
    private String email;

    public AccountDuplicatedException(String email) {
        this.email = email;
    }

    public String getEmail(){
        return this.email;
    }
}
