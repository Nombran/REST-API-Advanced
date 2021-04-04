package com.epam.esm.user;

import com.epam.esm.jwt.JwtUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;

/**
 * Class UserController for Rest Api Advanced Task.
 *
 * @author ARTSIOM BERASTSEN
 * @version 1.0
 */
@Validated
@RestController
@RequestMapping(value = "/api/v1/users")
public class UserController {
    /**
     * Field userService
     *
     * @see UserService
     */
    private final UserService userService;

    /**
     * Field userHateoasUtil
     *
     * @see UserHateoasUtil
     */
    private final UserHateoasUtil userHateoasUtil;

    @Autowired
    public UserController(UserService userService,
                          UserHateoasUtil userHateoasUtil) {
        this.userService = userService;
        this.userHateoasUtil = userHateoasUtil;
    }

    /**
     * GET method findUsers, that returns PageModel object with list of<br>
     * users, which match to all request params.<br>
     * <p>
     * [GET /api/v1/users/]<br>
     * Request (application/json).<br>
     * Response 200 (application/json).
     * </p>
     *
     * @param page represents page number
     * @param perPage represents number of certificate's items per page
     * @return PageModel object with list of certificatesDto objects, which match to all request params<br>
     * and PageMetadata info
     * @see UserDto
     * @see PagedModel
     * @see UserHateoasUtil
     */
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

    /**
     * POST method ,which creates user entity<br>
     * <p>
     * [POST api/v1/users/]<br>
     * Request (application/json).<br>
     * Response 201 (application/json).
     * </p>
     *
     * @param userDto represents dto object, which contain user<br>
     *                    information.
     * @see UserDto
     * @see UserHateoasUtil
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        UserDto created = userService.create(userDto);
        return userHateoasUtil.createSingleUserLinks(created);
    }

    /**
     * PUT method, used to update existent user object<br>
     * <p>
     * [PUT /api/v1/users/id/]<br>
     * Request (application/json).<br>
     * Response 200 (application/json).
     * </p>
     *
     * @param userDto represents dto object, which contain user information.<br>
     * @param id  represents id of the user.
     * @see UserDto
     */
    @PreAuthorize("authentication.principal.id == #id")
    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto update(@Valid @RequestBody UserDto userDto, @PathVariable("id") long id) {
        UserDto updated = userService.update(userDto,id);
        return userHateoasUtil.createSingleUserLinks(updated);
    }

    @GetMapping(value = "/me")
    @ResponseStatus(HttpStatus.OK)
    @Secured({"ROLE_USER","ROLE_ADMIN"})
    public UserDto getUserByToken(Authentication authentication) {
        JwtUser user = (JwtUser)authentication.getPrincipal();
        UserDto userDto = userService.find(user.getId());
        return userHateoasUtil.createSingleUserLinks(userDto);
    }

    /**
     * GET method, which used to get user dto object by it's id.<br>
     * <p>
     * [GET /api/v1/users/id/]<br>
     * Request (application/json).<br>
     * Response 200 (application/json).
     * </p>
     *
     * @param id represents id of the user.
     * @return userDto object<br>
     * @see UserDto
     */
    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_ADMIN') or authentication.principal.id == #id")
    public UserDto findById(@PathVariable("id")long id) {
        UserDto userDto = userService.find(id);
        return userHateoasUtil.createSingleUserLinks(userDto);
    }
}
