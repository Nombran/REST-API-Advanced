package com.epam.esm.hateoasutils;

import com.epam.esm.controller.CertificateController;
import com.epam.esm.controller.TagController;
import com.epam.esm.tag.dto.TagDto;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class TagHateoasUtil {

    public void createPaginationLinks(PagedModel<TagDto> model) {
        PagedModel.PageMetadata metadata = model.getMetadata();
        int curPage = (int)metadata.getNumber();
        int size = (int)metadata.getSize();
        int totalPages = (int)metadata.getTotalPages();
        if(curPage < totalPages) {
            model.add(linkTo(methodOn(TagController.class)
                    .findTags(curPage + 1, size))
                    .withRel("next"));
            model.add(linkTo(methodOn(TagController.class)
                    .findTags(totalPages, size))
                    .withRel("last"));
        }
        if(curPage > 1) {
            model.add(linkTo(methodOn(TagController.class)
                    .findTags(curPage-1,size))
                    .withRel("prev"));
        }
        model.add(linkTo(methodOn(TagController.class)
                .findTags(curPage,size))
                .withSelfRel());
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
                .findTags(null,null))
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
