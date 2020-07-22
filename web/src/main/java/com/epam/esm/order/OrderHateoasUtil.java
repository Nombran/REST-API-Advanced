package com.epam.esm.order;

import org.springframework.hateoas.PagedModel;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class OrderHateoasUtil {

    public void createPaginationLinks(PagedModel<OrderDto> model, Authentication authentication) {
        PagedModel.PageMetadata metadata = model.getMetadata();
        int curPage = (int)metadata.getNumber();
        int size = (int)metadata.getSize();
        int totalPages = (int)metadata.getTotalPages();
        if(curPage < totalPages) {
            model.add(linkTo(methodOn(OrderController.class)
                    .getUserOrders(curPage + 1, size, authentication))
                    .withRel("next"));
            model.add(linkTo(methodOn(OrderController.class)
                    .getUserOrders(totalPages, size, authentication))
                    .withRel("last"));
        }
        if(curPage > 1) {
            model.add(linkTo(methodOn(OrderController.class)
                    .getUserOrders(curPage-1,size, authentication))
                    .withRel("prev"));
        }
        model.add(linkTo(methodOn(OrderController.class)
                .getUserOrders(curPage,size, authentication))
                .withSelfRel());
        model.forEach(order-> createSelfRel(order, authentication));
    }

    public OrderDto createSelfRel(OrderDto orderDto,Authentication authentication) {
        orderDto.add(linkTo(methodOn(OrderController.class)
                .getUserOrderById(orderDto.getId(), authentication))
                .withSelfRel());
        return orderDto;
    }
}
