package com.epam.esm.tag;

import com.epam.esm.tag.TagDto;
import com.epam.esm.tag.Tag;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collections;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Component
public class TagDtoMapper {
    private final ModelMapper mapper;

    @Autowired
    public TagDtoMapper(ModelMapper modelMapper) {
        this.mapper = modelMapper;
    }

    @PostConstruct
    public void setupMapper() {
        mapper.createTypeMap(TagDto.class, Tag.class)
                .addMappings(m-> m.skip(Tag::setCertificates)).setPostConverter(toEntityConverter());
    }

    public Converter<TagDto, Tag> toEntityConverter() {
        return context -> {
            TagDto source = context.getSource();
            Tag destination = context.getDestination();
            mapSpecificFields(source, destination);
            return context.getDestination();
        };
    }

    public void mapSpecificFields(TagDto source, Tag destination) {
        destination.setCertificates(Collections.emptyList());
    }
}
