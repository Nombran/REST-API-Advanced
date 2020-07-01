package com.epam.esm.tag.service;

import com.epam.esm.certificate.dao.CertificateDao;
import com.epam.esm.exception.ServiceConflictException;
import com.epam.esm.tag.dao.TagDao;
import com.epam.esm.tag.dto.TagDto;
import com.epam.esm.tag.exception.TagNotFoundException;
import com.epam.esm.tag.model.Tag;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TagService {
    private final TagDao tagDao;
    private final CertificateDao certificateDao;
    private final ModelMapper modelMapper;

    public TagService(TagDao tagDao,
                      CertificateDao certificateDao,
                      ModelMapper modelMapper) {
        this.tagDao = tagDao;
        this.certificateDao = certificateDao;
        this.modelMapper = modelMapper;
    }

    public void create(TagDto tagDto) {
        Tag tag = modelMapper.map(tagDto, Tag.class);
        try {
            tagDao.create(tag);
        } catch (DataIntegrityViolationException e) {
            log.error("Tag with name " + tag.getName() + "already exists");
            throw new ServiceConflictException("Tag with name " + tag.getName() + " already exists");
        }
    }

    public void delete(long id) {
        try {
            Tag tag = tagDao.find(id).orElseThrow(() ->
                 new TagNotFoundException("There is no tag with id " + id)
            );
            tagDao.delete(tag);
        } catch (DataIntegrityViolationException e) {
            log.error("Cannot delete tag with id " + id +
                    " because of relationships with some certificate");
            throw new ServiceConflictException("Cannot delete tag with id " + id +
                    " because of relationships with some certificate");
        }
    }

    public TagDto find(long id) {
        Optional<Tag> tag = tagDao.find(id);
        if(tag.isPresent()) {
            return modelMapper.map(tag.get(),TagDto.class);
        } else {
            throw new TagNotFoundException("Tag with id = " + id + "doesn't exist");
        }
    }

    public List<TagDto> findAll() {
        return tagDao.findAll().stream()
                .map(tag -> modelMapper.map(tag,TagDto.class))
                .collect(Collectors.toList());
    }

    public List<TagDto> findTagsByCertificateId(long id) {
        if(certificateDao.find(id).isPresent()) {
            return tagDao.findByCertificateId(id).stream()
                    .map(tag -> modelMapper.map(tag, TagDto.class))
                    .collect(Collectors.toList());
        } else {
            throw new TagNotFoundException("There is no certificate with id = " + id);
        }
    }
}
