package com.shanwei.order.controller;

import com.shanwei.common.ApiResponse;
import com.shanwei.order.dto.CommentRequest;
import com.shanwei.order.dto.OrderCreateRequest;
import com.shanwei.order.dto.RequestCreateRequest;
import com.shanwei.order.entity.Comment;
import com.shanwei.order.entity.ServiceOrder;
import com.shanwei.order.entity.ServiceRequest;
import com.shanwei.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/requests")
    public ApiResponse<ServiceRequest> publishRequest(@RequestBody RequestCreateRequest request) {
        return ApiResponse.success(orderService.publishRequest(request));
    }

    @GetMapping("/requests")
    public ApiResponse<List<ServiceRequest>> requestHall() {
        return ApiResponse.success(orderService.requestHall());
    }

    @GetMapping("/admin/requests")
    public ApiResponse<List<ServiceRequest>> allRequests() {
        return ApiResponse.success(orderService.allRequests());
    }

    @GetMapping("/requests/{id}")
    public ApiResponse<ServiceRequest> requestDetail(@PathVariable Long id) {
        return ApiResponse.success(orderService.requestDetail(id));
    }

    @GetMapping("/requests/user/{userId}")
    public ApiResponse<List<ServiceRequest>> userRequests(@PathVariable Long userId) {
        return ApiResponse.success(orderService.userRequests(userId));
    }

    @PutMapping("/requests/{id}")
    public ApiResponse<ServiceRequest> updateRequest(@PathVariable Long id, @RequestBody RequestCreateRequest request) {
        return ApiResponse.success(orderService.updateRequest(id, request));
    }

    @DeleteMapping("/requests/{id}")
    public ApiResponse<Void> deleteRequest(@PathVariable Long id) {
        orderService.deleteRequest(id);
        return ApiResponse.success();
    }

    @PutMapping("/requests/{id}/status")
    public ApiResponse<ServiceRequest> changeRequestStatus(@PathVariable Long id, @RequestParam String status) {
        return ApiResponse.success(orderService.changeRequestStatus(id, status));
    }

    @PutMapping("/requests/{id}/accept")
    public ApiResponse<ServiceRequest> acceptRequest(@PathVariable Long id, @RequestParam Long acceptUserId) {
        return ApiResponse.success(orderService.acceptRequest(id, acceptUserId));
    }

    @PostMapping("/orders")
    public ApiResponse<ServiceOrder> createOrder(@RequestBody OrderCreateRequest request) {
        return ApiResponse.success(orderService.createOrder(request));
    }

    @GetMapping("/orders/user/{userId}")
    public ApiResponse<List<ServiceOrder>> userOrders(@PathVariable Long userId) {
        return ApiResponse.success(orderService.userOrders(userId));
    }

    @GetMapping("/admin/orders")
    public ApiResponse<List<ServiceOrder>> allOrders() {
        return ApiResponse.success(orderService.allOrders());
    }

    @PutMapping("/orders/{id}/accept")
    public ApiResponse<ServiceOrder> acceptOrder(@PathVariable Long id, @RequestParam Long sellerId) {
        return ApiResponse.success(orderService.acceptOrder(id, sellerId));
    }

    @PutMapping("/orders/{id}/confirm")
    public ApiResponse<ServiceOrder> confirmOrder(@PathVariable Long id, @RequestParam Long buyerId) {
        return ApiResponse.success(orderService.confirmOrder(id, buyerId));
    }

    @PutMapping("/orders/{id}/finish")
    public ApiResponse<ServiceOrder> finishOrder(@PathVariable Long id, @RequestParam Long sellerId) {
        return ApiResponse.success(orderService.finishOrder(id, sellerId));
    }

    @PutMapping("/orders/{id}/status")
    public ApiResponse<ServiceOrder> changeOrderStatus(@PathVariable Long id, @RequestParam String status) {
        return ApiResponse.success(orderService.changeOrderStatus(id, status));
    }

    @PostMapping("/comments")
    public ApiResponse<Comment> comment(@RequestBody CommentRequest request) {
        return ApiResponse.success(orderService.comment(request));
    }

    @GetMapping("/comments/user/{userId}")
    public ApiResponse<List<Comment>> userComments(@PathVariable Long userId) {
        return ApiResponse.success(orderService.userComments(userId));
    }

    @GetMapping("/admin/comments")
    public ApiResponse<List<Comment>> allComments() {
        return ApiResponse.success(orderService.allComments());
    }

    @DeleteMapping("/comments/{id}")
    public ApiResponse<Void> deleteComment(@PathVariable Long id) {
        orderService.deleteComment(id);
        return ApiResponse.success();
    }
}
