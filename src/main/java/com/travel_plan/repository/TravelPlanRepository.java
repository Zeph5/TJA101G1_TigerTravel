package com.travel_plan.repository;




import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.travel_plan.model.TravelItinerary;
import com.travel_plan.model.TravelPlan;


@Repository
public interface TravelPlanRepository extends JpaRepository<TravelPlan, Integer> {


	

}
