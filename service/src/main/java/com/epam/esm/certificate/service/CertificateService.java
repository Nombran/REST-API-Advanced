package com.epam.esm.certificate.service;

import com.epam.esm.certificate.dao.CertificateDao;
import com.epam.esm.certificate.dto.CertificateDto;
import com.epam.esm.certificate.exception.CertificateNotFoundException;
import com.epam.esm.certificate.model.Certificate;
import com.epam.esm.certificate.specification.CertificateSearchSqlBuilder;
import com.epam.esm.certificatetag.dao.CertificateTagDao;
import com.epam.esm.exception.ServiceConflictException;
import com.epam.esm.tag.dao.TagDao;
import com.epam.esm.tag.exception.TagNotFoundException;
import com.epam.esm.tag.model.Tag;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CertificateService {
    private final CertificateDao certificateDao;
    private final TagDao tagDao;
    private final CertificateTagDao certificateTagDao;
    private final ModelMapper modelMapper;

    public CertificateService(TagDao tagDao,
                              CertificateDao certificateDao,
                              ModelMapper modelMapper,
                              CertificateTagDao certificateTagDao) {
        this.tagDao = tagDao;
        this.certificateDao = certificateDao;
        this.modelMapper = modelMapper;
        this.certificateTagDao = certificateTagDao;
    }

    @Transactional
    public void create(CertificateDto certificateDto) {
        Certificate certificate = modelMapper.map(certificateDto, Certificate.class);
        try {
            certificateDao.create(certificate);
        } catch (DuplicateKeyException e) {
            log.error("Certificate with name " + certificate.getName() +
                    " already exists");
            throw new ServiceConflictException("Certificate with name "
                    + certificate.getName() + " already exists");
        }
        List<String> tags = certificateDto.getTags();
        addCertificateTags(tags, certificate.getId());
    }

    @Transactional
    public void update(CertificateDto certificateDto) {
        Certificate certificate = modelMapper.map(certificateDto, Certificate.class);
        long certificateId = certificate.getId();
        try {
            if (!certificateDao.update(certificate)) {
                throw new CertificateNotFoundException("Certificate with id "
                        + certificate.getId() + " doesn't exist");
            }
        } catch (DuplicateKeyException e) {
            log.error("Certificate with name " + certificate.getName() +
                    " already exists");
            throw new ServiceConflictException("Certificate with name "
                    + certificate.getName() + " already exists");
        }
        List<String> tags = certificateDto.getTags();
        updateCertificateTags(tags, certificateId);
    }

    @Transactional
    public void delete(long id) {
        certificateTagDao.deleteByCertificateId(id);
        if (!certificateDao.delete(id)) {
            throw new CertificateNotFoundException("Certificate with id = " + id + " doesn't exist");
        }
    }

    public CertificateDto find(long id) {
        Optional<Certificate> certificate = certificateDao.find(id);
        if (certificate.isPresent()) {
            return modelMapper.map(certificate.get(), CertificateDto.class);
        } else {
            throw new CertificateNotFoundException("Certificate with id = " + id + " doesn't exist");
        }
    }

    public List<CertificateDto> findCertificates(String tagName, String textPart, String orderBy,
                                                 int page, int perPage) {
        CertificateSearchSqlBuilder specification =
                new CertificateSearchSqlBuilder(tagName, textPart, orderBy);
        if (orderBy != null && !specification.checkOrderBy()) {
            throw new IllegalArgumentException("Invalid orderBy parameter");
        }
        String query = specification.getSqlQuery();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("page", (page - 1) * perPage);
        parameters.addValue("perPage", perPage);
        if (tagName != null) {
            parameters.addValue("tag_name", tagName);
        }
        if (textPart != null) {
            parameters.addValue("textPart", "%" + textPart + "%");
        }
        return certificateDao.findCertificates(query, parameters)
                .stream()
                .map(certificate -> modelMapper.map(certificate, CertificateDto.class))
                .collect(Collectors.toList());
    }

    private void addCertificateTags(List<String> tags, long certificateId) {
        tags.stream().
                distinct().
                forEach(tagName -> {
                    Tag tag = tagDao.findByName(tagName).orElseGet(() -> {
                        Tag newTag = new Tag();
                        newTag.setName(tagName);
                        return tagDao.create(newTag);
                    });
                    certificateTagDao.create(certificateId, tag.getId());
                });
    }

    private void updateCertificateTags(List<String> tags, long certificateId) {
        List<Tag> certificateTagsBeforeUpdate = tagDao.findByCertificateId(certificateId);
        certificateTagsBeforeUpdate.forEach(tag -> {
            if (!tags.contains(tag.getName())) {
                certificateTagDao.delete(certificateId, tag.getId());
            }
        });
        List<String> certificateTagNamesBeforeUpdate = certificateTagsBeforeUpdate.stream()
                .map(Tag::getName)
                .collect(Collectors.toList());
        List<String> tagsToAdd = tags.stream()
                .distinct()
                .filter(tagName -> !certificateTagNamesBeforeUpdate.contains(tagName))
                .collect(Collectors.toList());
        addCertificateTags(tagsToAdd, certificateId);
    }

    @Transactional
    public void addCertificateTag(Tag tag, long certificateId) {
        Tag tagToAdd = tagDao.findByName(tag.getName()).orElseGet(() ->
                tagDao.create(tag)
        );
        try {
            certificateTagDao.create(certificateId, tagToAdd.getId());
        } catch (DuplicateKeyException e) {
            log.error("Certificate with id " + certificateId +
                    " already has tag " + tag.getName());
            throw new ServiceConflictException("The certificate already has this tag");
        } catch (DataIntegrityViolationException e) {
            log.error("Certificate with id " + certificateId + " doesn't exist");
            throw new CertificateNotFoundException("Certificate with id "
                    + certificateId + " doesn't exist");
        }
    }

    public void deleteCertificateTag(long certificateId, long tagId) {
        if (certificateDao.find(certificateId).isPresent()) {
            tagDao.findByIdAndCertificateId(tagId, certificateId)
                    .orElseThrow(() -> new TagNotFoundException(
                            "Certificate with id " + certificateId +
                                    " doesnt have tag with id " + tagId)
                    );
            certificateTagDao.delete(certificateId, tagId);
        } else {
            throw new CertificateNotFoundException("Certificate with id "
                    + certificateId + " doesn't exist");
        }
    }
}
