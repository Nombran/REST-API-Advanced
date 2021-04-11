package com.epam.esm.service;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ServiceHateoasUtil {

    public void createPaginationLinks(PagedModel<ServiceDto> model, String[] tagNames, String textPart,
                                      String orderBy, ServiceStatus[] statuses) {
        PagedModel.PageMetadata metadata = model.getMetadata();
        int curPage = (int)metadata.getNumber();
        int size = (int)metadata.getSize();
        int totalPages = (int)metadata.getTotalPages();
        if(curPage < totalPages) {
            String nextPageHref = linkTo(methodOn(ServiceController.class)
                    .findCertificates(tagNames, textPart, orderBy,curPage + 1, size, statuses))
                    .toUriComponentsBuilder()
                    .toUriString();
            nextPageHref = nextPageHref.replaceAll("\\{.*?}", "");
            Link nextPage = Link.of(nextPageHref, "next");
            String lastPageHref = linkTo(methodOn(ServiceController.class)
                    .findCertificates(tagNames, textPart, orderBy, totalPages, size, statuses))
                    .toUriComponentsBuilder()
                    .toUriString();
            lastPageHref = lastPageHref.replaceAll("\\{.*?}", "");
            Link lastPage = Link.of(lastPageHref, "last");
            model.add(nextPage, lastPage);
        }
        if(curPage > 1) {
            String prevPageHref = linkTo(methodOn(ServiceController.class)
                    .findCertificates(tagNames, textPart, orderBy, curPage - 1 , size, statuses))
                    .toUriComponentsBuilder()
                    .toUriString();
            prevPageHref = prevPageHref.replaceAll("\\{.*?}", "");
            Link prevPage = Link.of(prevPageHref, "prev");
            model.add(prevPage);
        }
        String selfRelHref = linkTo(methodOn(ServiceController.class)
                .findCertificates(tagNames, textPart, orderBy, curPage, size, statuses))
                .toUriComponentsBuilder()
                .toUriString();
        selfRelHref = selfRelHref.replaceAll("\\{.*?}", "");
        Link selfRel = Link.of(selfRelHref);
        model.add(selfRel);
        model.getContent().forEach(this::createSelfRelLink);
    }

    public ServiceDto createSelfRelLink(ServiceDto certificate) {
            long id = certificate.getId();
            certificate.add(linkTo(methodOn(ServiceController.class)
                    .findById(id))
                    .withSelfRel());
            certificate.add(linkTo(methodOn(ServiceController.class)
                    .findAllCertificateTags(id))
                    .withRel("certificateTags"));
            return certificate;
    }
}
