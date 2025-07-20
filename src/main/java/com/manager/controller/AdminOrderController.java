package com.manager.controller;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.manager.model.DTO.OrderListDTO;
import com.manager.model.DTO.TouristListDTO;
import com.manager.service.AdminOrderService;
import com.member.model.TourOrderVO;
import com.member.model.TouristVO;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderController {
	private final AdminOrderService adminOrderService;
	public AdminOrderController(AdminOrderService adminOrderService) {
		this.adminOrderService = adminOrderService;
	}
	@GetMapping("/list")
	public String showOrderList(Model model) {
		List<OrderListDTO> orderList = adminOrderService.findAllOrders();
		model.addAttribute("orders", orderList);
		return "admin/order/order_List"; // 返回訂單列表頁面
	}
	@GetMapping("/{id}")
	public String showOrderDetail(@PathVariable Integer id, Model model) {		
		OrderListDTO order = adminOrderService.findOrderById(id);// 查訂單 DTO
		TourOrderVO tourOrderVO = adminOrderService.findOrderEntityById(id); // 查實體（為了取旅客）
		
		Set<TouristVO> tourist= tourOrderVO.getTourists(); // 取得訂單中的旅客列表
		
		model.addAttribute("tourists", tourist);
		model.addAttribute("order", order);
		System.out.println("旅客數：" + tourOrderVO.getTourists().size());
		for (TouristVO t : tourOrderVO.getTourists()) {
		    System.out.println(t.getTouristName());
		}
		
		return "admin/order/order_Detail"; 
	}
	
	@GetMapping("/edit/{id}")
	public String showEditForm(@PathVariable Integer id, Model model) {
		OrderListDTO order = adminOrderService.findOrderById(id);
		model.addAttribute("order", order);
		return "admin/order/order_Edit"; // 返回編輯訂單頁面
	}
	@PostMapping("/edit/{id}")
	public String updateOrder(@PathVariable Integer id, @ModelAttribute("order") OrderListDTO updatedOrder) {
		adminOrderService.updateOrder(id, updatedOrder);
		return "redirect:/admin/orders/" + id; // 重定向到訂單詳細頁面
	}
	
}
