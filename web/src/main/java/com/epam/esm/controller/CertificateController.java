package com.epam.esm.controller;

import com.epam.esm.certificate.dto.CertificateDto;
import com.epam.esm.certificate.service.CertificateService;
import com.epam.esm.tag.dto.TagDto;
import com.epam.esm.tag.service.TagService;
import com.epam.esm.tag.model.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

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

    @Autowired
    public CertificateController(CertificateService certificateService,
                                 TagService tagService) {
        this.certificateService = certificateService;
        this.tagService = tagService;
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
     * @param tagName         represents tag's name, connected with certificate
     * @param textPart represents part of full certificate's description
     * @param orderBy         represents field name for ordering by
     * @return list of certificatesDto objects, which match to all request params
     * @see CertificateDto
     * @see com.epam.esm.certificate.model.Certificate
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CertificateDto> findCertificates(@RequestParam(name = "tagName", required = false)
                                                         String tagName,
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
        return certificateService.findCertificates(tagName, textPart, orderBy, page, perPage);
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
    public void create(@Valid @RequestBody CertificateDto certificate) {
        certificateService.create(certificate);
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
    public void update(@Valid @RequestBody CertificateDto certificate, @PathVariable("id") long id) {
        certificate.setId(id);
        certificateService.update(certificate);
    }

    /**
     * DELETE method, which used to delete existent certificate, and all<br>
     * relations to tags, connected with it.<br>
     * <p>
     * [DELETE /api/v1/certificates/id/]<br>
     * Request (application/json).<br>
     * Response 204 (application/json).
     * </p>
     *
     * @param id represents id of the certificate.
     * @see CertificateDto
     */
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") long id) {
        certificateService.delete(id);
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
        return certificateService.find(id);
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
    public List<TagDto> findAllCertificateTags(@PathVariable("id") long id) {
        return tagService.findTagsByCertificateId(id);
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
     * @see com.epam.esm.certificate.model.Certificate
     * @see Tag
     */
    @DeleteMapping(value = "/{id}/tags/{tagId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCertificateTag(@PathVariable("id") long certificateId,
                                     @PathVariable("tagId") long tagId) {
        certificateService.deleteCertificateTag(certificateId, tagId);
    }
}
