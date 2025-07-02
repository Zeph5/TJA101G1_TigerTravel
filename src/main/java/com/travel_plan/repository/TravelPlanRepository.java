package com.travel_plan.repository;




import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.travel_plan.model.TravelPlan;


@Repository
public interface TravelPlanRepository extends JpaRepository<TravelPlan, Integer> {

	

}
