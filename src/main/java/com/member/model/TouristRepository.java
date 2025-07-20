package com.member.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TouristRepository extends JpaRepository<TouristVO, Integer> {

    // 根據帳號查詢（如果未來需要）
    TouristVO findByMemberAccount(String memberAccount);
}

