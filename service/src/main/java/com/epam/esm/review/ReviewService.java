package com.epam.esm.review;

import com.epam.esm.service.ServiceDao;
import com.epam.esm.service.ServiceNotFoundException;
import com.epam.esm.service.ServiceStatus;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
public class ReviewService {
    private final ModelMapper mapper;
    private final ReviewDao reviewDao;
    private final ServiceDao serviceDao;

    @Autowired
    public ReviewService(ModelMapper mapper,
                         ReviewDao reviewDao,
                         ServiceDao serviceDao) {
        this.mapper = mapper;
        this.reviewDao = reviewDao;
        this.serviceDao = serviceDao;
    }

    public void createReview(int serviceId, ReviewDto reviewDto) {
       com.epam.esm.service.Service service = serviceDao.find(serviceId).orElseThrow(() ->
               new ServiceNotFoundException("service not found")
       );
       service.setStatus(ServiceStatus.COMPLETED);
       serviceDao.update(service);
       Review review = mapper.map(reviewDto, Review.class);
       reviewDao.create(review);
    }
}
