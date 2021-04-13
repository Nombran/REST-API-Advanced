package com.epam.esm.service;

import com.epam.esm.tag.Tag;
import com.epam.esm.tag.TagDto;
import com.epam.esm.tag.TagHateoasUtil;
import com.epam.esm.tag.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

/**
 * Class CertificateController for Rest Api Advanced Task.
 *
 * @author ARTSIOM BERASTSEN
 * @version 1.0
 */
@Validated
@RestController
@RequestMapping(value = "/api/v1/certificates")
public class ServiceController {

    /**
     * Field certificateService
     *
     * @see CertificateService
     */
    private final CertificateService certificateService;

    /**
     * Field tagService
     *
     * @see TagService
     */
    private final TagService tagService;

    /**
     * Field certificateHateoasUtil
     *
     * @see ServiceHateoasUtil
     */
    private final ServiceHateoasUtil serviceHateoasUtil;

    /**
     * Field TagHateoasUtil
     *
     * @see TagHateoasUtil
     */
    private final TagHateoasUtil tagHateoasUtil;

    @Autowired
    public ServiceController(CertificateService certificateService,
                             TagService tagService,
                             ServiceHateoasUtil serviceHateoasUtil,
                             TagHateoasUtil tagHateoasUtil) {
        this.certificateService = certificateService;
        this.tagService = tagService;
        this.serviceHateoasUtil = serviceHateoasUtil;
        this.tagHateoasUtil = tagHateoasUtil;
    }

    /**
     * GET method findCertificates, that returns PageModel object with list of<br>
     * certificates, which match to all request params.<br>
     * <p>
     * [GET /api/v1/certificates/]<br>
     * Request (application/json).<br>
     * Response 200 (application/json).
     * </p>
     *
     * @param tagNames represents names of tags, connected with certificate
     * @param textPart represents part of full certificate's description
     * @param orderBy  represents field name for ordering by
     * @param page     represents page number
     * @param perPage  represents number of certificate's items per page
     * @return PageModel object with list of certificatesDto objects, which match to all request params<br>
     * and PageMetadata info
     * @see ServiceDto
     * @see Service
     * @see ServiceParamWrapper
     * @see ServiceHateoasUtil
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public PagedModel<ServiceDto> findCertificates(@RequestParam(name = "tagNames", required = false)
                                                               String[] tagNames,
                                                   @RequestParam(name = "textPart", required = false)
                                                               String textPart,
                                                   @RequestParam(name = "orderBy", required = false, defaultValue = "id")
                                                               String orderBy,
                                                   @RequestParam(name = "page", required = false, defaultValue = "1")
                                                       @Min(value = 1, message = "page number must be greater or equal to 1")
                                                               Integer page,
                                                   @RequestParam(name = "perPage", required = false, defaultValue = "50")
                                                       @Min(value = 1, message = "perPage param must be greater or equal to 1")
                                                               Integer perPage
    ) {
            ServiceParamWrapper wrapper = new ServiceParamWrapper(tagNames, textPart, orderBy, page, perPage);
            PagedModel<ServiceDto> pagedModel = certificateService.findCertificates(wrapper);
            serviceHateoasUtil.createPaginationLinks(pagedModel, tagNames, textPart, orderBy);
            return pagedModel;
    }

    @GetMapping(value = "/free")
    @ResponseStatus(HttpStatus.OK)
    public List<ServiceDto> getFreeServices() {
        return certificateService.findFreeServices();
    }

    /**
     * POST method ,which creates certificate entity, and all tags,<br>
     * connected with it.<br>
     * <p>
     * [POST api/v1/certificates/]<br>
     * Request (application/json).<br>
     * Response 201 (application/json).
     * </p>
     *
     * @param certificate represents dto object, which contain certificate<br>
     *                    information and list of it's tags.
     * @see ServiceDto
     * @see ServiceHateoasUtil
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Secured("ROLE_ADMIN")
    public ServiceDto create(@Valid @RequestBody ServiceDto certificate) {
        ServiceDto serviceDto = certificateService.create(certificate);
        return serviceHateoasUtil.createSelfRelLink(serviceDto);
    }

    /**
     * PUT method, used to update existent certificate object<br>
     * and all it's tags.<br>
     * <p>
     * [PUT /api/v1/certificates/id/]<br>
     * Request (application/json).<br>
     * Response 200 (application/json).
     * </p>
     *
     * @param certificate represents dto object, which contain certificate<br>
     *                    information and list of it's tags.
     * @param id          represents id of the certificate.
     * @see ServiceDto
     */
    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Secured("ROLE_ADMIN")
    public void update(@Valid @RequestBody ServiceDto certificate, @PathVariable("id") long id) {
        certificate.setId(id);
        certificateService.update(certificate);
    }

