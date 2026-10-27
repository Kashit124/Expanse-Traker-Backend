package com.cashBookBackend.cashBookBackendNew.Repository;

import com.cashBookBackend.cashBookBackendNew.Entity.Accounts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Accounts,Integer> {
    List<Accounts> findByUser_Id(Long userId);

    Optional<Accounts> findByNameAndUser_Id(String name,Long userId);

    boolean existsByNameAndUser_Id(String name,Long userId);
}
