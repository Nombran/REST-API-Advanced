package com.epam.esm.certificate.service;

import com.epam.esm.certificate.dao.CertificateDao;
import com.epam.esm.certificate.dto.CertificateDto;
import com.epam.esm.certificate.exception.CertificateNotFoundException;
import com.epam.esm.certificate.model.Certificate;
import com.epam.esm.exception.ServiceConflictException;
import com.epam.esm.tag.dao.TagDao;
import com.epam.esm.tag.dto.TagDto;
import com.epam.esm.tag.exception.TagNotFoundException;
import com.epam.esm.tag.model.Tag;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CertificateService {
    private final CertificateDao certificateDao;
    private final TagDao tagDao;
    private final ModelMapper modelMapper;

    public CertificateService(TagDao tagDao,
                              CertificateDao certificateDao,
                              ModelMapper modelMapper) {
        this.tagDao = tagDao;
        this.certificateDao = certificateDao;
        this.modelMapper = modelMapper;
    }

    public void create(CertificateDto certificateDto) {
        Certificate certificate = modelMapper.map(certificateDto, Certificate.class);
        try {
            certificateDao.create(certificate);
        } catch (DataIntegrityViolationException e) {
            log.error("Certificate with name " + certificate.getName() +
                    " already exists");
            throw new ServiceConflictException("Certificate with name "
                    + certificate.getName() + " already exists");
        }
    }

    public void update(CertificateDto certificateDto) {
        Certificate certificate = modelMapper.map(certificateDto, Certificate.class);
        long certificateId = certificate.getId();
        Certificate beforeUpdate = certificateDao.find(certificateId)
                .orElseThrow(() -> new CertificateNotFoundException("Certificate with id "
                        + certificate.getId() + " doesn't exist"));
        certificate.setCreationDate(beforeUpdate.getCreationDate());
        try {
            certificateDao.update(certificate);
        } catch (DataIntegrityViolationException e) {
            log.error("Certificate with name " + certificate.getName() +
                    " already exists");
            throw new ServiceConflictException("Certificate with name "
                    + certificate.getName() + " already exists");
        }
    }

    @Transactional
    public void patch(long id, CertificateDto changes) {
        Certificate certificate = certificateDao.find(id).orElseThrow(() ->
                new CertificateNotFoundException("Certificate with id = " + id + " doesn't exist")
        );
        CertificateDto certificateDto = modelMapper.map(certificate, CertificateDto.class);
        mergeObjects(certificateDto, changes);

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<CertificateDto>> violations = validator.validate(certificateDto);
        if(!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        update(certificateDto);
    }

    public void mergeObjects(CertificateDto certificate, CertificateDto changes) {
        String newName = changes.getName();
        String newDescription = changes.getDescription();
        BigDecimal newPrice = changes.getPrice();
        Integer newDuration = changes.getDuration();
        List<String> newTags = changes.getTags();
        if(newName != null) {
            certificate.setName(newName);
        }
        if(newDescription != null) {
            certificate.setDescription(newDescription);
        }
        if(newPrice != null) {
            certificate.setPrice(newPrice);
        }
        if(newDuration != null) {
            certificate.setDuration(newDuration);
        }
        if(newTags != null) {
            certificate.setTags(newTags);
        }
    }

    public void delete(long id) {
        Certificate certificate = certificateDao.find(id)
                .orElseThrow(() -> new CertificateNotFoundException("Certificate with id = " + id + " doesn't exist"));
        certificateDao.delete(certificate);
    }

    public CertificateDto find(long id) {
        Optional<Certificate> certificate = certificateDao.find(id);
        if (certificate.isPresent()) {
            return modelMapper.map(certificate.get(), CertificateDto.class);
        } else {
            throw new CertificateNotFoundException("Certificate with id = " + id + " doesn't exist");
        }
    }

    public List<CertificateDto> findCertificates(String[] tagNames, String textPart, String orderBy,
                                                 int page, int perPage) {
        List<String> tags;
        if(tagNames != null) {
            tags = Arrays.asList(tagNames);
        } else tags = Collections.emptyList();
        return certificateDao.findCertificates(tags, textPart, orderBy, page, perPage).stream()
                .map(certificate -> modelMapper.map(certificate, CertificateDto.class))
                .collect(Collectors.toList());
    }


    public void addCertificateTag(TagDto tagDto, long certificateId) {
        Certificate certificate = certificateDao.find(certificateId)
                .orElseThrow(() -> new CertificateNotFoundException("Certificate with id "
                        + certificateId + " doesn't exist"));
        Tag tag = modelMapper.map(tagDto, Tag.class);
        Tag tagToAdd = tagDao.findByName(tag.getName()).orElseGet(() -> {
            tagDao.create(tag);
            return tag;
        });
        if (certificate.getTags().contains(tagToAdd)) {
            throw new ServiceConflictException("The certificate already has this tag");
        } else {
            certificate.getTags().add(tagToAdd);
            certificateDao.update(certificate);
        }
    }

    public void deleteCertificateTag(long certificateId, long tagId) {
        Certificate certificate = certificateDao.find(certificateId)
                .orElseThrow(() -> new CertificateNotFoundException("Certificate with id "
                        + certificateId + " doesn't exist"));
        List<Tag> certificateTags = certificate.getTags();
        if (certificateTags.stream().anyMatch(tag -> tag.getId() == tagId)) {
            certificateTags.removeIf(tag -> tag.getId() == tagId);
        }
        certificateDao.update(certificate);
    }
}
