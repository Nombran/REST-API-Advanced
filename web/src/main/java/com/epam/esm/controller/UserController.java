package com.epam.esm.controller;

import com.epam.esm.order.dto.OrderDto;
import com.epam.esm.order.service.OrderService;
import com.epam.esm.user.dto.UserDto;
import com.epam.esm.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@Validated
@RestController
@RequestMapping(value = "/api/v1/users")
public class UserController {
    private final UserService userService;
    private final OrderService orderService;

    @Autowired
    public UserController(UserService userService,
                          OrderService orderService) {
        this.userService = userService;
        this.orderService = orderService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> findUsers(@RequestParam(name = "page", required = false, defaultValue = "1")
                              @Min(value = 1, message = "page number must be greater or equal to 1")
                                      Integer page,
                                   @RequestParam(name = "perPage", required = false, defaultValue = "50")
                              @Min(value = 1, message = "perPage param must be greater or equal to 1")
                                      Integer perPage) {
        return userService.findUsers(page, perPage);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@Valid @RequestBody UserDto userDto) {
        userService.create(userDto);
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void update(@Valid @RequestBody UserDto userDto, @PathVariable("id") long id) {
        userDto.setId(id);
        userService.update(userDto);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id")long id) {
        userService.delete(id);
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto findById(@PathVariable("id")long id) {
        return userService.find(id);
    }

    @PostMapping(value = "/{id}/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public void makeOrder(@Valid @RequestBody OrderDto orderDto, @PathVariable("id") long userId) {
        orderDto.setUserId(userId);
        orderService.create(orderDto);
    }

    @GetMapping(value = "/{id}/orders")
    @ResponseStatus(HttpStatus.OK)
    public List<OrderDto> getUserOrders(@PathVariable("id")long userId) {
        return orderService.findByUserId(userId);
    }

    @GetMapping(value = "/{id}/orders/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    public OrderDto getUserOrderById(@PathVariable("id") long userId, @PathVariable("orderId")long orderId) {
        return orderService.getOrderByUserIdAndOrderId(userId, orderId);
    }
}
