package com.epam.esm.tag;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collections;

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
            Tag destination = context.getDestination();
            mapSpecificFields(destination);
            return context.getDestination();
        };
    }

    public void mapSpecificFields(Tag destination) {
        destination.setCertificates(Collections.emptyList());
    }
}
