package com.akgarg.hibernate;

import com.akgarg.hibernate.entity.Student;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.MutationQuery;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

import static com.akgarg.hibernate.HConf.getSessionFactory;
import static com.akgarg.hibernate.HibernateUtils.saveStudents;

public class HQLExample {

    private static final Logger LOGGER = LogManager.getLogger(HQLExample.class);
    private static final int TOTAL_NUMBER_OF_STUDENTS = 10;

    public static void main(String[] args) {
        final SessionFactory sessionFactory = getSessionFactory();
        final Session session = sessionFactory.openSession();

        saveStudents(session, TOTAL_NUMBER_OF_STUDENTS);
        findAllStudents(session);
        findById(session);
        getWithPagination(session);
        updateById(session);
        deleteById(session);

        session.close();
        sessionFactory.close();
    }

    private static void findAllStudents(final Session session) {
        final Query<Student> fromStudentQuery = session.createQuery("FROM Student", Student.class);
        final List<Student> allStudents = fromStudentQuery.list();
        allStudents.forEach(LOGGER::info);
    }

    private static void findById(final Session session) {
        final int studentId = 1;
        final Query<Student> studentByIdQuery = session.createQuery("FROM Student s WHERE s.id = :id", Student.class);
        studentByIdQuery.setParameter("id", studentId);
        final Optional<Student> studentByIdOptional = studentByIdQuery.uniqueResultOptional();
        studentByIdOptional.ifPresentOrElse(
                student -> LOGGER.info("Student found by id {}: {}", studentId, student),
                () -> LOGGER.info("Student not found with id={}", studentId)
        );
    }

    private static void updateById(final Session session) {
        final int studentId = 1;
        final MutationQuery updateByIdQuery = session.createMutationQuery("UPDATE Student s SET s.name=:name WHERE s.id=:id");
        updateByIdQuery.setParameter("name", "John Doe");
        updateByIdQuery.setParameter("id", studentId);

        session.beginTransaction();
        final int updateResult = updateByIdQuery.executeUpdate();
        session.getTransaction().commit();

        session.clear();

        LOGGER.info("Number of affected rows after updateById={}: {}", studentId, updateResult);
        findAllStudents(session);
    }

    private static void deleteById(final Session session) {
        final int studentId = 3;
        final MutationQuery deleteByIdQuery = session.createMutationQuery("DELETE FROM Student s WHERE s.id = :id");
        deleteByIdQuery.setParameter("id", studentId);

        session.beginTransaction();
        final int deleteResult = deleteByIdQuery.executeUpdate();
        session.getTransaction().commit();

        LOGGER.info("Number of affected rows after deleteById={}: {}", studentId, deleteResult);
        findAllStudents(session);
    }

    private static void getWithPagination(final Session session) {
        final Query<Student> fromStudentPaginatedQuery = session.createQuery("FROM Student", Student.class);
        fromStudentPaginatedQuery.setFirstResult(0);
        fromStudentPaginatedQuery.setMaxResults(5);
        final List<Student> students = fromStudentPaginatedQuery.getResultList();
        students.forEach(LOGGER::info);
    }

}
