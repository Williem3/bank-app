package com.userfront.userfront.controller;

import com.userfront.userfront.domain.PrimaryAccount;
import com.userfront.userfront.domain.Recipient;
import com.userfront.userfront.domain.SavingsAccount;
import com.userfront.userfront.domain.User;
import com.userfront.userfront.service.TransactionService;
import com.userfront.userfront.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.management.loading.PrivateClassLoader;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/transfer")
public class TransferController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserService userService;

    @GetMapping("/betweenAccounts")
    public String betweenAccounts(Model theModel) {
        theModel.addAttribute("transferFrom", "");
        theModel.addAttribute("transferTo", "");
        theModel.addAttribute("amount", "");

        return "betweenAccounts";
    }

    @PostMapping("/betweenAccounts")
    public String betweenAccountsPOST(@ModelAttribute("transferFrom") String transferFrom,
                                      @ModelAttribute("transferTo") String transferTo,
                                      @ModelAttribute("amount") String amount,
                                      Principal principal) throws Exception {
        User user = userService.findByUsername(principal.getName());
        PrimaryAccount primaryAccount = user.getPrimaryAccount();
        SavingsAccount savingsAccount = user.getSavingsAccount();

        transactionService.betweenAccountsTransfer(transferFrom, transferTo, amount, primaryAccount, savingsAccount);

        return "redirect:/userFront";

    }

    @GetMapping("/recipient")
    public String recipient(Model theModel, Principal principal) {

        List<Recipient> recipientList = transactionService.findRecipientList(principal);
        Recipient recipient = new Recipient();

        theModel.addAttribute("recipientList", recipientList);
        theModel.addAttribute("recipient", recipient);

        return "recipient";

    }
    @PostMapping("/recipient/save")
    public String recipientPOST(@ModelAttribute("recipient") Recipient recipient, Principal principal) {

        User user = userService.findByUsername(principal.getName());
        recipient.setUser(user);
        transactionService.saveRecipient(recipient);

        return "redirect:/transfer/recipient";
    }
    @GetMapping("/recipient/edit")
    public String recipientEdit(@RequestParam(value = "recipientName") String recipientName, Model theModel, Principal principal) {
        Recipient recipient = transactionService.findRecipientByName(recipientName);
        List<Recipient> recipientList = transactionService.findRecipientList(principal);

        theModel.addAttribute("recipientList", recipientList);
        theModel.addAttribute("recipient", recipient);

        return "recipient";
    }
    @GetMapping("recipient/delete")
    public String recipientDelete(@RequestParam(value = "recipientName") String recipientName, Model theModel, Principal principal) {

        transactionService.deleteRecipientByName(recipientName);

        List<Recipient> recipientList = transactionService.findRecipientList(principal);

        Recipient recipient = new Recipient();

        theModel.addAttribute("recipient", recipient);

        return "recipient";
    }

    @GetMapping("/toSomeoneElse")
    public String toSomeoneElse(Model theModel, Principal principal) {
        List<Recipient> recipientList = transactionService.findRecipientList(principal);

        theModel.addAttribute("recipientList", recipientList);
        theModel.addAttribute("accountType", "");

        return "toSomeoneElse";
    }

    @PostMapping("/toSomeoneElse")
    public String toSomeoneElsePOST(@ModelAttribute("recipientName") String recipientName,
                                @ModelAttribute("accountType") String accountType,
                                @ModelAttribute("amount") String amount,
                                Principal principal) {

        User user = userService.findByUsername(principal.getName());
        Recipient recipient = transactionService.findRecipientByName(recipientName);

        try {
            transactionService.toSomeoneElseTransfer(recipient, accountType, amount, user.getPrimaryAccount(), user.getSavingsAccount());
        }
        catch (Exception e) {
            return "NotEnoughFunds";
        }


        return "redirect:/userFront";
    }
}

