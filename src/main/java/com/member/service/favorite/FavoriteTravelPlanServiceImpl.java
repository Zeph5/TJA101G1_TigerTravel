package com.member.service.favorite;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.member.model.FavoriteTravelPlan;
import com.member.model.FavoriteTravelPlanRepository;
import com.member.model.MemberRepository;
import com.member.model.memVO;
import com.travel_plan.model.TravelPlan;

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

	public boolean existsByMember_MemberIdAndTravelPlan_TravelPlanId(Integer memberId, Integer travelPlanId) {
		return favoriteTravelPlanRepo.existsByMember_MemberIdAndTravelPlan_TravelPlanId(memberId, travelPlanId);
	}

	public void deleteByMember_MemberIdAndTravelPlan_TravelPlanId(Integer memberId, Integer travelPlanId) {
		favoriteTravelPlanRepo.deleteByMember_MemberIdAndTravelPlan_TravelPlanId(memberId, travelPlanId);
	}

	public FavoriteTravelPlan save(FavoriteTravelPlan favorite) {
		return favoriteTravelPlanRepo.save(favorite);
	}
}