    /**
     * PATCH method, used to update existent certificate object<br>
     * by fields <br>
     * <p>
     * [PATCH /api/v1/certificates/id/]<br>
     * Request (application/json).<br>
     * Response 200 (application/json).
     * </p>
     *
     * @param serviceDto represents certificate fields<br>
     * @param id             represents id of the certificate.
     * @see ServiceDto
     */
    @PatchMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Secured("ROLE_ADMIN")
    public void patch(@PathVariable("id") long id, @RequestBody ServiceDto serviceDto) {
        certificateService.patch(id, serviceDto);
    }

    /**
     * GET method, which used to get certificate dto object by it's id.<br>
     * <p>
     * [GET /api/v1/certificates/id/]<br>
     * Request (application/json).<br>
     * Response 200 (application/json).
     * </p>
     *
     * @param id represents id of the certificate.
     * @return certificateDto object, which contain all certificate<br>
     * information and list of it's tags.
     * @see ServiceDto
     */
    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ServiceDto findById(@PathVariable("id") long id) {
        return serviceHateoasUtil.createSelfRelLink(certificateService.find(id));
    }

    /**
     * GET method, which used to get list of certificate's tags by certificate id<br>
     * <p>
     * [GET /api/v1/certificates/id/tags/]<br>
     * Request (application/json).<br>
     * Response 200 (application/json).
     * </p>
     *
     * @param id represents id of the certificate.
     * @return list of tag objects
     * @see Tag
     */
    @GetMapping(value = "/{id}/tags")
    @ResponseStatus(HttpStatus.OK)
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public CollectionModel<TagDto> findAllCertificateTags(@PathVariable("id") long id) {
        CollectionModel<TagDto> tags = CollectionModel.of(tagService.findTagsByCertificateId(id));
        tagHateoasUtil.createCertificateTagsLinks(tags, id);
        return tags;
    }

    /**
     * POST method, which used to add tag to certificate by certificate id.<br>
     * <p>
     * [POST /api/v1/certificates/id/tags/]<br>
     * Request (application/json).<br>
     * Response 201 (application/json).
     * </p>
     *
     * @param certificateId represents id of the certificate.
     * @param tag           represents tag object.
     * @see Tag
     */
    @PostMapping(value = "/{id}/tags")
    @ResponseStatus(HttpStatus.CREATED)
    @Secured("ROLE_ADMIN")
    public void addTag(@PathVariable("id") long certificateId, @RequestBody TagDto tag) {
        certificateService.addCertificateTag(tag, certificateId);
    }

    /**
     * DELETE method, which used to delete relation between tag and certificate.<br>
     * <p>
     * [DELETE /api/v1/certificates/id/tags/tagId]<br>
     * Request (application/json).<br>
     * Response 204 (application/json).
     * </p>
     *
     * @param certificateId represents id of the certificate.
     * @param tagId         represents id of the tag.
     * @see Service
     * @see Tag
     */
    @DeleteMapping(value = "/{id}/tags/{tagId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured("ROLE_ADMIN")
    public void deleteCertificateTag(@PathVariable("id") long certificateId,
                                     @PathVariable("tagId") long tagId) {
        certificateService.deleteCertificateTag(certificateId, tagId);
    }
}
