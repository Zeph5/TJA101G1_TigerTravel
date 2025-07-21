package com.member.model;

import com.member.model.memVO;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<memVO, Integer> {
	
	Optional<memVO> findById(Integer id);
	
	@Query("SELECT m.avatar FROM memVO m WHERE m.memberId = :id")
	Optional<byte[]> findAvatarById(Integer id);
	
	Optional<memVO> findByMemberAccount(String memberAccount);
	
	Optional<memVO> findByVerifyToken(String verifyToken);
	
	Optional<memVO> findByResetToken(String resetToken);

	Optional<memVO> findByMemberEmail(String email);

	Page<memVO> findAll(Specification<memVO> spec, Pageable pageable);
	
}
