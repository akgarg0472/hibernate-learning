package com.akgarg.hibernate;

import com.akgarg.hibernate.entity.Student;
import com.github.javafaker.Faker;
import org.hibernate.Session;

public final class HibernateUtils {

    private static final Faker FAKER = new Faker();

    private HibernateUtils() {
        throw new IllegalStateException();
    }

    static void saveStudents(final Session session, final int totalNumberOfStudents) {
        for (int i = 0; i < totalNumberOfStudents; i++) {
            final Student student = new Student();
            student.setName(FAKER.name().fullName());
            student.setTeam(FAKER.random().hex());
            session.beginTransaction();
            session.persist(student);
            session.getTransaction().commit();
        }
    }

}
