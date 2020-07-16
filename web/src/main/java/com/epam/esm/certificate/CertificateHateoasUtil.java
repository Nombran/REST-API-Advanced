package com.epam.esm.certificate;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CertificateHateoasUtil {

    public void createPaginationLinks(PagedModel<CertificateDto> model, String[] tagNames, String textPart,
                                      String orderBy) {
        PagedModel.PageMetadata metadata = model.getMetadata();
        int curPage = (int)metadata.getNumber();
        int size = (int)metadata.getSize();
        int totalPages = (int)metadata.getTotalPages();
        if(curPage < totalPages) {
            String nextPageHref = linkTo(methodOn(CertificateController.class)
                    .findCertificates(tagNames, textPart, orderBy,curPage + 1, size))
                    .toUriComponentsBuilder()
                    .toUriString();
            nextPageHref = nextPageHref.replaceAll("\\{.*?}", "");
            Link nextPage = Link.of(nextPageHref, "next");
            String lastPageHref = linkTo(methodOn(CertificateController.class)
                    .findCertificates(tagNames, textPart, orderBy, totalPages, size))
                    .toUriComponentsBuilder()
                    .toUriString();
            lastPageHref = lastPageHref.replaceAll("\\{.*?}", "");
            Link lastPage = Link.of(lastPageHref, "last");
            model.add(nextPage, lastPage);
        }
        if(curPage > 1) {
            String prevPageHref = linkTo(methodOn(CertificateController.class)
                    .findCertificates(tagNames, textPart, orderBy, curPage - 1 , size))
                    .toUriComponentsBuilder()
                    .toUriString();
            prevPageHref = prevPageHref.replaceAll("\\{.*?}", "");
            Link prevPage = Link.of(prevPageHref, "prev");
            model.add(prevPage);
        }
        String selfRelHref = linkTo(methodOn(CertificateController.class)
                .findCertificates(tagNames, textPart, orderBy, curPage, size))
                .toUriComponentsBuilder()
                .toUriString();
        selfRelHref = selfRelHref.replaceAll("\\{.*?}", "");
        Link selfRel = Link.of(selfRelHref);
        model.add(selfRel);
        model.getContent().forEach(this::createSelfRelLink);
    }

    public CertificateDto createSelfRelLink(CertificateDto certificate) {
            long id = certificate.getId();
            certificate.add(linkTo(methodOn(CertificateController.class)
                    .findById(id))
                    .withSelfRel());
            certificate.add(linkTo(methodOn(CertificateController.class)
                    .findAllCertificateTags(id))
                    .withRel("certificateTags"));
            return certificate;
    }
}
