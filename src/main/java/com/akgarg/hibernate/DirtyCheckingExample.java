package com.akgarg.hibernate;

import com.akgarg.hibernate.entity.Student;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.UUID;

import static com.akgarg.hibernate.HConf.getSessionFactory;

public class DirtyCheckingExample {

    private static final Logger LOGGER = LogManager.getLogger(DirtyCheckingExample.class);

    public static void main(String[] args) {
        final SessionFactory sessionFactory = getSessionFactory();
        final Session session = sessionFactory.openSession();

        final Student student = getStudent(); // student object is in Transient state

        session.beginTransaction();
        session.persist(student);   // student object is in Persistent state
        LOGGER.info("before committing first transaction");
        session.getTransaction().commit();
        LOGGER.info("first transaction committed");

        // even after prev transaction is committed, hibernate will still track student object for dirty checking mechanism
        LOGGER.info("student after first transaction: {}", session.get(Student.class, 1));
        student.setTeam(UUID.randomUUID().toString());

        // changes are reflected in persistent context, so we would get updated value but value is not updated in database
        LOGGER.info("student after first transaction after making changes: {}", session.get(Student.class, 1));

        /*
         * since, we didn't perform any new transaction, no changed would be reflected back to database.
         * But, if we start any transaction within same session then these changes made after prev transaction would be updated to database.
         *
         * For example, we're starting a new empty transaction
         * */
        LOGGER.info("starting second transaction");
        session.beginTransaction();
        LOGGER.info("before committing second transaction");
        session.getTransaction().commit();
        LOGGER.info("second transaction committed");

        /*
         * after committing second transaction, all changes made to object would be synced with database
         * because object is in persistent state. Now, make object detached from session.
         * */

        session.detach(student);
        LOGGER.info("student after object detach: {}", session.get(Student.class, 1));

        student.setTeam("Team Original");

        // we will get object without above changes because student object is detached from session
        LOGGER.info("student after object detach update: {}", session.get(Student.class, 1));

        session.beginTransaction();
        session.getTransaction().commit();

        LOGGER.info("student after object detach update transaction: {}", session.get(Student.class, 1));

        session.close();
        sessionFactory.close();
    }

    private static Student getStudent() {
        final Student student = new Student();
        student.setName("John Doe");
        student.setTeam("Team 1");
        return student;
    }
}
