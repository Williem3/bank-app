package com.userfront.userfront.service.impl;

import com.userfront.userfront.dao.*;
import com.userfront.userfront.domain.*;
import com.userfront.userfront.service.TransactionService;
import com.userfront.userfront.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private UserService userService;

    @Autowired
    private PrimaryTransactionDao primaryTransactionDao;

    @Autowired
    private SavingsTransactionDao savingsTransactionDao;

    @Autowired
    private PrimaryAccountDao primaryAccountDao;

    @Autowired
    private SavingsAccountDao savingsAccountDao;

    @Autowired
    private RecipientDao recipientDao;

    public List<PrimaryTransaction> findPrimaryTransactionList(String username) {
        User user = userService.findByUsername(username);
        List<PrimaryTransaction> primaryTransactionList = user.getPrimaryAccount().getPrimaryTransactionList();

        return primaryTransactionList;
    }

    public List<SavingsTransaction> findSavingsTransactionList(String username) {
        User user = userService.findByUsername(username);
        List<SavingsTransaction> savingsTransactionList = user.getSavingsAccount().getSavingsTransactionsList();

        return savingsTransactionList;
    }

    public void savePrimaryDepositTransaction(PrimaryTransaction primaryTransaction) {
        primaryTransactionDao.save(primaryTransaction);
    }
    public void saveSavingsDepositTransaction(SavingsTransaction savingsTransaction) {
        savingsTransactionDao.save(savingsTransaction);
    }

    public void savePrimaryWithdrawalTransaction(PrimaryTransaction primaryTransaction) {
        primaryTransactionDao.save(primaryTransaction);
    }
    public void saveSavingsWithdrawalTransaction(SavingsTransaction savingsTransaction) {
        savingsTransactionDao.save(savingsTransaction);
    }

    @Override
    public void betweenAccountsTransfer(String transferFrom, String transferTo, String amount, PrimaryAccount primaryAccount, SavingsAccount savingsAccount) throws Exception {
        // if transfer from is primary and transfer to is saving then subtract from primary and add to savings
        if (transferFrom.equalsIgnoreCase("Primary") && transferTo.equalsIgnoreCase("Savings")) {
            if (primaryAccount.getAccountBalance().subtract(new BigDecimal(amount)).compareTo(BigDecimal.ZERO) >= 0) {
                primaryAccount.setAccountBalance(primaryAccount.getAccountBalance().subtract(new BigDecimal(amount)));
                savingsAccount.setAccountBalance(savingsAccount.getAccountBalance().add(new BigDecimal(amount)));
            }
            else {
                throw new Exception("Not enough currency in " + transferFrom + " account.");
            }
            primaryAccountDao.save(primaryAccount);
            savingsAccountDao.save(savingsAccount);

            Date date = new Date();

            PrimaryTransaction primaryTransaction = new PrimaryTransaction(date,
                    "Between account transfer from " + transferFrom + " to " + transferTo,
                    "Transfer", "Finished",
                    Double.parseDouble(amount), primaryAccount.getAccountBalance(), primaryAccount);
            SavingsTransaction savingsTransaction = new SavingsTransaction(date, "Between account transfer from " + transferFrom + " to " + transferTo,
                    "Transfer", "Finished",
                    Double.parseDouble(amount), savingsAccount.getAccountBalance(), savingsAccount);

            primaryTransactionDao.save(primaryTransaction);
            savingsTransactionDao.save(savingsTransaction);
        }
        // if transfer from is savings and transfer to is primary then add to primary and subtract from savings
        else if (transferFrom.equalsIgnoreCase("Savings") && transferTo.equalsIgnoreCase("Primary")) {
            if (savingsAccount.getAccountBalance().subtract(new BigDecimal(amount)).compareTo(BigDecimal.ZERO) >= 0) {
                primaryAccount.setAccountBalance(primaryAccount.getAccountBalance().add(new BigDecimal(amount)));
                savingsAccount.setAccountBalance(savingsAccount.getAccountBalance().subtract(new BigDecimal(amount)));
            }
            else {
                throw new Exception("Not enough currency in " + transferFrom + " account.");
            }
            primaryAccountDao.save(primaryAccount);
            savingsAccountDao.save(savingsAccount);

            Date date = new Date();

            SavingsTransaction savingsTransaction = new SavingsTransaction(date, "Between account transfer from " + transferFrom + " to " + transferTo,
                    "Transfer", "Finished",
                    Double.parseDouble(amount), savingsAccount.getAccountBalance(), savingsAccount);
            PrimaryTransaction primaryTransaction = new PrimaryTransaction(date,
                    "Between account transfer from " + transferFrom + " to " + transferTo,
                    "Transfer", "Finished",
                    Double.parseDouble(amount), primaryAccount.getAccountBalance(), primaryAccount);

            savingsTransactionDao.save(savingsTransaction);
            primaryTransactionDao.save(primaryTransaction);
        }
        else {
            throw new Exception("Invalid transfer");
        }
    }

    @Override
    public void saveRecipient(Recipient recipient) {

        recipientDao.save(recipient);
    }

    @Override
    public List<Recipient> findRecipientList(Principal principal) {
        String username = principal.getName();
        List<Recipient> recipientList = recipientDao.findAll().stream()
                .filter(recipient -> username.equals(recipient.getUser().getUsername()))
                .collect(Collectors.toList());

        return recipientList;
    }

    @Override
    public Recipient findRecipientByName(String recipientName) {
        return recipientDao.findByName(recipientName);
    }

    @Override
    public void deleteRecipientByName(String recipientName) {
        recipientDao.deleteByName(recipientName);
    }

    @Override
    public void toSomeoneElseTransfer(Recipient recipient, String accountType, String amount, PrimaryAccount primaryAccount, SavingsAccount savingsAccount) throws Exception {

        if (accountType.equalsIgnoreCase("Primary")) {
            if (primaryAccount.getAccountBalance().subtract(new BigDecimal(amount)).compareTo(BigDecimal.ZERO) >= 0) {
                primaryAccount.setAccountBalance(primaryAccount.getAccountBalance().subtract(new BigDecimal(amount)));
                primaryAccountDao.save(primaryAccount);

                Date date = new Date();

                PrimaryTransaction primaryTransaction = new PrimaryTransaction(date, "Transfer to recipient " + recipient + ".", "Transfer", "Finished", Double.parseDouble(amount), primaryAccount.getAccountBalance(), primaryAccount);
                primaryTransactionDao.save(primaryTransaction);
            }
            else {
                throw new Exception("Not enough funds for this transfer");
            }
        }
        else if (accountType.equalsIgnoreCase("Savings")) {
            if (savingsAccount.getAccountBalance().subtract(new BigDecimal(amount)).compareTo(BigDecimal.ZERO) >= 0) {
                savingsAccount.setAccountBalance(savingsAccount.getAccountBalance().subtract(new BigDecimal(amount)));
                savingsAccountDao.save(savingsAccount);

                Date date = new Date();

                SavingsTransaction savingsTransaction = new SavingsTransaction(date, "Transfer to recipient " + recipient + ".", "Transfer", "Finished", Double.parseDouble(amount), savingsAccount.getAccountBalance(), savingsAccount);
                savingsTransactionDao.save(savingsTransaction);
            }
            else {
                throw new Exception("Not enough funds for this transfer");
            }
        }

    }
}
