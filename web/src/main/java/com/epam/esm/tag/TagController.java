package com.epam.esm.tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;

/**
 * Class TagController for Rest Advanced Task.
 *
 * @author ARTSIOM BERASTSEN
 * @version 1.0
 */
@Validated
@RestController
@RequestMapping(value = "/api/v1/tags")
public class TagController {
    /**
     * Field TagService
     *
     * @see TagService
     */
    private final TagService tagService;

    /**
     * Field TagHateoasUtil
     *
     * @see TagHateoasUtil
     */
    private final TagHateoasUtil tagHateoasUtil;

    @Autowired
    public TagController(TagService tagService,
                         TagHateoasUtil tagHateoasUtil) {
        this.tagHateoasUtil = tagHateoasUtil;
        this.tagService = tagService;
    }

    /**
     * GET method, which returns pageModel object with list of tags<br>
     * and pageMetadata object.
     * <p>
     * [GET /api/v1/tags/]<br>
     * Request (application/json).<br>
     * Response 200 (application/json).
     * </p>
     *
     * @param page represents number of page
     * @param perPage represents number of tags per page
     * @return pageModel object with list of tags.
     * @see Tag
     * @see PagedModel
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public PagedModel<TagDto> findTags(@RequestParam(name = "page", required = false, defaultValue = "1")
                                     @Min(value = 1, message = "page number must be greater or equal to 1")
                                             Integer page,
                                 @RequestParam(name = "perPage", required = false, defaultValue = "50")
                                     @Min(value = 1, message = "perPage param must be greater or equal to 1")
                                             Integer perPage) {
        PagedModel<TagDto> model = tagService.findTags(page, perPage);
        model.getContent().forEach(tagHateoasUtil::createSelfRel);
        tagHateoasUtil.createPaginationLinks(model);
        return model;
    }

    /**
     * POST method, which used to create new tag instance.<br>
     * <p>
     * [POST /api/v1/tags/]<br>
     * Request (application/json).<br>
     * Response 201 (application/json).
     * </p>
     *
     * @param tag represents tag object
     * @return created Tag
     * @see Tag
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Secured("ROLE_ADMIN")
    public TagDto create(@Valid @RequestBody TagDto tag) {
        return tagHateoasUtil.createSingleTagLinks(tagService.create(tag));
    }

    /**
     * GET method, which used to get tag object by it's id.<br>
     * <p>
     * [GET /api/v1/tags/id/]<br>
     * Request (application/json).<br>
     * Response 200 (application/json).
     * </p>
     * @param id represents id of the tag.
     * @return tag object.
     * @see Tag
     */
    @GetMapping(value = "/{id}")
    @Secured({"ROLE_USER","ROLE_ADMIN"})
    public TagDto findById(@PathVariable("id") long id) {
        return tagHateoasUtil.createSingleTagLinks(tagService.find(id));
    }

    /**
     * DELETE method, which used to delete existent tag by id.<br>
     * <p>
     * [DELETE /api/v1/tags/id/]<br>
     * Request (application/json).<br>
     * Response 204 (application/json).
     * </p>
     *
     * @param id represents id of the tag.
     * @see Tag
     */
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured("ROLE_ADMIN")
    public void delete(@PathVariable("id") long id) {
        tagService.delete(id);
    }

    /**
     * GET method, which used to get the most widely used secondary entity<br>
     * of a user with the highest cost of all orders.<br>
     * <p>
     * [GET /api/v1/tags/most-widely-tag]<br>
     * Request (application/json).<br>
     * Response 200 (application/json).
     * </p>
     * @return tag object.
     * @see Tag
     */
    @GetMapping(value = "/most-widely-tag")
    @Secured({"ROLE_USER","ROLE_ADMIN"})
    public TagDto GetValuedUsersMostPopularTag() {
        return tagHateoasUtil.createSingleTagLinks(
                tagService.GetValuedUsersMostPopularTag());
    }
}
