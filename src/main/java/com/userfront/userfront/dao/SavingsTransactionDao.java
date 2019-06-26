package com.userfront.userfront.dao;

import com.userfront.userfront.domain.SavingsTransaction;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SavingsTransactionDao extends CrudRepository<SavingsTransaction,Long> {
    List<SavingsTransaction> findAll();
}
