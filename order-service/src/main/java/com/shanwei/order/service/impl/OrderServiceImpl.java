package com.shanwei.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shanwei.common.ApiResponse;
import com.shanwei.common.BizException;
import com.shanwei.order.config.RabbitConfig;
import com.shanwei.order.dto.CommentRequest;
import com.shanwei.order.dto.OrderCreateRequest;
import com.shanwei.order.dto.RequestCreateRequest;
import com.shanwei.order.entity.Comment;
import com.shanwei.order.entity.ServiceOrder;
import com.shanwei.order.entity.ServiceRequest;
import com.shanwei.order.feign.SkillClient;
import com.shanwei.order.feign.UserClient;
import com.shanwei.order.mapper.CommentMapper;
import com.shanwei.order.mapper.ServiceOrderMapper;
import com.shanwei.order.mapper.ServiceRequestMapper;
import com.shanwei.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final ServiceRequestMapper requestMapper;
    private final ServiceOrderMapper orderMapper;
    private final CommentMapper commentMapper;
    private final SkillClient skillClient;
    private final UserClient userClient;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public ServiceRequest publishRequest(RequestCreateRequest request) {
        ServiceRequest entity = new ServiceRequest();
        entity.setUserId(request.getUserId());
        entity.setTitle(request.getTitle());
        entity.setDescription(request.getDescription());
        entity.setCategory(request.getCategory());
        entity.setBudget(request.getBudget());
        entity.setDeadline(request.getDeadline());
        entity.setStatus("OPEN");
        entity.setCreateTime(LocalDateTime.now());
        requestMapper.insert(entity);
        return entity;
    }

    @Override
    public List<ServiceRequest> requestHall() {
        return requestMapper.selectList(new LambdaQueryWrapper<ServiceRequest>()
                .eq(ServiceRequest::getStatus, "OPEN")
                .orderByDesc(ServiceRequest::getCreateTime));
    }

    @Override
    public List<ServiceRequest> allRequests() {
        return requestMapper.selectList(new LambdaQueryWrapper<ServiceRequest>().orderByDesc(ServiceRequest::getCreateTime));
    }

    @Override
    public ServiceRequest requestDetail(Long id) {
        return findRequest(id);
    }

    @Override
    public List<ServiceRequest> userRequests(Long userId) {
        return requestMapper.selectList(new LambdaQueryWrapper<ServiceRequest>()
                .eq(ServiceRequest::getUserId, userId)
                .orderByDesc(ServiceRequest::getCreateTime));
    }

    @Override
    public ServiceRequest updateRequest(Long id, RequestCreateRequest request) {
        ServiceRequest entity = findRequest(id);
        entity.setTitle(request.getTitle());
        entity.setDescription(request.getDescription());
        entity.setCategory(request.getCategory());
        entity.setBudget(request.getBudget());
        entity.setDeadline(request.getDeadline());
        requestMapper.updateById(entity);
        return entity;
    }

    @Override
    public void deleteRequest(Long id) {
        ServiceRequest request = findRequest(id);
        requestMapper.deleteById(request.getRequestId());
    }

    @Override
    public ServiceRequest changeRequestStatus(Long id, String status) {
        ServiceRequest request = findRequest(id);
        request.setStatus(status);
        requestMapper.updateById(request);
        return request;
    }

    @Override
    public ServiceRequest acceptRequest(Long id, Long acceptUserId) {
        ServiceRequest request = findRequest(id);
        if (!"OPEN".equals(request.getStatus())) {
            throw new BizException("该需求已被接收或已关闭");
        }
        if (request.getUserId().equals(acceptUserId)) {
            throw new BizException("不能接收自己发布的需求");
        }

        Map<String, Object> publisher = loadUser(request.getUserId());
        Map<String, Object> accepter = loadUser(acceptUserId);
        request.setStatus("ACCEPTED");
        requestMapper.updateById(request);

        rabbitTemplate.convertAndSend(RabbitConfig.ORDER_NOTIFY_QUEUE, Map.of(
                "toUser", request.getUserId(),
                "title", "需求已被接收",
                "content", "你的需求《" + request.getTitle() + "》已被接收。接收者联系方式：" + contactText(accepter)
        ));
        rabbitTemplate.convertAndSend(RabbitConfig.ORDER_NOTIFY_QUEUE, Map.of(
                "toUser", acceptUserId,
                "title", "需求接收成功",
                "content", "你已接收需求《" + request.getTitle() + "》。发布者联系方式：" + contactText(publisher)
        ));
        return request;
    }

    @Override
    public ServiceOrder createOrder(OrderCreateRequest request) {
        Map<String, Object> skill = loadSkill(request.getSkillId());
        ServiceOrder order = new ServiceOrder();
        order.setSkillId(request.getSkillId());
        order.setBuyerId(request.getBuyerId());
        order.setSellerId(Long.valueOf(String.valueOf(skill.get("userId"))));
        order.setStatus("PENDING");
        order.setRemark(request.getRemark());
        order.setCreateTime(LocalDateTime.now());
        orderMapper.insert(order);
        rabbitTemplate.convertAndSend(RabbitConfig.ORDER_NOTIFY_QUEUE, Map.of(
                "toUser", order.getSellerId(),
                "title", "新的技能预约",
                "content", "你收到一个新的技能预约请求：" + skill.get("skillName")
        ));
        return order;
    }

    @Override
    public List<ServiceOrder> userOrders(Long userId) {
        return orderMapper.selectList(new LambdaQueryWrapper<ServiceOrder>()
                .eq(ServiceOrder::getBuyerId, userId)
                .or()
                .eq(ServiceOrder::getSellerId, userId)
                .orderByDesc(ServiceOrder::getCreateTime));
    }

    @Override
    public List<ServiceOrder> allOrders() {
        return orderMapper.selectList(new LambdaQueryWrapper<ServiceOrder>().orderByDesc(ServiceOrder::getCreateTime));
    }

    @Override
    public ServiceOrder acceptOrder(Long id, Long sellerId) {
        ServiceOrder order = findOrder(id);
        if (!order.getSellerId().equals(sellerId)) {
            throw new BizException("只能由服务发布者接收预约");
        }
        if (!"PENDING".equals(order.getStatus())) {
            throw new BizException("当前预约不能接收");
        }
        Map<String, Object> skill = loadSkill(order.getSkillId());
        Map<String, Object> buyer = loadUser(order.getBuyerId());
        Map<String, Object> seller = loadUser(order.getSellerId());
        order.setStatus("ACCEPTED");
        orderMapper.updateById(order);
        rabbitTemplate.convertAndSend(RabbitConfig.ORDER_NOTIFY_QUEUE, Map.of(
                "toUser", order.getBuyerId(),
                "title", "预约已被接收",
                "content", "你预约的技能《" + skill.get("skillName") + "》已被接收。服务方联系方式：" + contactText(seller)
        ));
        rabbitTemplate.convertAndSend(RabbitConfig.ORDER_NOTIFY_QUEUE, Map.of(
                "toUser", order.getSellerId(),
                "title", "预约接收成功",
                "content", "你已接收技能预约《" + skill.get("skillName") + "》。预约方联系方式：" + contactText(buyer)
        ));
        return order;
    }

    @Override
    public ServiceOrder confirmOrder(Long id, Long buyerId) {
        ServiceOrder order = findOrder(id);
        if (!order.getBuyerId().equals(buyerId)) {
            throw new BizException("只能由预约发布者确认预约");
        }
        if (!"ACCEPTED".equals(order.getStatus())) {
            throw new BizException("当前预约不能确认");
        }
        Map<String, Object> skill = loadSkill(order.getSkillId());
        order.setStatus("CONFIRMED");
        orderMapper.updateById(order);
        rabbitTemplate.convertAndSend(RabbitConfig.ORDER_NOTIFY_QUEUE, Map.of(
                "toUser", order.getSellerId(),
                "title", "预约已确认",
                "content", "预约方已确认技能预约《" + skill.get("skillName") + "》，可以开始服务。"
        ));
        return order;
    }

    @Override
    public ServiceOrder finishOrder(Long id, Long sellerId) {
        ServiceOrder order = findOrder(id);
        if (!order.getSellerId().equals(sellerId)) {
            throw new BizException("只能由服务发布者确认完成服务");
        }
        if (!"CONFIRMED".equals(order.getStatus())) {
            throw new BizException("当前预约不能确认完成");
        }
        Map<String, Object> skill = loadSkill(order.getSkillId());
        order.setStatus("FINISHED");
        orderMapper.updateById(order);
        rabbitTemplate.convertAndSend(RabbitConfig.ORDER_NOTIFY_QUEUE, Map.of(
                "toUser", order.getBuyerId(),
                "title", "服务已完成",
                "content", "服务方已确认完成技能服务《" + skill.get("skillName") + "》，请进行评分和评价。"
        ));
        return order;
    }

    @Override
    public ServiceOrder changeOrderStatus(Long id, String status) {
        ServiceOrder order = findOrder(id);
        order.setStatus(status);
        orderMapper.updateById(order);
        return order;
    }

    @Override
    public Comment comment(CommentRequest request) {
        ServiceOrder order = findOrder(request.getOrderId());
        if (!"FINISHED".equals(order.getStatus())) {
            throw new BizException("服务完成后才能评价");
        }
        if (!order.getBuyerId().equals(request.getFromUser())) {
            throw new BizException("只能由预约发布者评价");
        }
        if (request.getScore() == null || request.getScore() < 1 || request.getScore() > 5) {
            throw new BizException("评分必须在 1 到 5 之间");
        }
        Comment comment = new Comment();
        comment.setOrderId(request.getOrderId());
        comment.setFromUser(request.getFromUser());
        comment.setToUser(order.getSellerId());
        comment.setScore(request.getScore());
        comment.setContent(request.getContent());
        comment.setTime(LocalDateTime.now());
        commentMapper.insert(comment);
        order.setStatus("DONE");
        orderMapper.updateById(order);
        rabbitTemplate.convertAndSend(RabbitConfig.COMMENT_NOTIFY_QUEUE, Map.of(
                "toUser", order.getSellerId(),
                "title", "新的评价",
                "content", "你的技能获得新评价：" + request.getContent()
        ));
        return comment;
    }

    @Override
    public List<Comment> userComments(Long userId) {
        return commentMapper.selectList(new LambdaQueryWrapper<Comment>()
                .eq(Comment::getFromUser, userId)
                .or()
                .eq(Comment::getToUser, userId)
                .orderByDesc(Comment::getTime));
    }

    @Override
    public List<Comment> allComments() {
        return commentMapper.selectList(new LambdaQueryWrapper<Comment>().orderByDesc(Comment::getTime));
    }

    @Override
    public void deleteComment(Long id) {
        Comment comment = commentMapper.selectById(id);
        if (comment == null) {
            throw new BizException("评价不存在");
        }
        commentMapper.deleteById(id);
    }

    private ServiceRequest findRequest(Long id) {
        ServiceRequest request = requestMapper.selectById(id);
        if (request == null) {
            throw new BizException("需求不存在");
        }
        return request;
    }

    private ServiceOrder findOrder(Long id) {
        ServiceOrder order = orderMapper.selectById(id);
        if (order == null) {
            throw new BizException("预约不存在");
        }
        return order;
    }

    private Map<String, Object> loadSkill(Long skillId) {
        Map<String, Object> skill = skillClient.detail(skillId).getData();
        if (skill == null) {
            throw new BizException("技能不存在，无法预约");
        }
        return skill;
    }

    private Map<String, Object> loadUser(Long userId) {
        ApiResponse<Map<String, Object>> response = userClient.publicUser(userId);
        if (response == null || response.getData() == null) {
            throw new BizException("用户信息不存在");
        }
        return response.getData();
    }

    private String contactText(Map<String, Object> user) {
        return value(user, "nickname") + "，邮箱：" + value(user, "email") + "，手机号：" + value(user, "phone");
    }

    private String value(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (value == null || String.valueOf(value).isBlank()) {
            return "未填写";
        }
        return String.valueOf(value);
    }
}
