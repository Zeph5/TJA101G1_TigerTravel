package com.member.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.member.model.FavoriteSceneryRepository;
import com.member.model.FavoriteSceneryVO;
import com.member.model.memVO;
import com.scenery.model.SceneryVO;

@Service
public class FavoriteSceneryService {

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
}
