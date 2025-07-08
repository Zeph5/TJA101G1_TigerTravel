package com.member.service.favorite;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.stereotype.Service;

import com.member.model.FavoriteTourRepository;
import com.member.model.FavoriteTourVO;
import com.member.model.memVO;
import com.scenery.model.SceneryVO;

@Service
public class FavoriteTourService {
	
	private FavoriteTourRepository favoriteTourRepo;
	
	public FavoriteTourService(FavoriteTourRepository favoriteTourRepo) {
		this.favoriteTourRepo = favoriteTourRepo;
	}
	
	public void addFavorite(memVO member, SceneryVO scenery) {
        if (favoriteTourRepo.findByMemberAndScenery(member, scenery).isEmpty()) {
            FavoriteTourVO favorite = new FavoriteTourVO();
            favorite.setMember(member);
            favorite.setScenery(scenery);
            favorite.setCreateTime(new Timestamp(System.currentTimeMillis()));
            favoriteTourRepo.save(favorite);
        }
    }

    public void removeFavorite(memVO member, SceneryVO scenery) {
        favoriteTourRepo.deleteByMemberAndScenery(member, scenery);
    }

    public List<FavoriteTourVO> getFavoritesByMember(memVO member) {
        return favoriteTourRepo.findByMember(member);
    }

}
