package com.epam.esm.order.service;

import com.epam.esm.certificate.model.Certificate;
import com.epam.esm.order.dao.OrderDao;
import com.epam.esm.order.dto.OrderDto;
import com.epam.esm.order.exception.OrderNotFoundException;
import com.epam.esm.order.model.Order;
import com.epam.esm.user.dao.UserDao;
import com.epam.esm.user.exception.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
@Transactional
public class OrderService {
    private final OrderDao orderDao;
    private final ModelMapper modelMapper;
    private final UserDao userDao;

    @Autowired
    public OrderService(OrderDao orderDao,
                        ModelMapper modelMapper,
                        UserDao userDao) {
        this.modelMapper = modelMapper;
        this.orderDao = orderDao;
        this.userDao = userDao;
    }

    public OrderDto create(OrderDto orderDto, long userId) {
        orderDto.setUserId(userId);
        userDao.find(userId).orElseThrow(()->
                new UserNotFoundException("User with id " + userId + " doesn't exist"));
        Order order = modelMapper.map(orderDto, Order.class);
        BigDecimal totalPrice = order.getCertificates().stream()
                .map(Certificate::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalPrice(totalPrice);
        orderDao.create(order);
        return modelMapper.map(order, OrderDto.class);
    }

    public PagedModel<OrderDto> findByUserId(long userId, int page, int perPage) {
        List<OrderDto> orders = orderDao.getOrdersByUserId(userId, page, perPage).stream()
                .map(order -> modelMapper.map(order, OrderDto.class))
                .collect(Collectors.toList());
        int totalOrdersCount = orderDao.getCountOfUsersOrders(userId).intValue();
        PagedModel.PageMetadata pageMetadata = new PagedModel.PageMetadata(perPage, page, totalOrdersCount);
        return PagedModel.of(orders, pageMetadata);
    }

    public OrderDto getOrderByUserIdAndOrderId(long userId, long orderId) {
        userDao.find(userId).orElseThrow(()->
                new UserNotFoundException("User with id " + userId + " doesn't exist"));
        Order order = orderDao.findOrderByUserIdAndOrderId(userId, orderId).orElseThrow(()->
                new OrderNotFoundException("User with id " + userId + " doesn't have order with id " + orderId));
        return modelMapper.map(order, OrderDto.class);
    }
}
