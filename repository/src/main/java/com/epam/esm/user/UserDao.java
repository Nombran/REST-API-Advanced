package com.epam.esm.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class UserDao {
    @PersistenceContext
    private final EntityManager em;

    @Autowired
    public UserDao(EntityManager entityManager) {
        this.em = entityManager;
    }

    public void create(User user) {
        em.persist(user);
    }

    public void update(User user) {
        em.merge(user);
    }

    public void delete(User user) {
        em.remove(user);
    }

    public Optional<User> find(long id) {
        return Optional.ofNullable(em.find(User.class,id));
    }

    public List<User> findAll() {
        TypedQuery<User> query = em.createQuery(
                "select u from User u",
                User.class);
        return query.getResultList();
    }

    public List<User> findUsers(Integer page, Integer perPage) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<User> query = cb.createQuery(User.class);
        Root<User> root = query.from(User.class);
        query.select(root);

        return em.createQuery(query)
                .setFirstResult((page-1) * perPage)
                .setMaxResults(perPage)
                .getResultList();
    }

    public Long findAllUserCount() {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<User> root = criteriaQuery.from(User.class);
        criteriaQuery.select(criteriaBuilder.count(root));
        return em.createQuery(criteriaQuery)
                .getSingleResult();
    }

    public Optional<User> findUserByLogin(String login) {
        TypedQuery<User> query = em.createQuery("select u from User u where u.login=:login",User.class);
        query.setParameter("login", login);
        try {
            return Optional.ofNullable(query.getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }
}
