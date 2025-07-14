package com.member.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.member.model.FavoriteTravelPlan;
import com.member.model.FavoriteTravelPlanRepository;
import com.member.model.memVO;
import com.travel_plan.model.TravelPlan;

@Controller
public class TestController {

    @Autowired
    private FavoriteTravelPlanRepository favoriteRepo;

    @GetMapping("/test-favorites")
    @ResponseBody
    public String testFavorite() {
        memVO member = new memVO();
        member.setMemberId(1); // 測試用會員 ID

        List<FavoriteTravelPlan> favorites = favoriteRepo.findByMember(member);

        if (favorites.isEmpty()) return "沒有收藏任何旅程";

        StringBuilder sb = new StringBuilder("收藏的旅程：<br>");
        for (FavoriteTravelPlan fav : favorites) {
            TravelPlan plan = fav.getTravelPlan();
            if (plan != null) {
                sb.append("✔️ ID: ").append(plan.getTravelPlanId())
                  .append("，標題：").append(plan.getTravelTitle())
                  .append("<br>");
            } else {
                sb.append("⚠️ 此收藏沒有對應旅程資料<br>");
            }
        }

        return sb.toString();
    }
}