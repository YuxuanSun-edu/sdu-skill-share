package com.shanwei.order.service;

import com.shanwei.order.dto.CommentRequest;
import com.shanwei.order.dto.OrderCreateRequest;
import com.shanwei.order.dto.RequestCreateRequest;
import com.shanwei.order.entity.Comment;
import com.shanwei.order.entity.ServiceOrder;
import com.shanwei.order.entity.ServiceRequest;

import java.util.List;

public interface OrderService {
    ServiceRequest publishRequest(RequestCreateRequest request);

    List<ServiceRequest> requestHall();

    List<ServiceRequest> allRequests();

    ServiceRequest requestDetail(Long id);

    List<ServiceRequest> userRequests(Long userId);

    ServiceRequest updateRequest(Long id, RequestCreateRequest request);

    void deleteRequest(Long id);

    ServiceRequest changeRequestStatus(Long id, String status);

    ServiceRequest acceptRequest(Long id, Long acceptUserId);

    ServiceOrder createOrder(OrderCreateRequest request);

    List<ServiceOrder> userOrders(Long userId);

    List<ServiceOrder> allOrders();

    ServiceOrder acceptOrder(Long id, Long sellerId);

    ServiceOrder confirmOrder(Long id, Long buyerId);

    ServiceOrder finishOrder(Long id, Long sellerId);

    ServiceOrder changeOrderStatus(Long id, String status);

    Comment comment(CommentRequest request);

    List<Comment> allComments();

    void deleteComment(Long id);

    List<Comment> userComments(Long userId);
}
