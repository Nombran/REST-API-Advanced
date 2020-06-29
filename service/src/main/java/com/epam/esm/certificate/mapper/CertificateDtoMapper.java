package com.epam.esm.certificate.mapper;

import com.epam.esm.certificate.dto.CertificateDto;
import com.epam.esm.certificate.model.Certificate;
import com.epam.esm.tag.dao.TagDao;
import com.epam.esm.tag.model.Tag;
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
    }

    public Converter<Certificate, CertificateDto> toDtoConverter() {
        return context -> {
            Certificate source = context.getSource();
            CertificateDto destination = context.getDestination();
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

}
