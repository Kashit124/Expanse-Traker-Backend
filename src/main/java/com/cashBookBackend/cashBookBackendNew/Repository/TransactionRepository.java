package com.cashBookBackend.cashBookBackendNew.Repository;

import com.cashBookBackend.cashBookBackendNew.Entity.Transactions;
import org.hibernate.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transactions,Integer> {
    List<Transactions> findByAccount_AccountId(Integer accountId);
}
