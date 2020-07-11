package com.epam.esm.order.dao;

import com.epam.esm.order.model.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
@Slf4j
public class OrderDao {
    @PersistenceContext
    private final EntityManager em;

    @Autowired
    public OrderDao(EntityManager em) {
        this.em = em;
    }

    public void create(Order order) {
        order.setPurchaseDate(LocalDateTime.now());
        em.persist(order);
    }

    public void update(Order order) {
        em.merge(order);
    }

    public void delete(Order order) {
        em.remove(order);
    }

    public Optional<Order> findById(long id) {
        return Optional.ofNullable(em.find(Order.class, id));
    }

    public List<Order> getOrdersByUserId(long userId) {
        TypedQuery<Order> query = em.createQuery("select o from Order o where o.user.id =: id",
                Order.class);
        query.setParameter("id", userId);
        return query.getResultList();
    }

    public Optional<Order> findOrderByUserIdAndOrderId(long userId, long orderId) {
        TypedQuery<Order> query = em.createQuery("select o from Order o where o.id =: orderId " +
                "and o.user.id =: userId", Order.class);
        query.setParameter("userId", userId);
        query.setParameter("orderId", orderId);
        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }
}
