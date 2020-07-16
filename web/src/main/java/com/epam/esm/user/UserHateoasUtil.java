package com.epam.esm.user;

import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserHateoasUtil {

    public void createPaginationLinks(PagedModel<UserDto> model) {
        PagedModel.PageMetadata metadata = model.getMetadata();
        int curPage = (int)metadata.getNumber();
        int size = (int)metadata.getSize();
        int totalPages = (int)metadata.getTotalPages();
        if(curPage < totalPages) {
            model.add(linkTo(methodOn(UserController.class)
                    .findUsers(curPage + 1, size))
                    .withRel("next"));
            model.add(linkTo(methodOn(UserController.class)
                    .findUsers(totalPages, size))
                    .withRel("last"));
        }
        if(curPage > 1) {
            model.add(linkTo(methodOn(UserController.class)
                    .findUsers(curPage-1,size))
                    .withRel("prev"));
        }
        model.add(linkTo(methodOn(UserController.class)
                .findUsers(curPage,size))
                .withSelfRel());
        model.forEach(this::createSelfRel);
    }

    public void createSelfRel(UserDto userDto) {
        userDto.add(linkTo(methodOn(UserController.class)
                .findById(userDto.getId()))
                .withSelfRel());
        userDto.add(linkTo(methodOn(UserController.class)
                .getUserOrders(1,50,userDto.getId()))
                .withRel("userOrders"));
    }

    public UserDto createSingleUserLinks(UserDto userDto) {
        userDto.add(linkTo(methodOn(UserController.class)
                .findById(userDto.getId()))
                .withSelfRel());
        userDto.add(linkTo(methodOn(UserController.class)
                .findUsers(null, null))
                .withRel("allUsers")
                .expand());
        userDto.add(linkTo(methodOn(UserController.class)
                .getUserOrders(1,50, userDto.getId()))
                .withRel("usersOrders"));
        return userDto;
    }
}
