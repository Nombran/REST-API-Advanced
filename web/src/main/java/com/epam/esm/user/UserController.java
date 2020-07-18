package com.epam.esm.user;

import com.epam.esm.order.OrderHateoasUtil;
import com.epam.esm.order.OrderDto;
import com.epam.esm.order.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @Secured("ROLE_ADMIN")
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

    @PreAuthorize("authentication.principal.id == #id")
    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto update(@Valid @RequestBody UserDto userDto, @PathVariable("id") long id) {
        UserDto updated = userService.update(userDto,id);
        return userHateoasUtil.createSingleUserLinks(updated);
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_ADMIN') or authentication.principal.id == #id")
    public UserDto findById(@PathVariable("id")long id) {
        UserDto userDto = userService.find(id);
        return userHateoasUtil.createSingleUserLinks(userDto);
    }

    @PreAuthorize("authentication.principal.id == #userId")
    @PostMapping(value = "/{id}/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDto makeOrder(@Valid @RequestBody OrderDto orderDto, @PathVariable("id") long userId) {
        return orderService.create(orderDto, userId);
    }

    @GetMapping(value = "/{id}/orders")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_ADMIN') or authentication.principal.id == #userId")
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
    @PreAuthorize("hasRole('ROLE_ADMIN') or authentication.principal.id == #userId")
    public OrderDto getUserOrderById(@PathVariable("id") long userId, @PathVariable("orderId")long orderId) {
        OrderDto orderDto = orderService.getOrderByUserIdAndOrderId(userId, orderId);
        return orderHateoasUtil.createSelfRel(orderDto, userId);
    }
}
