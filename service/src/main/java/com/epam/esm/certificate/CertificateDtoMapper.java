package com.epam.esm.certificate;

import com.epam.esm.tag.Tag;
import com.epam.esm.tag.TagDao;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CertificateDtoMapper{
    private final ModelMapper mapper;
    private final TagDao tagDao;

    @Autowired
    public CertificateDtoMapper(ModelMapper modelMapper,
                                TagDao tagDao) {
        this.mapper = modelMapper;
        this.tagDao = tagDao;
    }

    @PostConstruct
    public void setupMapper() {
        mapper.createTypeMap(Certificate.class, CertificateDto.class)
                .addMappings(m -> m.skip(CertificateDto::setTags)).setPostConverter(toDtoConverter());
        mapper.createTypeMap(CertificateDto.class, Certificate.class)
                .addMappings(m-> m.skip(Certificate::setTags)).setPostConverter(toEntityConverter());
    }

    public Converter<Certificate, CertificateDto> toDtoConverter() {
        return context -> {
            Certificate source = context.getSource();
            CertificateDto destination = context.getDestination();
            mapSpecificFields(source, destination);
            return context.getDestination();
        };
    }

    public Converter<CertificateDto, Certificate> toEntityConverter() {
        return context -> {
            CertificateDto source = context.getSource();
            Certificate destination = context.getDestination();
            mapSpecificFields(source, destination);
            return context.getDestination();
        };
    }

    public void mapSpecificFields(Certificate source, CertificateDto destination) {
        List<Tag> certificateTags = tagDao.findByCertificateId(source.getId());
        destination.setTags(certificateTags.stream()
                .map(Tag::getName)
                .collect(Collectors.toList()));
    }

    public void mapSpecificFields(CertificateDto source, Certificate destination) {
        List<String> tagsAsString = source.getTags()
                .stream()
                .distinct()
                .collect(Collectors.toList());
        List<Tag> tagsAsObjects = tagsAsString.stream()
                .map(name -> tagDao.findByName(name).orElse(new Tag(name)))
                .collect(Collectors.toList());
        destination.setTags(tagsAsObjects);
    }
}
