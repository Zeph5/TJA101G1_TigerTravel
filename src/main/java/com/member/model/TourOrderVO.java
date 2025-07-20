package com.member.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.travel_plan.model.TravelItinerary;

import jakarta.persistence.*;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "tour_order")
public class TourOrderVO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tour_order_id")
    private Integer tourOrderId;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private memVO member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "travel_itinerary_id")
    private TravelItinerary travelItinerary;

    private Integer tourPrice;
    private Integer totalAmount;
    private Integer totalAfterCoupon;
    private String tourOrderStatus;
    private Integer peopleCount;

    @CreatedDate
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    private String cardLastFour;
    private String cardExpiryDate;

    @OneToOne
    @JoinColumn(name = "tourist_id")
    private TouristVO tourist;

    @OneToMany(mappedBy = "tourOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TouristIdVO> tourists;
    
    public TourOrderVO() {};
    
    public TourOrderVO(Integer tourOrderId, memVO member, Integer tourPrice,
			Integer totalAmount, Integer totalAfterCoupon, String tourOrderStatus, LocalDateTime createTime,
			TravelItinerary travelItinerary,String cardExpiryDate,String cardLastFour,TouristVO tourist,
			Set<TouristIdVO> tourists) {
		super();
		this.tourOrderId = tourOrderId;
		this.member = member;
		this.tourPrice = tourPrice;
		this.totalAmount = totalAmount;
		this.totalAfterCoupon = totalAfterCoupon;
		this.tourOrderStatus = tourOrderStatus;
		this.createTime = createTime;
		this.travelItinerary = travelItinerary;
		this.tourist = tourist;
		this.cardExpiryDate = cardExpiryDate;
		this.cardLastFour = cardLastFour;
		this.tourists = tourists;
	}

	

	public Set<TouristIdVO> getTourists() {
		return tourists;
	}

	public void setTourists(Set<TouristIdVO> tourists) {
	    this.tourists = tourists;
	}

	public String getCardLastFour() {
		return cardLastFour;
	}

	public void setCardLastFour(String cardLastFour) {
		this.cardLastFour = cardLastFour;
	}

	public String getCardExpiryDate() {
		return cardExpiryDate;
	}

	public void setCardExpiryDate(String cardExpiryDate) {
		this.cardExpiryDate = cardExpiryDate;
	}

	public Integer getPeopleCount() {
        return peopleCount;
    }

	public TouristVO getTourist() {
		return tourist;
	}

	public void setTourist(TouristVO tourist) {
		this.tourist = tourist;
	}

	public void setPeopleCount(Integer peopleCount) {
		this.peopleCount = peopleCount;
	}

	// ===== Getter / Setter =====
	public Integer getTourOrderId() {
        return tourOrderId;
    }

    public void setTourOrderId(Integer tourOrderId) {
        this.tourOrderId = tourOrderId;
    }


    public memVO getMember() {
		return member;
	}

	public void setMember(memVO member) {
		this.member = member;
	}

    public Integer getTourPrice() {
        return tourPrice;
    }

    public void setTourPrice(Integer tourPrice) {
        this.tourPrice = tourPrice;
    }

    public Integer getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Integer totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Integer getTotalAfterCoupon() {
        return totalAfterCoupon;
    }

    public void setTotalAfterCoupon(Integer totalAfterCoupon) {
        this.totalAfterCoupon = totalAfterCoupon;
    }

    public String getTourOrderStatus() {
        return tourOrderStatus;
    }

    public void setTourOrderStatus(String tourOrderStatus) {
        this.tourOrderStatus = tourOrderStatus;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public TravelItinerary getTravelItinerary() {
        return travelItinerary;
    }

    public void setTravelItinerary(TravelItinerary travelItinerary) {
        this.travelItinerary = travelItinerary;
    }
}