package com.member.model;

import com.travel_plan.model.TravelPlan;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name= "favorite_tour",uniqueConstraints = {
	    @UniqueConstraint(columnNames = {"member_id", "travel_plan_id"})
})
public class FavoriteTravelPlan {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "favorite_tour_id")
	private Integer id;
	
	//關聯會員
	@ManyToOne
	@JoinColumn(name = "member_id", nullable = false)
	private memVO member;
	
	//關聯旅行行程
	@ManyToOne
	@JoinColumn(name = "travel_plan_id" , nullable = false)
	private TravelPlan travelPlan;
	
	public FavoriteTravelPlan() {};
	
	public FavoriteTravelPlan(Integer id, memVO member, TravelPlan travelPlan) {
		super();
		this.id = id;
		this.member = member;
		this.travelPlan = travelPlan;
	}

	//Getter & Setter

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public memVO getMember() {
		return member;
	}

	public void setMember(memVO member) {
		this.member = member;
	}

	public TravelPlan getTravelPlan() {
		return travelPlan;
	}

	public void setTravelPlan(TravelPlan travelPlan) {
		this.travelPlan = travelPlan;
	}
	
}
