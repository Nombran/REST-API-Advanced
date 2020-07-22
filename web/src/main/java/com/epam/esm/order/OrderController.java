package com.epam.esm.order;

import com.epam.esm.jwt.JwtUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequestMapping(value = "/api/v1/users")
public class OrderController {
    private final OrderService orderService;
    private final OrderHateoasUtil orderHateoasUtil;

    @Autowired
    public OrderController(OrderService orderService,
                           OrderHateoasUtil orderHateoasUtil) {
        this.orderService = orderService;
        this.orderHateoasUtil = orderHateoasUtil;
    }

    @Secured("ROLE_USER")
    @PostMapping(value = "me/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDto makeOrder(@Valid @RequestBody OrderDto orderDto, Authentication authentication) {
        JwtUser user = (JwtUser)authentication.getPrincipal();
        return orderService.create(orderDto, user.getId());
    }

    @GetMapping(value = "me/orders")
    @ResponseStatus(HttpStatus.OK)
    @Secured("ROLE_USER")
    public PagedModel<OrderDto> getUserOrders(@RequestParam(name = "page", required = false, defaultValue = "1")
                                              @Min(value = 1, message = "page number must be greater or equal to 1")
                                                      Integer page,
                                              @RequestParam(name = "perPage", required = false, defaultValue = "50")
                                              @Min(value = 1, message = "perPage param must be greater or equal to 1")
                                                      Integer perPage,
                                              Authentication authentication) {
        JwtUser user = (JwtUser)authentication.getPrincipal();
        PagedModel<OrderDto> model = orderService.findByUserId(user.getId(), page, perPage);
        orderHateoasUtil.createPaginationLinks(model, authentication);
        return model;
    }

    @GetMapping(value = "me/orders/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    @Secured("ROLE_USER")
    public OrderDto getUserOrderById(@PathVariable("orderId")long orderId, Authentication authentication) {
        JwtUser user = (JwtUser)authentication.getPrincipal();
        OrderDto orderDto = orderService.getOrderByUserIdAndOrderId(user.getId(), orderId);
        return orderHateoasUtil.createSelfRel(orderDto, authentication);
    }
}
