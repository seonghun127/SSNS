package com.ssns.Accounts;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class AccountDto {

    @Data
    public static class Create {
        @NotBlank
        private String email;

        @NotBlank
        private String password;

        @NotBlank
        private String name;
    }

    @Data
    public static class Update {
        @NotBlank
        private String name;
    }

    @Data
    public static class Response {
        private String email;
        private String name;
        private LocalDateTime createdDate;
        private LocalDateTime modifiedDate;
    }
}
