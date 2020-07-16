package com.epam.esm.order;

import com.epam.esm.certificate.Certificate;
import com.epam.esm.certificate.CertificateDao;
import com.epam.esm.user.User;
import com.epam.esm.user.UserDao;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderDtoMapper {
    private final ModelMapper mapper;
    private final UserDao userDao;
    private final CertificateDao certificateDao;

    @Autowired
    public OrderDtoMapper(ModelMapper modelMapper,
                         UserDao userDao,
                          CertificateDao certificateDao) {
        this.mapper = modelMapper;
        this.userDao = userDao;
        this.certificateDao = certificateDao;
    }

    @PostConstruct
    public void setupMapper() {
        mapper.createTypeMap(Order.class, OrderDto.class)
                .addMappings(m -> m.skip(OrderDto::setCertificatesIds)).setPostConverter(toDtoConverter())
                .addMappings(m-> m.skip(OrderDto::setUserId)).setPostConverter(toDtoConverter());
        mapper.createTypeMap(OrderDto.class, Order.class)
                .addMappings(m-> m.skip(Order::setCertificates)).setPostConverter(toEntityConverter())
                .addMappings(m-> m.skip(Order::setUser)).setPostConverter(toEntityConverter());
    }

    public Converter<Order, OrderDto> toDtoConverter() {
        return context -> {
            Order source = context.getSource();
            OrderDto destination = context.getDestination();
            mapSpecificFields(source, destination);
            return context.getDestination();
        };
    }

    public Converter<OrderDto, Order> toEntityConverter() {
        return context -> {
            OrderDto source = context.getSource();
            Order destination = context.getDestination();
            mapSpecificFields(source, destination);
            return context.getDestination();
        };
    }

    public void mapSpecificFields(Order source, OrderDto destination) {
        List<Certificate> certificates  = source.getCertificates();
        List<Long> certificateIds = certificates.stream()
                .map(Certificate::getId)
                .collect(Collectors.toList());
        destination.setCertificatesIds(certificateIds);
        destination.setUserId(source.getUser().getId());
    }

    public void mapSpecificFields(OrderDto source, Order destination) {
        List<Long> certificateIds = source.getCertificatesIds();
            List<Certificate> certificates = certificateIds.stream()
                    .map(id -> certificateDao.find(id).orElseThrow(() ->
                            new IllegalArgumentException("Certificate with id + " + id + " doesn't exist")))
                    .collect(Collectors.toList());
            destination.setCertificates(certificates);
            User user = userDao.find(source.getUserId()).orElseThrow(() ->
                    new IllegalArgumentException("User with id " + source.getUserId() + " doesn't exist"));
            destination.setUser(user);
    }
}
