package com.akgarg.hibernate;

import com.akgarg.hibernate.entity.Student;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import static com.akgarg.hibernate.HConf.getSessionFactory;
import static com.akgarg.hibernate.HibernateUtils.saveStudents;

public class CacheExample {

    private static final int TOTAL_NUMBER_OF_STUDENTS = 10;
    private static final Logger LOGGER = LogManager.getLogger(CacheExample.class);

    public static void main(String[] args) {
        final SessionFactory sessionFactory = getSessionFactory();

        final Session session = sessionFactory.openSession();
        saveStudents(session, TOTAL_NUMBER_OF_STUDENTS);
        session.close();

        final Session session1 = sessionFactory.openSession();
        LOGGER.info("s1 -> Student by id={}: {}", 1, session1.get(Student.class, 1));
        LOGGER.info("s1 -> Student by id={}: {}", 1, session1.get(Student.class, 1));
        session1.close();

        final Session session2 = sessionFactory.openSession();
        LOGGER.info("s2 -> Student by id={}: {}", 1, session2.get(Student.class, 1));
        LOGGER.info("s2 -> Student by id={}: {}", 1, session2.get(Student.class, 1));
        session2.close();

        LOGGER.info("Second level cache hit count: {}", sessionFactory.getStatistics().getSecondLevelCacheHitCount());
        LOGGER.info("Second level cache miss count: {}", sessionFactory.getStatistics().getSecondLevelCacheMissCount());

        sessionFactory.close();
    }

}
