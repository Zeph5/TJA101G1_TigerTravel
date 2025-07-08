package com.member.model;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scenery.model.SceneryVO;

public interface FavoriteTourRepository extends JpaRepository<FavoriteTourVO,Integer> {

	//查詢某會員的所有景點收藏
	List<FavoriteTourVO> findByMember(memVO member);
	
	//判斷會員是否已收藏某景點(避免重複收藏)
	Optional<FavoriteTourVO> findByMemberAndScenery(memVO member, SceneryVO scenery);

	//刪除指定會員的某個景點收藏
	void deleteByMemberAndScenery(memVO member, SceneryVO scenery);
}
