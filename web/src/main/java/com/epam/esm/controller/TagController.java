package com.epam.esm.controller;

import com.epam.esm.tag.model.Tag;
import com.epam.esm.tag.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.Valid;
import java.util.List;

/**
 * Class TagController for Rest Api Basics Task.
 *
 * @author ARTSIOM BERASTSEN
 * @version 1.0
 */
@RestController
@RequestMapping(value = "/api/v1/tags")
public class TagController {
    /**
     * Field TagService
     * @see TagService
     */
    private final TagService tagService;

    @Autowired
    public TagController(TagService tagService) {
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
    public List<Tag> findAll() {
        return tagService.findAll();
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
     * @see Tag
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@Valid @RequestBody Tag tag) {
        tagService.create(tag);
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
    public Tag findById(@PathVariable("id") long id) {
        return tagService.find(id);
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
}
