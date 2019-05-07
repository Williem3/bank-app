package com.userfront.userfront.service;

import com.userfront.userfront.domain.*;

import java.security.Principal;
import java.util.List;

public interface TransactionService {
    List<PrimaryTransaction> findPrimaryTransactionList(String username);
    List<SavingsTransaction> findSavingsTransactionList(String username);
    void savePrimaryDepositTransaction(PrimaryTransaction primaryTransaction);
    void saveSavingsDepositTransaction(SavingsTransaction savingsTransaction);
    void savePrimaryWithdrawalTransaction(PrimaryTransaction primaryTransaction);
    void saveSavingsWithdrawalTransaction(SavingsTransaction savingsTransaction);

    void betweenAccountsTransfer(String transferFrom,
                                 String transferTo, String amount,
                                 PrimaryAccount primaryAccount,
                                 SavingsAccount savingsAccount) throws Exception;

    void saveRecipient(Recipient recipient);

    List<Recipient> findRecipientList(Principal principal);

    Recipient findRecipientByName(String recipientName);

    void deleteRecipientByName(String recipientName);

    void toSomeoneElseTransfer(Recipient recipient, String accountType, String amount, PrimaryAccount primaryAccount, SavingsAccount savingsAccount) throws Exception;
}
