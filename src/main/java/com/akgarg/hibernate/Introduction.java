package com.akgarg.hibernate;

import com.akgarg.hibernate.entity.Student;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import static com.akgarg.hibernate.HConf.getSessionFactory;

public class Introduction {

    private static final Logger LOGGER = LogManager.getLogger(Introduction.class);

    public static void main(final String[] args) {
        final SessionFactory sessionFactory = getSessionFactory();
        final Session session = sessionFactory.openSession();

        final Student student = getStudent();   // transient state

        final Transaction transaction = session.beginTransaction();
        session.persist(student);   // persistent state

        // if we made any change in student object here, then it would be reflected in database
        // using Hibernate Dirty Checking mechanism because object is still in persistent state here
        // below changes will automatically fire update query due to dirty check mechanism of hibernate.
        student.setName("John Doe");
        student.setTeam("Foo");

        transaction.commit();

        final Student studentByGetMethod = session.get(Student.class, 1);
        final Student studentByLoadMethod = session.getReference(Student.class, 1); // load method is deprecated, so use getReference method instead

        LOGGER.info("student by get(): {}", studentByGetMethod);
        LOGGER.info("student by load(): {}", studentByLoadMethod);

        session.close(); // detaches the student object
        sessionFactory.close();
    }

    private static Student getStudent() {
        final Student student = new Student();
        student.setName("Student 1");
        student.setTeam("B.Tech 1st Year");
        return student;
    }

}
