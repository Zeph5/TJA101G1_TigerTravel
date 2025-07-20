package com.member.service.favorite;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.member.model.FavoriteTravelPlan;
import com.member.model.FavoriteTravelPlanRepository;
import com.member.model.MemberRepository;
import com.member.model.memVO;
import com.travel_plan.model.TravelItinerary;
import com.travel_plan.model.TravelPlan;

import jakarta.transaction.Transactional;

@Service
public class FavoriteTravelPlanServiceImpl implements FavoriteTravelPlanService {

	
	private final FavoriteTravelPlanRepository favoriteTravelPlanRepo;
    
    private final MemberRepository memberRepository;
    
    public FavoriteTravelPlanServiceImpl(FavoriteTravelPlanRepository favoriteTravelPlanRepo,
    									MemberRepository memberRepository) {
        this.favoriteTravelPlanRepo = favoriteTravelPlanRepo;
        this.memberRepository = memberRepository;
    }

    public List<TravelPlan> getTravelPlansByMember(memVO member) {
    	memVO realMember = memberRepository.findById(member.getMemberId()).orElse(null);
        if (realMember == null) return Collections.emptyList();
        
        System.out.println("會員ID: " + realMember.getMemberId());
        System.out.println("會員帳號: " + realMember.getMemberAccount());

        List<FavoriteTravelPlan> list = favoriteTravelPlanRepo.findByMember(realMember);
        System.out.println("查詢收藏筆數: " + list.size());


        return favoriteTravelPlanRepo.findByMember(realMember)
                .stream()
                .map(FavoriteTravelPlan::getTravelPlan)
                .collect(Collectors.toList());
    }

    // ✅ 是否已收藏
    @Override
	public boolean existsByMember_MemberIdAndTravelPlan_TravelPlanId(Integer memberId, Integer travelPlanId) {
		return favoriteTravelPlanRepo.existsByMember_MemberIdAndTravelPlan_TravelPlanId(memberId, travelPlanId);
	}

	public void deleteByMember_MemberIdAndTravelPlan_TravelPlanId(Integer memberId, Integer travelPlanId) {
		favoriteTravelPlanRepo.deleteByMember_MemberIdAndTravelPlan_TravelPlanId(memberId, travelPlanId);
	}

	// ✅ 儲存（for test）
    @Override
	public FavoriteTravelPlan save(FavoriteTravelPlan favorite) {
		return favoriteTravelPlanRepo.save(favorite);
	}

	 // ✅ 加入收藏
	@Override
	public void addFavorite(memVO member, TravelPlan travelPlan) {
	    Optional<FavoriteTravelPlan> existing = favoriteTravelPlanRepo.findByMemberAndTravelPlan(member, travelPlan);
	    if (existing.isEmpty()) {
	        FavoriteTravelPlan fav = new FavoriteTravelPlan();
	        fav.setMember(member);
	        fav.setTravelPlan(travelPlan);
	        favoriteTravelPlanRepo.save(fav);
	    }
	}
	// ✅ 移除收藏
	@Override
	@Transactional
	public void removeFavorite(memVO member, TravelPlan travelPlan) {
        favoriteTravelPlanRepo.deleteByMember_MemberIdAndTravelPlan_TravelPlanId(
                member.getMemberId(), travelPlan.getTravelPlanId()
        );
    }

	// ✅ 查詢某會員的所有收藏紀錄（完整實體）
	@Override
	public List<FavoriteTravelPlan> getFavoritesByMember(memVO member) {
        return favoriteTravelPlanRepo.findByMember(member);
    }

	@Override
	public boolean isFavorite(memVO member, TravelPlan plan) {
	    return favoriteTravelPlanRepo.existsByMember_MemberIdAndTravelPlan_TravelPlanId(
	        member.getMemberId(),
	        plan.getTravelPlanId()
	    );
	}

    
}
