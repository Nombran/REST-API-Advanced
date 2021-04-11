package com.epam.esm.tag;

import com.epam.esm.service.ServiceDao;
import com.epam.esm.service.ServiceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class TagService {
    private final TagDao tagDao;
    private final ServiceDao serviceDao;
    private final ModelMapper modelMapper;

    public TagService(TagDao tagDao,
                      ServiceDao serviceDao,
                      ModelMapper modelMapper) {
        this.tagDao = tagDao;
        this.serviceDao = serviceDao;
        this.modelMapper = modelMapper;
    }

    public TagDto create(TagDto tagDto) {
        Tag tag = modelMapper.map(tagDto, Tag.class);
            tagDao.create(tag);
            tagDto.setId(tag.getId());
            return tagDto;
    }

    public void delete(long id) {
            Tag tag = tagDao.find(id).orElseThrow(() ->
                 new TagNotFoundException("There is no tag with id " + id)
            );
            tagDao.delete(tag);
    }

    public TagDto find(long id) {
        Optional<Tag> tag = tagDao.find(id);
        if(tag.isPresent()) {
            return modelMapper.map(tag.get(),TagDto.class);
        } else {
            throw new TagNotFoundException("Tag with id = " + id + "doesn't exist");
        }
    }

    public PagedModel<TagDto> findTags(Integer page, Integer perPage, String textPart) {
        List<TagDto> tags = tagDao.findTags(page, perPage, textPart).stream()
                .map(tag -> modelMapper.map(tag,TagDto.class))
                .collect(Collectors.toList());
        long countOfTags = tagDao.getCountOfTags(textPart);
        return PagedModel.of(tags, new PagedModel.PageMetadata(perPage,page,countOfTags));
    }

    public List<TagDto> findTagsByCertificateId(long id) {
        if(serviceDao.find(id).isPresent()) {
            return tagDao.findByCertificateId(id).stream()
                    .map(tag -> modelMapper.map(tag, TagDto.class))
                    .collect(Collectors.toList());
        } else {
            throw new ServiceNotFoundException("There is no certificate with id = " + id);
        }
    }

    public TagDto GetValuedUsersMostPopularTag() {
        Tag tag = tagDao.GetValuedUsersMostPopularTag().orElseThrow(()->
                new TagNotFoundException("Cannot find tag. Not enough data."));
        return modelMapper.map(tag, TagDto.class);
    }
}
