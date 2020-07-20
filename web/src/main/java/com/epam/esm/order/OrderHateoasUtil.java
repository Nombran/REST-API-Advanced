package com.epam.esm.order;

import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class OrderHateoasUtil {

    public void createPaginationLinks(PagedModel<OrderDto> model, long userId) {
        PagedModel.PageMetadata metadata = model.getMetadata();
        int curPage = (int)metadata.getNumber();
        int size = (int)metadata.getSize();
        int totalPages = (int)metadata.getTotalPages();
        if(curPage < totalPages) {
            model.add(linkTo(methodOn(OrderController.class)
                    .getUserOrders(curPage + 1, size, userId))
                    .withRel("next"));
            model.add(linkTo(methodOn(OrderController.class)
                    .getUserOrders(totalPages, size, userId))
                    .withRel("last"));
        }
        if(curPage > 1) {
            model.add(linkTo(methodOn(OrderController.class)
                    .getUserOrders(curPage-1,size, userId))
                    .withRel("prev"));
        }
        model.add(linkTo(methodOn(OrderController.class)
                .getUserOrders(curPage,size, userId))
                .withSelfRel());
        model.forEach(order-> createSelfRel(order, userId));
    }

    public OrderDto createSelfRel(OrderDto orderDto,long userId) {
        orderDto.add(linkTo(methodOn(OrderController.class)
                .getUserOrderById(userId, orderDto.getId()))
                .withSelfRel());
        return orderDto;
    }
}
