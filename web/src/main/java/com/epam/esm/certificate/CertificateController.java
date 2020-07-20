package com.epam.esm.certificate;

import com.epam.esm.tag.TagHateoasUtil;
import com.epam.esm.tag.Tag;
import com.epam.esm.tag.TagDto;
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

/**
 * Class CertificateController for Rest Api Basics Task.
 *
 * @author ARTSIOM BERASTSEN
 * @version 1.0
 */
@Validated
@RestController
@RequestMapping(value = "/api/v1/certificates")
public class CertificateController {

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

    private final CertificateHateoasUtil certificateHateoasUtil;

    private final TagHateoasUtil tagHateoasUtil;

    @Autowired
    public CertificateController(CertificateService certificateService,
                                 TagService tagService,
                                 CertificateHateoasUtil certificateHateoasUtil,
                                 TagHateoasUtil tagHateoasUtil) {
        this.certificateService = certificateService;
        this.tagService = tagService;
        this.certificateHateoasUtil = certificateHateoasUtil;
        this.tagHateoasUtil = tagHateoasUtil;
    }

    /**
     * GET method findCertificates, that returns List of CertificateDto objects, which match <br>
     * to all request params.<br>
     * <p>
     * [GET /api/v1/certificates/]<br>
     * Request (application/json).<br>
     * Response 200 (application/json).
     * </p>
     *
     * @param t        represents tag's name, connected with certificate
     * @param textPart represents part of full certificate's description
     * @param orderBy  represents field name for ordering by
     * @return list of certificatesDto objects, which match to all request params
     * @see CertificateDto
     * @see Certificate
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public PagedModel<CertificateDto> findCertificates(@RequestParam(name = "tagNames", required = false)
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
        PagedModel<CertificateDto> pagedModel = certificateService.findCertificates(tagNames, textPart,
                orderBy, page, perPage);
        certificateHateoasUtil.createPaginationLinks(pagedModel, tagNames, textPart, orderBy);
        return pagedModel;
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
     * @see CertificateDto
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Secured("ROLE_ADMIN")
    public CertificateDto create(@Valid @RequestBody CertificateDto certificate) {
        CertificateDto certificateDto = certificateService.create(certificate);
        return certificateHateoasUtil.createSelfRelLink(certificateDto);
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
     * @see CertificateDto
     */
    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Secured("ROLE_ADMIN")
    public void update(@Valid @RequestBody CertificateDto certificate, @PathVariable("id") long id) {
        certificate.setId(id);
        certificateService.update(certificate);
    }

    @PatchMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Secured("ROLE_ADMIN")
    public void patch(@PathVariable("id") long id, @RequestBody CertificateDto certificateDto) {
        certificateService.patch(id, certificateDto);
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
     * @see CertificateDto
     */
    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CertificateDto findById(@PathVariable("id") long id) {
        return certificateHateoasUtil.createSelfRelLink(certificateService.find(id));
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
    @Secured({"ROLE_USER","ROLE_ADMIN"})
    public CollectionModel<TagDto> findAllCertificateTags(@PathVariable("id") long id) {
       CollectionModel<TagDto> tags =  CollectionModel.of(tagService.findTagsByCertificateId(id));
       tagHateoasUtil.createCertificateTagsLinks(tags,id);
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
     * @see Certificate
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
