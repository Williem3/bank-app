package com.userfront.userfront.controller;

import com.userfront.userfront.domain.*;
import com.userfront.userfront.service.AccountService;
import com.userfront.userfront.service.TransactionService;
import com.userfront.userfront.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private UserService userService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionService transactionService;

    @RequestMapping("/primaryAccount")
    public String primaryAccount(Model theModel, Principal principal) {
        List<PrimaryTransaction> primaryTransactionList = transactionService.findPrimaryTransactionList(principal.getName());

        User user = userService.findByUsername(principal.getName());
        PrimaryAccount primaryAccount = user.getPrimaryAccount();

        theModel.addAttribute("primaryAccount", primaryAccount);
        theModel.addAttribute("primaryTransactionList", primaryTransactionList);

        return "primaryAccount";
    }


    @RequestMapping("/savingsAccount")
    public String savingsAccount(Model theModel, Principal principal) {
        List<SavingsTransaction> savingsTransactionList = transactionService.findSavingsTransactionList(principal.getName());

        User user = userService.findByUsername(principal.getName());
        SavingsAccount savingsAccount = user.getSavingsAccount();

        theModel.addAttribute("savingsAccount", savingsAccount);
        theModel.addAttribute("savingsTransactionList", savingsTransactionList);

        return "savingsAccount";
    }

    @GetMapping("/deposit")
    public String deposit(Model theModel) {
        theModel.addAttribute("accountType", "");
        theModel.addAttribute("amount", "");

        return "deposit";
    }
    @PostMapping("/deposit")
    public String depositPOST(@ModelAttribute("amount") String amount, @ModelAttribute("accountType") String accountType,
                                                                                                Principal principal) {
            accountService.deposit(accountType, Double.parseDouble(amount), principal);

            return "redirect:/userFront";
    }


    @GetMapping("/withdraw")
    public String withdraw(Model theModel) {
        theModel.addAttribute("accountType", "");
        theModel.addAttribute("amount", "");

        return "withdraw";
    }
    @PostMapping("/withdraw")
    public String withdrawPOST(@ModelAttribute("amount") String amount, @ModelAttribute("accountType") String accountType,
                          Principal principal) throws Exception {
        accountService.withdraw(accountType, Double.parseDouble(amount), principal);

        return "redirect:/userFront";
    }

}
