package com.akgarg.hibernate;

import com.akgarg.hibernate.entity.Student;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.MutationQuery;
import org.hibernate.query.criteria.*;

import static com.akgarg.hibernate.HConf.getSessionFactory;
import static com.akgarg.hibernate.HibernateUtils.saveStudents;

public class CriteriaExample {

    private static final Logger LOGGER = LogManager.getLogger(CriteriaExample.class);
    private static final int TOTAL_NUMBER_OF_STUDENTS = 10;

    public static void main(String[] args) {
        final SessionFactory sessionFactory = getSessionFactory();
        final Session session = sessionFactory.openSession();
        saveStudents(session, TOTAL_NUMBER_OF_STUDENTS);
        session.close();

        findAll(sessionFactory.openSession());
        findById(sessionFactory.openSession());
        orderByIdDesc(sessionFactory.openSession());
        updateById(sessionFactory.openSession());
        nameHaving(sessionFactory.openSession());
        deleteById(sessionFactory.openSession());

        sessionFactory.close();
    }

    private static void deleteById(final Session session) {
        final EntityManager entityManager = session.getEntityManagerFactory().createEntityManager();
        final HibernateCriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        final JpaCriteriaDelete<Student> criteriaQuery = criteriaBuilder.createCriteriaDelete(Student.class);
        final JpaRoot<Student> root = criteriaQuery.from(Student.class);

        criteriaQuery.where(criteriaBuilder.equal(root.get("id"), 1));

        final Query deleteQuery = entityManager.createQuery(criteriaQuery);
        session.beginTransaction();
        final int deleteResult = deleteQuery.executeUpdate();
        LOGGER.info("delete query deleted '{}' rows", deleteResult);
        session.getTransaction().commit();
        entityManager.close();
        session.close();
    }

    private static void nameHaving(final Session session) {
        final EntityManager entityManager = session.getEntityManagerFactory().createEntityManager();
        final HibernateCriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        final JpaCriteriaQuery<Student> criteriaQuery = criteriaBuilder.createQuery(Student.class);
        final JpaRoot<Student> root = criteriaQuery.from(Student.class);

        criteriaQuery.select(root).where(criteriaBuilder.like(root.get("name"), "%John%"));

        final TypedQuery<Student> query = entityManager.createQuery(criteriaQuery);
        query.getResultList().forEach(LOGGER::info);
        entityManager.close();
        session.close();
    }

    private static void updateById(final Session session) {
        final HibernateCriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        final JpaCriteriaUpdate<Student> criteriaQuery = criteriaBuilder.createCriteriaUpdate(Student.class);
        final JpaRoot<Student> root = criteriaQuery.from(Student.class);

        criteriaQuery.set("name", "John Doe");
        criteriaQuery.where(criteriaBuilder.equal(root.get("id"), 1));

        final MutationQuery updateQuery = session.createMutationQuery(criteriaQuery);
        session.beginTransaction();
        final int updateResult = updateQuery.executeUpdate();
        session.getTransaction().commit();
        LOGGER.info("update query updated '{}' rows", updateResult);
        session.close();
    }

    private static void orderByIdDesc(final Session session) {
        final EntityManager entityManager = session.getEntityManagerFactory().createEntityManager();
        final HibernateCriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        final JpaCriteriaQuery<Student> criteriaQuery = criteriaBuilder.createQuery(Student.class);
        final JpaRoot<Student> root = criteriaQuery.from(Student.class);

        criteriaQuery.select(root).orderBy(criteriaBuilder.desc(root.get("id")));

        final TypedQuery<Student> query = entityManager.createQuery(criteriaQuery);
        query.getResultList().forEach(LOGGER::info);
        entityManager.close();
        session.close();
    }

    private static void findById(final Session session) {
        final EntityManager entityManager = session.getEntityManagerFactory().createEntityManager();
        final HibernateCriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        final JpaCriteriaQuery<Student> criteriaQuery = criteriaBuilder.createQuery(Student.class);
        final JpaRoot<Student> root = criteriaQuery.from(Student.class);

        criteriaQuery.select(root)
                .where(criteriaBuilder.greaterThan(root.get("id"), 1));

        final TypedQuery<Student> query = entityManager.createQuery(criteriaQuery);
        query.getResultList().forEach(LOGGER::info);
        entityManager.close();
        session.close();
    }

    private static void findAll(final Session session) {
        final EntityManager entityManager = session.getEntityManagerFactory().createEntityManager();
        final HibernateCriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        final CriteriaQuery<Student> criteriaQuery = criteriaBuilder.createQuery(Student.class);
        final Root<Student> root = criteriaQuery.from(Student.class);
        criteriaQuery.select(root);
        final TypedQuery<Student> query = entityManager.createQuery(criteriaQuery);
        query.getResultList().forEach(LOGGER::info);
        entityManager.close();
        session.close();
    }

}
