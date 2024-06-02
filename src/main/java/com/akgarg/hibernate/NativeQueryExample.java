package com.akgarg.hibernate;

import com.akgarg.hibernate.entity.Student;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.MutationQuery;
import org.hibernate.query.NativeQuery;

import java.util.List;

import static com.akgarg.hibernate.HConf.getSessionFactory;
import static com.akgarg.hibernate.HibernateUtils.saveStudents;

@SuppressWarnings({"SqlDialectInspection", "SqlNoDataSourceInspection"})
public class NativeQueryExample {

    private static final Logger LOGGER = LogManager.getLogger(NativeQueryExample.class);
    private static final int TOTAL_NUMBER_OF_STUDENTS = 10;

    public static void main(String[] args) {
        final SessionFactory sessionFactory = getSessionFactory();
        final Session session = sessionFactory.openSession();
        saveStudents(session, TOTAL_NUMBER_OF_STUDENTS);

        selectAll(session);
        updateOne(session);

        session.close();
        sessionFactory.close();
    }

    private static void updateOne(final Session session) {
        final MutationQuery updateNativeMutationQuery = session.createNativeMutationQuery("UPDATE student s SET s.name = :name WHERE s.id = :id");
        updateNativeMutationQuery.setParameter("name", "John Doe");
        updateNativeMutationQuery.setParameter("id", 1);

        session.beginTransaction();
        final int updateResult = updateNativeMutationQuery.executeUpdate();
        session.getTransaction().commit();

        LOGGER.info("number of affected rows: {}", updateResult);
    }

    private static void selectAll(final Session session) {
        final NativeQuery<Student> selectAllStudents = session.createNativeQuery("SELECT * FROM student", Student.class);
        final List<Student> students = selectAllStudents.list();
        students.forEach(LOGGER::info);
    }

}
