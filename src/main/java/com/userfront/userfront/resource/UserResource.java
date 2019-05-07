package com.userfront.userfront.resource;

import java.util.List;

import com.userfront.userfront.domain.PrimaryTransaction;
import com.userfront.userfront.domain.SavingsTransaction;
import com.userfront.userfront.domain.User;
import com.userfront.userfront.service.TransactionService;
import com.userfront.userfront.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
@PreAuthorize("hasRole('ADMIN')")
public class UserResource {

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionService transactionService;

    @GetMapping(value = "/user/all")
    public List<User> userList() {
        return userService.findUserList();
    }

    @GetMapping(value = "/user/primary/transaction")
    public List<PrimaryTransaction> getPrimaryTransactionList(@RequestParam("username") String username) {
        return transactionService.findPrimaryTransactionList(username);
    }

    @GetMapping(value = "/user/savings/transaction")
    public List<SavingsTransaction> getSavingsTransactionList(@RequestParam("username") String username) {
        return transactionService.findSavingsTransactionList(username);
    }

    @RequestMapping("/user/{username}/enable")
    public void enableUser(@PathVariable("username") String username) {
        userService.enableUser(username);
    }

    @RequestMapping("/user/{username}/disable")
    public void disableUser(@PathVariable("username") String username) {
        userService.disableUser(username);
    }
}
