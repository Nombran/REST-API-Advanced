package com.epam.esm.service;

import com.epam.esm.tag.Tag;
import com.epam.esm.tag.Tag_;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Repository
public class ServiceDao {
    @PersistenceContext
    private final EntityManager em;
    private static final String SQL_FIND_NON_INACTIVE_CERTIFICATE_BY_NAME = "select c from Service c" +
            " where c.status in(:active,:published)" +
            " and c.name=:name";

    @Autowired
    public ServiceDao(EntityManager em) {
        this.em = em;
    }


    public void create(Service service) {
        LocalDateTime creationDate = LocalDateTime.now();
        service.setCreationDate(creationDate);
        em.persist(service);
    }

    public void update(Service service) {
        LocalDateTime modificationDate = LocalDateTime.now();
        service.setModificationDate(modificationDate);
        em.merge(service);
    }

    public Optional<Service> find(long id) {
        return Optional.ofNullable(em.find(Service.class, id));
    }

    public List<Service> findCertificates(ServiceParamWrapper wrapper) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Service> query = cb.createQuery(Service.class);
        Root<Service> root = query.from(Service.class);
        query.select(root);

        String[] tagNamesArray = wrapper.getTagNames();
        List<String> tagNames = null;
        if(tagNamesArray != null) {
            tagNames = Arrays.asList(tagNamesArray);
        }

        ServiceStatus[] statusArray = wrapper.getStatuses();
        List<ServiceStatus> statuses = null;
        if(statusArray != null) {
            statuses = Arrays.asList(statusArray);
        }

        String textPart = wrapper.getTextPart();
        String orderBy = wrapper.getOrderBy();
        int page = wrapper.getPage();
        int perPage = wrapper.getPerPage();

        prepareSearchQuery(query, root, tagNames, textPart, statuses);

        if (orderBy != null) {
            query.orderBy(cb.desc(root.get(orderBy)));
        }

        return em.createQuery(query)
                .setFirstResult((page - 1) * perPage)
                .setMaxResults(perPage)
                .getResultList();
    }

    public int getTotalElementsCountFromCertificateSearch(ServiceParamWrapper wrapper) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Service> root = query.from(Service.class);

        List<String> tagNames = Collections.emptyList();
        String[] tagNamesArray = wrapper.getTagNames();
        if(tagNamesArray != null) {
            tagNames = Arrays.asList(tagNamesArray);
        }

        ServiceStatus[] statusArray = wrapper.getStatuses();
        List<ServiceStatus> statuses = null;
        if(statusArray != null) {
            statuses = Arrays.asList(statusArray);
        }

        String textPart = wrapper.getTextPart();

        prepareSearchQuery(query, root, tagNames, textPart, statuses);

        query.select(cb.countDistinct(root));

        if(tagNames.size() == 0) {
            return em.createQuery(query)
                    .getSingleResult()
                    .intValue();
        } else {
            return em.createQuery(query)
                    .getResultList().size();
        }
    }

    public <X> void prepareSearchQuery(AbstractQuery<X> query, Root<Service> root,
                                       List<String> tagNames, String textPart, List<ServiceStatus> statuses) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        List<Predicate> predicates = new ArrayList<>();

        if (textPart != null && !textPart.isEmpty()) {
            Predicate predicateForName = cb.like(cb.lower(root.get(Service_.NAME)), "%" + textPart + "%");
            Predicate predicateForDescription = cb.like(root.get(Service_.description), "%" + textPart + "%");
            Predicate textPartPredicate = cb.or(predicateForName, predicateForDescription);
            predicates.add(textPartPredicate);
        }

        if(statuses != null && !statuses.isEmpty()) {
            Predicate statusPredicate = root.get(Service_.status).in(statuses);
            predicates.add(statusPredicate);
        }

        if (tagNames != null && tagNames.size() != 0) {
            ListJoin<Service, Tag> tagJoin = root.join(Service_.tags);
            Expression<Long> countOfCertificateTagsInGroup = cb.count(root);
            Predicate predicateCertificateTagsInInputList = tagJoin.get(Tag_.NAME).in(tagNames);
            predicates.add(predicateCertificateTagsInInputList);
            query.where(cb.and(predicates.toArray(new Predicate[0])))
                    .having(cb.equal(countOfCertificateTagsInGroup, tagNames.size()))
                    .groupBy(root);
        } else {
            query.where(cb.and(predicates.toArray(new Predicate[0])));
        }
    }

    public Optional<Service> findNonInactiveCertificateByName(String name) {
        TypedQuery<Service> typedQuery = em.createQuery(
                SQL_FIND_NON_INACTIVE_CERTIFICATE_BY_NAME,
                Service.class);
        typedQuery.setParameter("active", ServiceStatus.ACTIVE);
        typedQuery.setParameter("published", ServiceStatus.PUBLISHED);
        typedQuery.setParameter("name", name);
        try {
            Service service = typedQuery.getSingleResult();
            return Optional.ofNullable(service);
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }
}
