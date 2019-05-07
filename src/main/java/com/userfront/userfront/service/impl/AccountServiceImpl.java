package com.userfront.userfront.service.impl;

import com.userfront.userfront.dao.PrimaryAccountDao;
import com.userfront.userfront.dao.PrimaryTransactionDao;
import com.userfront.userfront.dao.SavingsAccountDao;
import com.userfront.userfront.domain.*;
import com.userfront.userfront.service.AccountService;
import com.userfront.userfront.service.TransactionService;
import com.userfront.userfront.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.Date;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {

    private static int nextAccountNumber = 11223145;

    @Autowired
    private PrimaryAccountDao primaryAccountDao;
    @Autowired
    private SavingsAccountDao savingsAccountDao;

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionService transactionService;

    @Override
    // CREATE PRIMARY ACCOUNT METHOD
    public PrimaryAccount createPrimaryAccount() {
        PrimaryAccount primaryAccount = new PrimaryAccount();
        primaryAccount.setAccountBalance(new BigDecimal(0.0));
        primaryAccount.setAccountNumber(accountGen());

        primaryAccountDao.save(primaryAccount);

        return primaryAccountDao.findByAccountNumber(primaryAccount.getAccountNumber());
    }

    @Override
    // CREATE SAVINGS ACCOUNT METHOD
    public SavingsAccount createSavingsAccount() {
        SavingsAccount savingsAccount = new SavingsAccount();
        savingsAccount.setAccountBalance(new BigDecimal(0.0));
        savingsAccount.setAccountNumber(accountGen());

        savingsAccountDao.save(savingsAccount);

        return savingsAccountDao.findByAccountNumber(savingsAccount.getAccountNumber());
    }

    // DEPOSIT METHOD
    public void deposit(String accountType, double amount, Principal principal) {
        User user = userService.findByUsername(principal.getName());

        if (accountType.equalsIgnoreCase("Primary")) {
            PrimaryAccount primaryAccount = user.getPrimaryAccount();
            primaryAccount.setAccountBalance(primaryAccount.getAccountBalance().add(new BigDecimal(amount)));
            primaryAccountDao.save(primaryAccount);

            Date date = new Date();

            PrimaryTransaction primaryTransaction = new PrimaryTransaction(date, "Deposit to Primary Account", "Account", "Finished", amount, primaryAccount.getAccountBalance(), primaryAccount);
            transactionService.savePrimaryDepositTransaction(primaryTransaction);
        }
        else if (accountType.equalsIgnoreCase("Savings")) {
            SavingsAccount savingsAccount = user.getSavingsAccount();
            savingsAccount.setAccountBalance(savingsAccount.getAccountBalance().add(new BigDecimal(amount)));
            savingsAccountDao.save(savingsAccount);

            Date date = new Date();

            SavingsTransaction savingsTransaction = new SavingsTransaction(date, "Deposit to Savings Account", "Account", "Finished", amount, savingsAccount.getAccountBalance(), savingsAccount);
            transactionService.saveSavingsDepositTransaction(savingsTransaction);
        }
    }

    // WITHDRAWAL METHOD
    public void withdraw(String accountType, double amount, Principal principal) throws Exception {
        User user = userService.findByUsername(principal.getName());

        if (accountType.equalsIgnoreCase("Primary")) {
            PrimaryAccount primaryAccount = user.getPrimaryAccount();

            if (primaryAccount.getAccountBalance().subtract(new BigDecimal(amount)).compareTo(BigDecimal.ZERO) >= 0) {
                primaryAccount.setAccountBalance(primaryAccount.getAccountBalance().subtract(new BigDecimal(amount)));
                primaryAccountDao.save(primaryAccount);

                Date date = new Date();

                PrimaryTransaction primaryTransaction = new PrimaryTransaction(date, "Withdrawal from Primary Account", "Account", "Finished", amount, primaryAccount.getAccountBalance(), primaryAccount);
                transactionService.savePrimaryWithdrawalTransaction(primaryTransaction);
            }
            else {
                throw new Exception("Not enough funds for this transfer");
            }

        }
        else if (accountType.equalsIgnoreCase("Savings")) {
            SavingsAccount savingsAccount = user.getSavingsAccount();


            if (savingsAccount.getAccountBalance().subtract(new BigDecimal(amount)).compareTo(BigDecimal.ZERO) >= 0) {
                savingsAccount.setAccountBalance(savingsAccount.getAccountBalance().subtract(new BigDecimal(amount)));
                savingsAccountDao.save(savingsAccount);

                Date date = new Date();

                SavingsTransaction savingsTransaction = new SavingsTransaction(date, "Withdrawal from Savings Account", "Account", "Finished", amount, savingsAccount.getAccountBalance(), savingsAccount);
                transactionService.saveSavingsWithdrawalTransaction(savingsTransaction);
            }
            else {
                throw new Exception("Not enough funds for this transfer");
            }
        }
    }

    private int accountGen() {
        return ++nextAccountNumber;
    }

}
