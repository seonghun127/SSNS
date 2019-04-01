package com.ssns;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssns.Accounts.Account;
import com.ssns.Accounts.AccountDto;
import com.ssns.Accounts.AccountRepository;
import com.ssns.Accounts.AccountService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Transactional
public class AccountControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    private MockMvc mockMvc;

    public AccountDto.Create accountCreateFixture(){
        AccountDto.Create account = new AccountDto.Create();
        account.setEmail("seonghun");
        account.setPassword("1234");
        account.setName("kim");
        return account;
    }

    @Before
    public void setup(){
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    // ======================================================================================================== //

   /* test for creating new account */
    @Test
    public void createAccount() throws Exception {
        AccountDto.Create account = accountCreateFixture();

        ResultActions result = mockMvc.perform(post("/accounts")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(account)));
        result.andDo(print());
        result.andExpect(status().isCreated());

        result = mockMvc.perform(post("/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(account)));

        result.andDo(print());
        result.andExpect(status().isBadRequest());
        result.andExpect(jsonPath("$.code", is("duplicated.email.exception")));
    }

    // ======================================================================================================== //

    /* test for getting accounts by page */
    @Test
    public void getAccounts() throws Exception{
        AccountDto.Create account = accountCreateFixture();
        accountService.createAccount(account);

        ResultActions result = mockMvc.perform(get("/accounts"));
        result.andDo(print());
        result.andExpect(status().isOk());
    }

    // ======================================================================================================== //

    /* test for getting a account with id */
    @Test
    public void getAccount() throws Exception {
        AccountDto.Create account = accountCreateFixture();
        Account newAccount = accountService.createAccount(account);

        ResultActions result = mockMvc.perform(get("/accounts/"+newAccount.getId()));
        result.andDo(print());
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.email", is("seonghun")));
    }

    // ======================================================================================================== //

    /* test for updating account's information */
    @Test
    public void updateAccount() throws Exception {
        AccountDto.Create account = accountCreateFixture();
        Account newAccount = accountService.createAccount(account);
        newAccount.setName("kim seong hoon");

        ResultActions result = mockMvc.perform(put("/accounts/"+newAccount.getId())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(newAccount)));
        result.andDo(print());
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.name", is("kim seong hoon")));
    }

    // ======================================================================================================== //

    /* test for deleting a account */
    @Test
    public void deleteAccount() throws Exception{
        AccountDto.Create account = accountCreateFixture();
        Account newAccount = accountService.createAccount(account);

        ResultActions result = mockMvc.perform(delete("/accounts/" + newAccount.getId()));
        result.andDo(print());
        result.andExpect(status().isNoContent());
    }
    // ======================================================================================================== //

    @Test
    public void checkProfile () {
        String profile = this.restTemplate.getForObject("/profile", String.class);
        assertThat(profile).isEqualTo("local");
    }

    // ======================================================================================================== //
}
