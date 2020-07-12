package com.epam.esm.controller;

import com.epam.esm.hateoasutils.TagHATEOASUtil;
import com.epam.esm.tag.dto.TagDto;
import com.epam.esm.tag.model.Tag;
import com.epam.esm.tag.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;

/**
 * Class TagController for Rest Api Basics Task.
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
     * @see TagService
     */
    private final TagService tagService;

    private final TagHATEOASUtil tagHateoasUtil;

    @Autowired
    public TagController(TagService tagService,
                         TagHATEOASUtil tagHateoasUtil) {
        this.tagHateoasUtil = tagHateoasUtil;
        this.tagService = tagService;
    }

    /**
     * GET method, which used to get all tag objects.<br>
     * <p>
     * [GET /api/v1/tags/]<br>
     * Request (application/json).<br>
     * Response 200 (application/json).
     * </p>
     *
     * @return list of tag objects.
     * @see Tag
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
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
     * see Tag
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
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
    public void delete(@PathVariable("id") long id) {
        tagService.delete(id);
    }

    @GetMapping(value = "/most-widely-tag")
    public TagDto GetMostWidelyUsedTagOfAUserWithTheHighestCostOfAllOrders() {
        return tagHateoasUtil.createSingleTagLinks(
                tagService.GetMostWidelyUsedTagOfAUserWithTheHighestCostOfAllOrders());
    }
}
