package com.epam.esm.order;

import com.epam.esm.service.Service;
import com.epam.esm.service.ServiceStatus;
import com.epam.esm.user.User;
import com.epam.esm.user.UserDao;
import com.epam.esm.user.UserNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.PagedModel;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class OrderServiceTest {
    @InjectMocks
    OrderService orderService;
    @Mock
    OrderDao orderDao;
    @Mock
    UserDao userDao;
    @Spy
    ModelMapper modelMapper;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void create_nonexistentUserId_shouldThrowException() {
        //Given
        doAnswer(invocation -> Optional.empty()).when(userDao)
                .find(anyLong());
        long id = 1;

        //When
        Assertions.assertThrows(UserNotFoundException.class,
                ()-> orderService.create(new OrderDto(), id));
    }

    @Test
    public void create_orderWithNonactiveCertificate_shouldThrowException() {
        //Given
        Service service = new Service("name", "description", new BigDecimal("12.6"),
                5);
        service.setStatus(ServiceStatus.PUBLISHED);
        service.setTags(Collections.emptyList());
        doAnswer(invocation -> {
            Order order = new Order();
            order.setServices(Collections.singletonList(service));
            return order;
        }).when(modelMapper).map(any(OrderDto.class),eq(Order.class));
        doAnswer(invocation -> Optional.of(new User())).when(userDao)
                .find(anyLong());
        OrderDto orderDto = new OrderDto();
        orderDto.setCertificatesIds(Collections.singletonList(1L));

        //When
        Assertions.assertThrows(OrderConflictException.class,
                ()->orderService.create(orderDto, 1));
    }

    @Test
    public void create_correctOrder_shouldCallDaoCreate() {
        //Given
        Service service = new Service("name", "description", new BigDecimal("12.6"),
                5);
        service.setStatus(ServiceStatus.ACTIVE);
        service.setTags(Collections.emptyList());
        doAnswer(invocation -> {
            Order order = new Order();
            order.setServices(Collections.singletonList(service));
            return order;
        }).when(modelMapper).map(any(OrderDto.class),eq(Order.class));
        doAnswer(invocation -> Optional.of(new User())).when(userDao)
                .find(anyLong());
        OrderDto orderDto = new OrderDto();
        orderDto.setCertificatesIds(Collections.singletonList(1L));

        //When
        orderService.create(orderDto, 1L);

        //Then
        verify(orderDao, times(1)).create(notNull());
    }

    @Test
    public void create_correctOrder_shouldReturnOrderWithCorrectTotalCost() {
        //Given
        Service service = new Service("name", "description", new BigDecimal("12.6"),
                5);
        service.setStatus(ServiceStatus.ACTIVE);
        service.setTags(Collections.emptyList());
        Service serviceTwo = new Service("name 2", "description 2", new BigDecimal("10.0"),
                5);
        service.setStatus(ServiceStatus.ACTIVE);
        service.setTags(Collections.emptyList());
        doAnswer(invocation -> {
            Order order = new Order();
            order.setServices(Arrays.asList(service, serviceTwo));
            return order;
        }).when(modelMapper).map(any(OrderDto.class),eq(Order.class));
        doAnswer(invocation -> Optional.of(new User())).when(userDao)
                .find(anyLong());
        OrderDto orderDto = new OrderDto();
        orderDto.setCertificatesIds(Collections.singletonList(1L));
        BigDecimal expected = new BigDecimal("22.6");

        //When
        orderDto = orderService.create(orderDto, 1L);

        //Then
        assertEquals(expected, orderDto.getTotalPrice());
    }

    @Test
    public void findByUserId_correctId_shouldReturnCorrectPageMetadata() {
        //Given
        int page = 1;
        int perPage = 50;
        doAnswer(invocation -> {
            Order orderOne = new Order();
            Order orderTwo = new Order();
            return Arrays.asList(orderOne, orderTwo);
        }).when(orderDao).getOrdersByUserId(anyLong(), anyInt(), anyInt());
        doAnswer(invocation -> 2L).when(orderDao).getCountOfUsersOrders(anyLong());
        PagedModel.PageMetadata expected = new PagedModel.PageMetadata(perPage, page, 2);

        //When
        PagedModel<OrderDto> model = orderService.findByUserId(1, page, perPage);

        //Then
        assertEquals(expected, model.getMetadata());
    }
}
