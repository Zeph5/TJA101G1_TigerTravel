package com.member.model;

import com.member.model.memVO;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<memVO, Integer> {
	
	@Query("SELECT m.avatar FROM memVO m WHERE m.member_id = :id")
	Optional<byte[]> findAvatarById(Integer id);
	
	Optional<memVO> findByMemberAccount(String memberAccount);

}
