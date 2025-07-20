package com.member.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "tourist")
public class TouristVO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tourist_id")
    private Integer touristId;

    @Column(name = "contact_number", nullable = false)
    private String contactNumber;

    @Column(name = "tourist_name", nullable = false)
    private String touristName;

    @Column(name = "tourist_email", nullable = false)
    private String touristEmail;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "member_account", nullable = false)
    private String memberAccount;
    
    @Column(name = "tourist_personal_id", nullable = false)
    private String touristPersonalId;
    
    private LocalDateTime createTime;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tour_order_id", referencedColumnName = "tour_order_id", nullable = false)
    private TourOrderVO tourOrder;


    // ✅ 一對多：主報名人 → 子旅客清單
    @OneToMany(mappedBy = "tourist", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TouristIdVO> touristIds;
    
//    @OneToMany(mappedBy = "tourist", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<TouristIdVO> touristIds;

	public TouristVO(Integer touristId, String memberAccount, String touristName, String touristEmail, String phone,
			TourOrderVO tourOrder,Set<TouristIdVO> touristIds,String contactNumber, String touristPersonalId,
			LocalDateTime createTime) {
		super();
		this.touristId = touristId;
		this.memberAccount = memberAccount;
		this.touristName = touristName;
		this.touristEmail = touristEmail;
		this.phone = phone;
		this.tourOrder = tourOrder;
		this.touristIds = touristIds;
		this.contactNumber = contactNumber;
		this.touristPersonalId = touristPersonalId;
		this.createTime = createTime;
	}
	public TouristVO(){};

    // Getter / Setter

    public Integer getTouristId() {
        return touristId;
    }

    public void setTouristId(Integer touristId) {
        this.touristId = touristId;
    }

    public String getMemberAccount() {
        return memberAccount;
    }

    public void setMemberAccount(String memberAccount) {
        this.memberAccount = memberAccount;
    }

    public String getTouristName() {
        return touristName;
    }

    public void setTouristName(String touristName) {
        this.touristName = touristName;
    }

    public String getTouristEmail() {
        return touristEmail;
    }

    public void setTouristEmail(String touristEmail) {
        this.touristEmail = touristEmail;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

	public TourOrderVO getTourOrder() {
		return tourOrder;
	}

	public void setTourOrder(TourOrderVO tourOrder) {
		this.tourOrder = tourOrder;
	}

	public Set<TouristIdVO> getTouristIds() {
		return touristIds;
	}

	public void setTouristIds(Set<TouristIdVO> touristIds) {
		this.touristIds = touristIds;
	}

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}
	

	public String getTouristPersonalId() {
		return touristPersonalId;
	}

	public void setTouristPersonalId(String touristPersonalId) {
		this.touristPersonalId = touristPersonalId;
	}
	public LocalDateTime getCreateTime() {
		return createTime;
	}
	public void setCreateTime(LocalDateTime createTime) {
		this.createTime = createTime;
	}
	
	

    
//    public List<TouristIdVO> getTouristIds() {
//		return touristIds;
//	}
//
//	public void setTouristIds(List<TouristIdVO> touristIds) {
//		this.touristIds = touristIds;
//	}
}