package com.member.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.member.model.FavoriteSceneryRepository;
import com.member.model.FavoriteSceneryVO;
import com.member.model.memVO;
import com.scenery.model.SceneryVO;

import jakarta.transaction.Transactional;

@Service
public class FavoriteSceneryService {
	
	@Autowired
    private FavoriteSceneryRepository favoriteSceneryRepository;

	private final FavoriteSceneryRepository favoriteSceneryRepo;
	
	public FavoriteSceneryService(FavoriteSceneryRepository favoriteSceneryRepo) {
		this.favoriteSceneryRepo = favoriteSceneryRepo;
	}
	
	public List<SceneryVO> getFavoritesByMember(memVO member) {
        List<FavoriteSceneryVO> favorites = favoriteSceneryRepo.findByMember(member);
        return favorites.stream()
                        .map(FavoriteSceneryVO::getScenery)
                        .collect(Collectors.toList());
    }
	
	public boolean isFavorited(Integer memberId, Integer sceneryId) {
        return favoriteSceneryRepository.existsByMember_MemberIdAndScenery_SceneryId(memberId, sceneryId);
    }
    
    public void addFavorite(Integer memberId, Integer sceneryId) {
        if (!isFavorited(memberId, sceneryId)) {
            FavoriteSceneryVO favorite = new FavoriteSceneryVO();
            
            // Set member
            memVO member = new memVO();
            member.setMemberId(memberId);
            favorite.setMember(member);
            
            // Set scenery
            SceneryVO scenery = new SceneryVO();
            scenery.setSceneryId(sceneryId);
            favorite.setScenery(scenery);
            
            // Set create time
            favorite.setCreateTime(LocalDateTime.now());
            
            favoriteSceneryRepository.save(favorite);
        }
        
    }
    @Transactional
    public void removeFavorite(Integer memberId, Integer sceneryId) {
        favoriteSceneryRepository.deleteByMember_MemberIdAndScenery_SceneryId(memberId, sceneryId);
    }
}
