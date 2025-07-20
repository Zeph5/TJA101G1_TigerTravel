package com.member.model;

import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "tourist_id")
public class TouristIdVO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_order_id", nullable = false)
    private TourOrderVO tourOrder;

    @Column(name = "tourist_name", nullable = false, length = 50)
    private String touristName;

    @Column(name = "tourist_personal_id", nullable = false, length = 20)
    private String touristPersonalId;

    @Column(name = "contact_number", nullable = false, length = 20)
    private String contactNumber;

    @CreationTimestamp
    @Column(name = "create_time", nullable = false, updatable = false)
    private Timestamp createTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tourist_id", nullable = true)
    private TouristVO tourist;
    
	public TouristIdVO() {}
    
    public TouristIdVO(Integer id, TourOrderVO tourOrder, String touristName, String touristPersonalId,
			String contactNumber, Timestamp createTime,TouristVO tourist) {
    	super();
		this.id = id;
		this.tourOrder = tourOrder;
		this.touristName = touristName;
		this.touristPersonalId = touristPersonalId;
		this.contactNumber = contactNumber;
		this.createTime = createTime;
		this.tourist = tourist;
	}

	public Integer getId(){
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public TourOrderVO getTourOrder() {
        return tourOrder;
    }

    public void setTourOrder(TourOrderVO tourOrder) {
    	this.tourOrder = tourOrder;
    	tourOrder.getTourists().add(this); // 保持雙向一致性
    }

    public String getTouristName() {
        return touristName;
    }

    public void setTouristName(String touristName) {
        this.touristName = touristName;
    }

    public String getTouristPersonalId() {
        return touristPersonalId;
    }

    public void setTouristPersonalId(String touristPersonalId) {
        this.touristPersonalId = touristPersonalId;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "TouristIdVO [touristId=" + id + ", tourOrder=" + tourOrder + ", touristName=" + touristName
                + ", touristPersonalId=" + touristPersonalId + ", contactNumber=" + contactNumber + ", createTime="
                + createTime + "]";
    }
    
    public TouristVO getTourist() {
		return tourist;
	}

	public void setTourist(TouristVO tourist) {
		this.tourist = tourist;
	}
}