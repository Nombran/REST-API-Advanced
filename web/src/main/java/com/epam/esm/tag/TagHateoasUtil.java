package com.epam.esm.tag;

import com.epam.esm.certificate.CertificateController;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class TagHateoasUtil {

    public void createPaginationLinks(PagedModel<TagDto> model, String textPart) {
        PagedModel.PageMetadata metadata = model.getMetadata();
        int curPage = (int)metadata.getNumber();
        int size = (int)metadata.getSize();
        int totalPages = (int)metadata.getTotalPages();
        if(curPage < totalPages) {

            model.add(linkTo(methodOn(TagController.class)
                    .findTags(curPage + 1, size, textPart))
                    .withRel("next"));
            model.add(linkTo(methodOn(TagController.class)
                    .findTags(totalPages, size, textPart))
                    .withRel("last"));
            String nextPageHref = linkTo(methodOn(TagController.class)
                    .findTags(curPage + 1, size, textPart))
                    .toUriComponentsBuilder()
                    .toUriString();
            nextPageHref = nextPageHref.replaceAll("\\{.*?}", "");
            Link nextPage = Link.of(nextPageHref, "next");
            String lastPageHref = linkTo(methodOn(TagController.class)
                    .findTags(totalPages, size, textPart))
                    .toUriComponentsBuilder()
                    .toUriString();
            lastPageHref = lastPageHref.replaceAll("\\{.*?}", "");
            Link lastPage = Link.of(lastPageHref, "last");
            model.add(nextPage, lastPage);
        }
        if(curPage > 1) {
            String prevPageHref = linkTo(methodOn(TagController.class)
                    .findTags(curPage - 1 , size, textPart))
                    .toUriComponentsBuilder()
                    .toUriString();
            prevPageHref = prevPageHref.replaceAll("\\{.*?}", "");
            Link prevPage = Link.of(prevPageHref, "prev");
            model.add(prevPage);
        }
        String selfRelHref = linkTo(methodOn(TagController.class)
                .findTags(curPage, size, textPart))
                .toUriComponentsBuilder()
                .toUriString();
        selfRelHref = selfRelHref.replaceAll("\\{.*?}", "");
        Link selfRel = Link.of(selfRelHref);
        model.add(selfRel);
    }

    public void createSelfRel(TagDto tagDto) {
        tagDto.add(linkTo(methodOn(TagController.class)
                .findById(tagDto.getId()))
                .withSelfRel());
    }

    public TagDto createSingleTagLinks(TagDto tagDto) {
        tagDto.add(linkTo(methodOn(TagController.class)
                .findById(tagDto.getId()))
                .withSelfRel());
        tagDto.add(linkTo(methodOn(TagController.class)
                .findTags(null,null, null))
                .withRel("allTags")
                .expand());
        return tagDto;
    }

    public void createCertificateTagsLinks(CollectionModel<TagDto> tags, long certificateId) {
        tags.getContent().forEach(this::createSelfRel);
        tags.add(linkTo(methodOn(CertificateController.class)
                .findAllCertificateTags(certificateId))
                .withSelfRel());
    }
}
