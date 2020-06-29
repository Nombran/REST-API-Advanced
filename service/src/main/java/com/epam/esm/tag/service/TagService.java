package com.epam.esm.tag.service;

import com.epam.esm.certificate.dao.CertificateDao;
import com.epam.esm.exception.ServiceConflictException;
import com.epam.esm.tag.dao.TagDao;
import com.epam.esm.tag.exception.TagNotFoundException;
import com.epam.esm.tag.model.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class TagService {
    private final TagDao tagDao;
    private final CertificateDao certificateDao;

    public TagService(TagDao tagDao,
                      CertificateDao certificateDao) {
        this.tagDao = tagDao;
        this.certificateDao = certificateDao;

    }

    public void create(Tag tag) {
        try {
            tagDao.create(tag);
        } catch (DuplicateKeyException e) {
            log.error("Tag with name " + tag.getName() + "already exists");
            throw new ServiceConflictException("Tag with name " + tag.getName() + " already exists");
        }
    }

    public void delete(long id) {
        try {
            tagDao.delete(id);
        } catch (DataIntegrityViolationException e) {
            log.error("Cannot delete tag with id " + id +
                    " because of relationships with some certificate");
            throw new ServiceConflictException("Cannot delete tag with id " + id +
                    " because of relationships with some certificate");
        }
    }

    public Tag find(long id) {
        Optional<Tag> tag = tagDao.find(id);
        if(tag.isPresent()) {
            return tag.get();
        } else {
            throw new TagNotFoundException("Tag with id = " + id + "doesn't exist");
        }
    }

    public List<Tag> findAll() {
        return tagDao.findAll();
    }

    public List<Tag> findTagsByCertificateId(long id) {
        if(certificateDao.find(id).isPresent()) {
            return tagDao.findByCertificateId(id);
        } else {
            throw new TagNotFoundException("There is no certificate with id = " + id);
        }
    }
}
