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

/**
 * Class OrderController for Rest Api Advanced Task.
 *
 * @author ARTSIOM BERASTSEN
 * @version 1.0
 */
@RestController
@RequestMapping(value = "/api/v1/users")
public class OrderController {
    /**
     * Field orderService
     *
     * @see OrderService
     */
    private final OrderService orderService;

    /**
     * Field orderHateoasUtil
     *
     * @see OrderHateoasUtil
     */
    private final OrderHateoasUtil orderHateoasUtil;

    @Autowired
    public OrderController(OrderService orderService,
                           OrderHateoasUtil orderHateoasUtil) {
        this.orderService = orderService;
        this.orderHateoasUtil = orderHateoasUtil;
    }

    /**
     * POST method ,which creates user's order<br>
     * <p>
     * [POST api/v1/users/me/orders]<br>
     * Request (application/json).<br>
     * Response 201 (application/json).
     * </p>
     *
     * @param orderDto represents order object, which contain order<br>
     * information and list of ordered certificates
     * @param authentication represents current user authentication object
     * @see OrderDto
     * @see Authentication
     * @see JwtUser
     */
    @Secured("ROLE_USER")
    @PostMapping(value = "me/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDto makeOrder(@Valid @RequestBody OrderDto orderDto, Authentication authentication) {
        JwtUser user = (JwtUser)authentication.getPrincipal();
        return orderService.create(orderDto, user.getId());
    }

    /**
     * GET method ,which returns pageModel object with list of user's orders<br>
     * and current pageMetadata Object
     * <p>
     * [GET api/v1/users/me/orders]<br>
     * Request (application/json).<br>
     * Response 201 (application/json).
     * </p>
     *
     * @param page represents page number<br>
     * @param perPage represents number of orders per page
     * @see OrderDto
     * @see Authentication
     * @see PagedModel
     */
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

    /**
     * GET method ,which returns single user's order by it's id<br>
     * <p>
     * [GET api/v1/users/me/orders/{id}]<br>
     * Request (application/json).<br>
     * Response 201 (application/json).
     * </p>
     *
     * @param orderId represents order's id<br>
     * @param authentication represents current user authentication object
     * @return orderDto object with order info
     * @see OrderDto
     * @see Authentication
     * @see PagedModel
     */
    @GetMapping(value = "me/orders/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    @Secured("ROLE_USER")
    public OrderDto getUserOrderById(@PathVariable("orderId")long orderId, Authentication authentication) {
        JwtUser user = (JwtUser)authentication.getPrincipal();
        OrderDto orderDto = orderService.getOrderByUserIdAndOrderId(user.getId(), orderId);
        return orderHateoasUtil.createSelfRel(orderDto, authentication);
    }
}
