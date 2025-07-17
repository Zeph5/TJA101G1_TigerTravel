package com.scenery.model;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SceneryRepository extends JpaRepository<SceneryVO, Integer> {

	List<SceneryVO> findBySceneryNameContainingIgnoreCase(String sceneryName);

	List<SceneryVO> findBySceneryAddressContainingIgnoreCase(String sceneryAddress);

	List<SceneryVO> findBySceneryNameContainingIgnoreCaseAndSceneryAddressContainingIgnoreCase(String sceneryName,
			String sceneryAddress);

	Page<SceneryVO> findAll(Pageable pageable);

	Page<SceneryVO> findBySceneryNameContainingIgnoreCaseAndSceneryAddressContainingIgnoreCase(String sceneryName,
			String sceneryAddress, Pageable pageable);

	Optional<SceneryVO> findBySceneryName(String trim);

	@Query("SELECT s FROM SceneryVO s " +
		       "WHERE (:name IS NULL OR s.sceneryName LIKE %:name%) " +
		       "AND (:address IS NULL OR s.sceneryAddress LIKE %:address%) " +
		       "AND (:status IS NULL OR s.sceneryStatus = :status)")
		Page<SceneryVO> advancedSearch(@Param("name") String name,
		                               @Param("address") String address,
		                               @Param("status") Integer status,
		                               Pageable pageable);
	
	List<SceneryVO> findBySceneryNameContainingAndSceneryStatus(String name, Integer status);
    List<SceneryVO> findBySceneryStatus(Integer status);
    
    Page<SceneryVO> findBySceneryStatus(Integer status, Pageable pageable);
    
    @Query("SELECT s FROM SceneryVO s WHERE " +
    	       "(:keyword IS NULL OR LOWER(s.sceneryName) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
    	       "AND s.sceneryStatus = 1")
    	Page<SceneryVO> searchByKeywordAndStatus(@Param("keyword") String keyword, Pageable pageable);


}