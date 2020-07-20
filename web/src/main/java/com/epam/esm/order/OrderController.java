package com.epam.esm.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequestMapping(value = "/api/v1/users/{id}")
public class OrderController {
    private final OrderService orderService;
    private final OrderHateoasUtil orderHateoasUtil;

    @Autowired
    public OrderController(OrderService orderService,
                           OrderHateoasUtil orderHateoasUtil) {
        this.orderService = orderService;
        this.orderHateoasUtil = orderHateoasUtil;
    }

    @PreAuthorize("authentication.principal.id == #userId")
    @PostMapping(value = "/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDto makeOrder(@Valid @RequestBody OrderDto orderDto, @PathVariable("id") long userId) {
        return orderService.create(orderDto, userId);
    }

    @GetMapping(value = "/orders")
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

    @GetMapping(value = "/orders/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_ADMIN') or authentication.principal.id == #userId")
    public OrderDto getUserOrderById(@PathVariable("id") long userId, @PathVariable("orderId")long orderId) {
        OrderDto orderDto = orderService.getOrderByUserIdAndOrderId(userId, orderId);
        return orderHateoasUtil.createSelfRel(orderDto, userId);
    }
}
