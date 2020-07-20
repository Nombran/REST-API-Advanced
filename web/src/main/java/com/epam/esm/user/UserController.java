package com.epam.esm.user;

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
    private final UserHateoasUtil userHateoasUtil;

    @Autowired
    public UserController(UserService userService,
                          UserHateoasUtil userHateoasUtil) {
        this.userService = userService;
        this.userHateoasUtil = userHateoasUtil;
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
}
