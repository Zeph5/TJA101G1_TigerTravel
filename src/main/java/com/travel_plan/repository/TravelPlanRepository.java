package com.travel_plan.repository;




import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import com.travel_plan.model.TravelItinerary;
import com.travel_plan.model.TravelPlan;
import com.travel_plan.model.TravelPlanDay;


@Repository
public interface TravelPlanRepository extends JpaRepository<TravelPlan, Integer> {

	List<TravelPlan> findByTravelTitleContaining(String keyword);
	
	Page<TravelPlan> findByTravelTitleContaining(String keyword, Pageable pageable);
	
	List<TravelPlan> findTop6ByOrderByTravelPlanIdDesc();
	
	Optional<TravelPlan> findById(Integer id);

}
