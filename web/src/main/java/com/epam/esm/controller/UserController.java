package com.epam.esm.controller;

import com.epam.esm.hateoasutils.OrderHateoasUtil;
import com.epam.esm.hateoasutils.UserHateoasUtil;
import com.epam.esm.order.dto.OrderDto;
import com.epam.esm.order.service.OrderService;
import com.epam.esm.user.dto.UserDto;
import com.epam.esm.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@Validated
@RestController
@RequestMapping(value = "/api/v1/users")
public class UserController {
    private final UserService userService;
    private final OrderService orderService;
    private final UserHateoasUtil userHateoasUtil;
    private final OrderHateoasUtil orderHateoasUtil;

    @Autowired
    public UserController(UserService userService,
                          OrderService orderService,
                          UserHateoasUtil userHateoasUtil,
                          OrderHateoasUtil orderHateoasUtil) {
        this.userService = userService;
        this.orderService = orderService;
        this.userHateoasUtil = userHateoasUtil;
        this.orderHateoasUtil = orderHateoasUtil;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public PagedModel<UserDto> findUsers(@RequestParam(name = "page", required = false, defaultValue = "1")
                              @Min(value = 1, message = "page number must be greater or equal to 1")
                                      Integer page,
                                        @RequestParam(name = "perPage", required = false, defaultValue = "50")
                              @Min(value = 1, message = "perPage param must be greater or equal to 1")
                                      Integer perPage) {
        PagedModel<UserDto> model =  userService.findUsers(page, perPage);
        userHateoasUtil.createPaginationLinks(model);
        return model;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        UserDto created = userService.create(userDto);
        return userHateoasUtil.createSingleUserLinks(created);
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto update(@Valid @RequestBody UserDto userDto, @PathVariable("id") long id) {
        UserDto updated = userService.update(userDto,id);
        return userHateoasUtil.createSingleUserLinks(updated);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id")long id) {
        userService.delete(id);
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto findById(@PathVariable("id")long id) {
        UserDto userDto = userService.find(id);
        return userHateoasUtil.createSingleUserLinks(userDto);
    }

    @PostMapping(value = "/{id}/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDto makeOrder(@Valid @RequestBody OrderDto orderDto, @PathVariable("id") long userId) {
        return orderService.create(orderDto, userId);
    }

    @GetMapping(value = "/{id}/orders")
    @ResponseStatus(HttpStatus.OK)
    public PagedModel<OrderDto> getUserOrders(@RequestParam(name = "page", required = false, defaultValue = "1")
                                            @Min(value = 1, message = "page number must be greater or equal to 1")
                                                    Integer page,
                                              @RequestParam(name = "perPage", required = false, defaultValue = "50")
                                            @Min(value = 1, message = "perPage param must be greater or equal to 1")
                                                    Integer perPage,
                                              @PathVariable("id")long userId) {
        PagedModel<OrderDto> model = orderService.findByUserId(userId, page, perPage);
        orderHateoasUtil.createPaginationLinks(model, userId);
        return model;
    }

    @GetMapping(value = "/{id}/orders/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    public OrderDto getUserOrderById(@PathVariable("id") long userId, @PathVariable("orderId")long orderId) {
        OrderDto orderDto = orderService.getOrderByUserIdAndOrderId(userId, orderId);
        return orderHateoasUtil.createSelfRel(orderDto, userId);
    }
}
