# Hibernate

Hibernate is an ORM framework for Java to interact with databases in a seamless manner. It is widely used to let
developers perform database operations without writing queries themselves; instead, Hibernate writes the queries behind
the scenes. It provides simple and intuitive APIs to seamlessly interact with databases and simplify the development
process.

# Configuration

Hibernate requires configuration metadata to interact with databases. Some of the core and important configurations
include DB URL, schema, driver, username, password, dialect, etc. We can configure Hibernate either by using an XML
configuration file or Java-based configuration.

### XML Configuration

By default, Hibernate uses **hibernate.cfg.xml** and this file is typically defined in the **resource** directory of
maven or gradle project.

```xml
<?xml version = "1.0" encoding = "utf-8"?>
<!DOCTYPE hibernate-configuration SYSTEM
        "http://www.hibernate.org/dtd/hibernate-configuration-3.0.dtd">
<hibernate-configuration>
    <session-factory>
        <property name="connection.driver_class">com.mysql.cj.jdbc.Driver</property>
        <property name="connection.url">jdbc:mysql://127.0.0.1:3306/database_name</property>
        <property name="connection.username">username</property>
        <property name="connection.password">password</property>
        <property name="dialect">org.hibernate.dialect.MySQLDialect</property>
        <property name="hbm2ddl.auto">update</property>
        <property name="show_sql">true</property>
        <property name="format_sql">true</property>
        <mapping class="fully.qualified.entity.ClassName"/>
    </session-factory>
</hibernate-configuration>
```

### Java Configuration

If you want to omit XML configuration completely, then following java configuration can be also used to configure
Hibernate.

```java
public static void main(final String[] args) {

    final Map<String, Object> hibernateSettings = Map.of(
            JdbcSettings.JAKARTA_JDBC_DRIVER, "com.mysql.cj.jdbc.Driver",
            JdbcSettings.JAKARTA_JDBC_USER, "username",
            JdbcSettings.JAKARTA_JDBC_PASSWORD, "password",
            JdbcSettings.JAKARTA_JDBC_URL, "jdbc:mysql://127.0.0.1:3306/database_name",
            JdbcSettings.SHOW_SQL, true,
            JdbcSettings.FORMAT_SQL, true,
            SchemaToolingSettings.HBM2DDL_AUTO, "update"
    );

    final ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
            .applySettings(hibernateSettings)
            .build();

    final Configuration configuration = new Configuration();

    // Add your entity classes here
    /*
        configuration.addAnnotatedClass(MyEntity1.class);
        configuration.addAnnotatedClass(MyEntity2.class);
        configuration.addAnnotatedClass(MyEntity3.class);
     */

    final SessionFactory sessionFactory = configuration
            .buildSessionFactory(serviceRegistry);
    final Session session = sessionFactory.openSession();

}
```

# Working with Hibernate

- First, we need an instance of the `SessionFactory`. It represents an instance of Hibernate, maintaining and governing
  runtime behavior, operations, entities, etc., based on the configuration provided.
- Second, we need a `Session` instance, which is the primary interface between the Java application and the underlying
  database. It provides the APIs to perform CRUD operations and execute transactions in the database.

```java
final SessionFactory sessionFactory = new Configuration()
        .configure()
        .buildSessionFactory();
final Session session = sessionFactory.openSession();
```

By default, Hibernate picks the **hibernate.cfg.xml** file from the **resources** directory of the project. If the file
is placed elsewhere, we can specify the path using the following method:

```java
final String configFilePath = "/absolute/path/of/configuration/file.xml";
final SessionFactory sessionFactory = new Configuration()
        .configure(configFilePath)
        .buildSessionFactory();
final Session session = sessionFactory.openSession();
```

Now that we have a `Session` instance, we can perform database operations using this Session instance.

### Hibernate Entity

Hibernate entity class in hibernate represents the table/document metadata on which CRUD operations would be performed
by hibernate.  
Following are
some important annotations that are used to let hibernate know about schema and constraints for the entity.

| Annotation      | Target | Description                                                                                      |
|-----------------|--------|--------------------------------------------------------------------------------------------------|
| @Entity         | class  | Marks a class as an entity for Hibernate.                                                        |
| @Table          | class  | Defines table-related properties such as name, indexes, etc.                                     |
| @Id             | field  | Marks the primary key of the entity.                                                             |
| @Column         | field  | Defines column constraints such as name, default value, nullable, definition, etc.               |
| @Transient      | field  | Indicates that the field is not to be persisted to the table/document.                           |
| @Lob            | field  | Indicates that the field is a large object or BLOB storage.                                      |
| @GeneratedValue | field  | Specifies that the value of this field is to be generated by Hibernate using a defined strategy. |
| @OneToOne       | field  | Defines a one-to-one relationship between entities.                                              |
| @OneToMany      | field  | Defines a one-to-many relationship between entities.                                             |
| @ManyToOne      | field  | Defines a many-to-one relationship between entities.                                             |
| @ManyToMany     | field  | Defines a many-to-many relationship between entities.                                            |

These are the commonly used annotations in the Hibernate framework.

### CRUD operations

Now that we have the `SessionFactory` and `Session` instances, and all entities are configured, we can start performing
CRUD operations using Hibernate. Assume we have a Student entity with the following attributes:

- id
- name
- clazz
- section

We can perform **Create** or **insertion** in the following manner:

```java
import com.akgarg.hibernate.entity.Student;
import org.hibernate.SessionFactory;

public static void main(String[] args) {
    final SessionFactory sessionFactory = getSessionFactory();
    final Session session = sessionFactory.openSession();

    final Student student = new Student();
    student.setId(1);
    student.setName("Student 1");
    student.setTeam("B.Tech 1st Year");

    final Transaction transaction = session.beginTransaction();
    session.persist(student);
    transaction.commit();

    session.close();
    sessionFactory.close();
}
```

We can perform Read or get operation in the following manner:

```java
public static void main(String[] args) {
    final SessionFactory sessionFactory = getSessionFactory();
    final Session session = sessionFactory.openSession();

    final int id = 1;
    final Student studentGet = session.get(Student.class, id);
    final Student studentLoad = session.load(Student.class, id);

    System.out.println("Fetched studentGet: " + studentGet);
    System.out.println("Fetched studentLoad: " + studentLoad);
}
```

We can perform update operation in the following manner:

```java
public static void main(String[] args) {
    final SessionFactory sessionFactory = getSessionFactory();
    final Session session = sessionFactory.openSession();

    session.beginTransaction();
    final int id = 1;
    final Student student = session.get(Student.class, id);
    student.setProperty(value);
    session.update(student);
    session.getTransaction().commit();

    session.close();
    sessionFactory.close();
}
```

We can perform delete operation in the following manner:

```java
public static void main(String[] args) {
    final SessionFactory sessionFactory = getSessionFactory();
    final Session session = sessionFactory.openSession();

    session.beginTransaction();
    final int id = 1;
    final Student student = session.get(Student.class, id);
    session.delete(student);
    session.getTransaction().commit();

    session.close();
    sessionFactory.close();
}
```

## Hibernate get() vs load():

`get()` and `load()` are two key methods to fetch the entity using ID. Both methods behave similarly but differ in the
following ways:

| get()                                                                                                                | load()                                                                                                                                                                                            |
|----------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| syntax: `get(EntityClass.class, ID)`                                                                                 | syntax: `get(EntityClass.class, ID) `                                                                                                                                                             |
| Returns null if the entity is not found in Hibernate cache or database, else returns the entity object.              | Returns a proxy object instead of the actual object, and if the entity is not found in the Hibernate cache or database, it throws ObjectNotFoundException                                         |
| Directly hits the DB if the object for the requested ID is not found in the Hibernate session cache.                 | Since load() returns a HibernateProxy for the entity, it doesn't hit the DB if the object is not found in the session cache. Instead, it hits only when the returned proxy entity object is used. |
| Eagerly initializes the entity instance, meaning it tries to fully initialize the entity object as soon as possible. | Lazily initializes the entity instance, reducing overall DB hits and improving performance.                                                                                                       |
| Used when we are not sure if the object exists in the DB or not.                                                     | Used when we are sure that the entity exists for the provided ID.                                                                                                                                 |

## Hibernate Object States:

In Hibernate, an object could have three states:

- Transient
- Persistent
- Detached

### Transient:

An entity object is said to be in a **transient** state if it is not being managed by any session object or it is not
attached to any session object.

### Persistent:

An entity object is said to be in a **persistent** state when it is attached to a session instance. An object moves to
the persistent state when we either fetch it from the database or perform save/update operations on it.

### Detached:

An entity object is said to be in a detached state when it was previously associated with a session but is no longer
associated or managed by any session. This can be done by either closing the session or manually detaching
using `session.detach(object).`

```java
public static void main(String[] args) {
    final SessionFactory sessionFactory = getSessionFactory();
    final Session session = sessionFactory.openSession();

    // student object is in transient state because it is not being manager by any session
    final Student student = new Student();
    student.setName("John Doe");
    student.setClazz("Foo");
    student.setSection("Bar");

    session.beginTransaction();
    session.persist(student);   // session object is in persistent state
    session.getTransaction().commit();

    // session.detach(student);    // session object becomes detached

    session.close();    // makes student object detached if we are not calling session#detach manually
    sessionFactory.close();
}
```

## Hibernate GenerationType:

The GenerationType is used in conjunction with the `@GeneratedValue` annotation to specify how Hibernate should generate
and assign the primary key identifier for an entity. Here are all the strategies provided by Hibernate:

- **AUTO**:  Hibernate decides the strategy for identifier generation based on the underlying database. This is often
  the default choice and delegates the decision to Hibernate, which selects the best strategy depending on the database
  capabilities and configuration.
- **IDENTITY**: Lets the database provider generate the identifier for the entity. For example, this strategy uses `auto
  increment` in MySQL databases. It is performant for most cases where the database supports auto ID generation. This
  strategy is simple and efficient as it leverages the database's native ID generation capabilities.
- **SEQUENCE**: Relies on the database's sequence generator object. Using sequences, Hibernate can pre-allocate a pool
  or set of IDs and then assign them to newly added records, making it performant for cases where the database supports
  sequence generation, such as PostgreSQL and Oracle. This strategy provides flexibility and efficiency in ID
  generation.
- **TABLE**: Not recommended because it uses a dedicated table in the database to maintain and generate primary keys,
  which significantly impacts overall performance. This strategy should be avoided unless there is a specific
  requirement for using a table-based approach.
- **UUID**: Allows to Hibernate to use UUIDs for ID generation, which guarantees uniqueness even in distributed systems.
  However, it is less performant when the dataset becomes large due to the length and complexity of UUIDs. This strategy
  is useful for ensuring global uniqueness across distributed systems but comes with performance trade-offs.

Each strategy has its own use cases and performance considerations, and the choice depends on the specific requirements
of the application and the capabilities of the underlying database. However, in most cases, **IDENTITY** or **SEQUENCE**
are the preferred choices due to their efficiency and performant nature.

## Hibernate Cascading:

Hibernate cascading refers to the automatic persistence operations (such as save, update, delete) that are propagated
from a parent entity to its associated child entities. When a cascade option is set on a parent-child relationship in
Hibernate, any changes made to the parent entity will be cascaded to its associated child entities, ensuring consistency
in the database.

Here are some common cascade options in Hibernate:

- **CascadeType.ALL**: This option cascades all operations (save, update, delete) from the parent to the child entities.

- **CascadeType.PERSIST**: Cascades the persist operation from the parent to the child entities, ensuring that new child
  entities are persisted when the parent entity is persisted.

- **CascadeType.MERGE**: Cascades the merge operation from the parent to the child entities, ensuring that changes made
  to detached child entities are merged into the persistent context when the parent entity is merged.

- **CascadeType.REMOVE**: Cascades the remove operation from the parent to the child entities, ensuring that child
  entities are deleted when the parent entity is deleted.

- **CascadeType.REFRESH**: Cascades the refresh operation from the parent to the child entities, ensuring that child
  entities are refreshed from the database when the parent entity is refreshed.

```java

@Getter
@Setter
@ToString
@Entity
@Table(name = "parent")
public class Parent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Child> children = new ArrayList<>();

}

@Getter
@Setter
@ToString
@Entity
@Table(name = "child")
public class Child {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Parent parent;

}
```

In this example, `Parent` has a one-to-many relationship with `Child`, mapped by the `children` field.
The `cascade = CascadeType.ALL` attribute on the `@OneToMany` annotation indicates that all operations (save, update,
delete) performed on Parent entities should be cascaded to their associated Child entities.

Now, when we perform operations on a Parent entity, such as saving, updating, or deleting, the corresponding
operations will be cascaded to its associated Child entities.

```java
public static void main(String[] args) {
    // Creating a Parent entity with associated Child entities
    Parent parent = new Parent();
    Child child1 = new Child();
    Child child2 = new Child();
    parent.getChildren().add(child1);
    parent.getChildren().add(child2);

    // Saving the Parent entity will also save the associated Child entities due to cascading
    session.beginTransaction();
    session.save(parent);
    session.getTransaction().commit();

    // Updating or deleting the Parent entity will similarly cascade the operation to its associated Child entities
    session.beginTransaction();
    // Retrieve the parent entity from the database
    Parent parentToUpdate = session.get(Parent.class, parentId);
    // Update the parent entity
    // This will cascade to associated child entities if CascadeType.UPDATE is set
    parentToUpdate.setName("Updated Name");
    session.getTransaction().commit();

    session.beginTransaction();
    // Delete the parent entity
    // This will cascade to associated child entities if CascadeType.DELETE is set
    session.delete(parentToUpdate);
    session.getTransaction().commit();
}
```

## Hibernate Query Language (HQL):

Hibernate Query Language (HQL) is a powerful object-oriented query language offered by Hibernate. It provides developers
with a flexible and robust mechanism for querying data within Hibernate applications. Unlike SQL, HQL operates on entity
classes and their properties, rather than directly on database tables and columns. This makes HQL queries more readable
and maintainable, especially for developers familiar with object-oriented programming concepts.

**Key Features of HQL:**

- **Database Independence**: HQL is independent of the underlying database allowing us to write queries that work
  seamlessly across different database platforms.
- **Platform Agnostic**: As a result of database independence, HQL is platform-agnostic. We can write the same HQL
  queries and expect them to function on different database platforms supported by Hibernate.
- **Supports Complex Queries**: HQL supports complex queries like joins, aggregations (COUNT, SUM, AVG etc.), and more.
  This allows us to retrieve and manipulate data efficiently.
- **Runtime Query Generation**: HQL allows for query generation at runtime, providing flexibility for dynamic data
  access scenarios.
- **PreparedStatement Binding**: HQL supports parameter binding using PreparedStatement. This helps prevent SQL
  injection attacks by separating the query logic from the data.
- **Entity-Centric Approach**: HQL follows an entity-centric approach, aligning well with the object-oriented
  programming paradigm. This makes it easier to write queries that map directly to our application's domain model.

### Working with HQL

Let's solidify this concept by comparing HQL and SQL queries using a sample Student entity class:

```java

@Entity
@Table(name = "student")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "team", nullable = false)
    private String team;

}
```

#### Find All:

`SQL query: SELECT * FROM student`

```java
private void findAllStudents(final Session session) {
    final Query<Student> fromStudentQuery = session.createQuery("FROM Student", Student.class);
    final List<Student> allStudents = fromStudentQuery.list();
    allStudents.forEach(LOGGER::info);
    session.close();
}
```

#### Find By ID:

`SQL query: SELECT * FROM student WHERE id=?`

```java
private void findById(final Session session) {
    final int studentId = 1;
    final Query<Student> studentByIdQuery = session.createQuery("FROM Student s WHERE s.id = :id", Student.class);
    studentByIdQuery.setParameter("id", studentId);
    final Optional<Student> studentByIdOptional = studentByIdQuery.uniqueResultOptional();
    studentByIdOptional.ifPresentOrElse(
            student -> LOGGER.info("Student found by id {}: {}", studentId, student),
            () -> LOGGER.info("Student not found with id={}", studentId)
    );
    session.close();
}
```

#### Find With Pagination:

`SQL query: SELECT * FROM student LIMIT ?, ?`

```java
private void getWithPagination(final Session session) {
    final Query<Student> fromStudentPaginatedQuery = session.createQuery("FROM Student", Student.class);
    fromStudentPaginatedQuery.setFirstResult(0);
    fromStudentPaginatedQuery.setMaxResults(5);
    final List<Student> students = fromStudentPaginatedQuery.getResultList();
    students.forEach(LOGGER::info);
    session.close();
}
```

#### Update By Condition:

`SQL query: UPDATE student SET column_name=? WHERE condition=some_condition`

```java
private void updateById(final Session session) {
    final int studentId = 1;
    final MutationQuery updateByIdQuery = session.createMutationQuery("UPDATE Student s SET s.name=:name WHERE s.id=:id");
    updateByIdQuery.setParameter("name", "John Doe");
    updateByIdQuery.setParameter("id", studentId);

    session.beginTransaction();
    final int updateResult = updateByIdQuery.executeUpdate();
    session.getTransaction().commit();

    session.clear(); // clears the cache maintained by Hibernate session

    LOGGER.info("Number of affected rows after updateById={}: {}", studentId, updateResult);
    findAllStudents(session);
    session.close();
}
```

#### Delete by ID:

`SQL query: DELETE FROM student WHERE id=?`

```java
private void deleteById(final Session session) {
    final int studentId = 3;
    final MutationQuery deleteByIdQuery = session.createMutationQuery("DELETE FROM Student s WHERE s.id = :id");
    deleteByIdQuery.setParameter("id", studentId);

    session.beginTransaction();
    final int deleteResult = deleteByIdQuery.executeUpdate();
    session.getTransaction().commit();

    LOGGER.info("Number of affected rows after deleteById={}: {}", studentId, deleteResult);
    findAllStudents(session);
    session.close();
}
```

Below are some of the most commonly used HQL queries and their description:

| HQL                                                      | Description                                                  |
|----------------------------------------------------------|--------------------------------------------------------------|
| `FROM EntityName`                                        | Retrieve all instances of a specific entity                  |
| `SELECT e.property FROM EntityName e`                    | Retrieve specific properties of entities                     |
| `SELECT COUNT(*) FROM EntityName`                        | Count the number of instances of a specific entity           |
| `SELECT DISTINCT e.property FROM EntityName e`           | Retrieve distinct values of a property from entities         |
| `SELECT e FROM EntityName e WHERE condition`             | Filter entities based on specific conditions                 |
| `UPDATE EntityName SET property = value WHERE condition` | Update entity properties based on specific conditions        |
| `DELETE FROM EntityName WHERE condition`                 | Delete entities based on specific conditions                 |
| `DELETE FROM EntityName WHERE condition`                 | Delete entities based on specific conditions                 |
| `JOIN FETCH e.association`                               | Fetch associated entities eagerly                            |
| `LEFT JOIN e.association`                                | Perform a left join with associated entities                 |
| `ORDER BY e.property`                                    | Order query results based on a property                      |
| `GROUP BY e.property HAVING condition`                   | Group query results based on a property and apply conditions |

### Executing Native Query:

Hibernate allows developers to execute native SQL queries alongside its HQL (Hibernate Query Language) for more complex
or specific database operations. Native SQL queries are written in the standard SQL syntax specific to the underlying
database system. When using native SQL queries, developers should ensure compatibility with the database system and
consider potential performance implications.

```java
private void selectAll(final Session session) {
    final NativeQuery<Student> selectAllStudents = session.createNativeQuery("SELECT * FROM student", Student.class);
    final List<Student> students = selectAllStudents.list();
    students.forEach(LOGGER::info);
}

private void updateOne(final Session session) {
    final MutationQuery updateNativeMutationQuery = session.createNativeMutationQuery("UPDATE student s SET s.name = :name WHERE s.id = :id");
    updateNativeMutationQuery.setParameter("name", "John Doe");
    updateNativeMutationQuery.setParameter("id", 1);

    session.beginTransaction();
    final int updateResult = updateNativeMutationQuery.executeUpdate();
    session.getTransaction().commit();

    LOGGER.info("number of affected rows: {}", updateResult);
}
```

One critical consideration when using native SQL queries is the necessity to update them whenever there's a change in
the table schema. This dependency on the schema makes native queries less adaptable and more susceptible to errors.

## Criteria API

The Hibernate Criteria API is a powerful tool for building dynamic queries in a type-safe manner, especially when
compared to raw SQL or HQL (Hibernate Query Language). It is a way to construct database queries using an
object-oriented approach. It allows us to build queries programmatically by composing criteria objects with conditions
and filters.

- find All:

  ```java
  private void findAll(final Session session) {
    final HibernateCriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
    final CriteriaQuery<Student> criteriaQuery = criteriaBuilder.createQuery(Student.class);
    final Root<Student> root = criteriaQuery.from(Student.class);
    criteriaQuery.select(root);
    final Query<Student> query = session.createQuery(criteriaQuery);
    query.getResultList().forEach(LOGGER::info);
    session.close();
  }
  ```

- Find by ID:

  ```java
  private void findById(final Session session) {
      final HibernateCriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
      final JpaCriteriaQuery<Student> criteriaQuery = criteriaBuilder.createQuery(Student.class);
      final JpaRoot<Student> root = criteriaQuery.from(Student.class);
  
      criteriaQuery.select(root)
              .where(criteriaBuilder.greaterThan(root.get("id"), 1));
  
      final Query<Student> query = session.createQuery(criteriaQuery);
      query.getResultList().forEach(LOGGER::info);
  }
  ```
- order By ID desc:
  ```java
  private void orderByIdDesc(final Session session) {
      final HibernateCriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
      final JpaCriteriaQuery<Student> criteriaQuery = criteriaBuilder.createQuery(Student.class);
      final JpaRoot<Student> root = criteriaQuery.from(Student.class);
  
      criteriaQuery.select(root).orderBy(criteriaBuilder.desc(root.get("id")));
  
      final Query<Student> query = session.createQuery(criteriaQuery);
      query.getResultList().forEach(LOGGER::info);
      session.close();
  }
  ```
- update by ID:

  ```java
  private void updateById(final Session session) {
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
  ```

- property value Like:
  ```java
  private void nameLike(final Session session) {
    final HibernateCriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
    final JpaCriteriaQuery<Student> criteriaQuery = criteriaBuilder.createQuery(Student.class);
    final JpaRoot<Student> root = criteriaQuery.from(Student.class);
  
    criteriaQuery.select(root).where(criteriaBuilder.like(root.get("name"), "%John%"));
  
    final Query<Student> query = session.createQuery(criteriaQuery);
    query.getResultList().forEach(LOGGER::info);
    session.close();
  }
  ```
- delete by ID:
  ```java
  private void deleteById(final Session session) {
    final HibernateCriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
    final JpaCriteriaDelete<Student> criteriaQuery = criteriaBuilder.createCriteriaDelete(Student.class);
    final JpaRoot<Student> root = criteriaQuery.from(Student.class);
  
    criteriaQuery.where(criteriaBuilder.equal(root.get("id"), 1));
  
    final MutationQuery deleteQuery = session.createMutationQuery(criteriaQuery);
    session.beginTransaction();
    final int deleteResult = deleteQuery.executeUpdate();
    LOGGER.info("delete query deleted '{}' rows", deleteResult);
    session.getTransaction().commit();
    session.close();
  }
  ```

## Hibernate Caching

Hibernate use caching mechanism out of the box to improve performance of the application. Hibernate caching is a crucial
aspect of optimizing performance in Hibernate-based applications. It aims to reduce the number of times an application
needs to access the database by storing frequently accessed data in memory.

There are several levels of caching in Hibernate:

- **First Level Cache:** This cache is associated with the Hibernate **Session** object. It is **enabled by default**and
  works within the boundaries of a single session. It helps in reducing the number of SQL queries sent to the database
  by caching entities and their associated state. It's worth noting that the first level cache is not shared among
  different sessions.

- **Second Level Cache:** This cache sits at the **SessionFactory** level, meaning it's shared among all sessions. It
  caches objects across sessions and transactions, making it more effective in reducing database hits. Hibernate
  provides pluggable support for second level caching through various providers
  like `Ehcache`, `Infinispan`, `Hazelcast`, etc. We need to configure and manage this cache explicitly in our Hibernate
  configuration.

- **Query Level Cache:** Hibernate also provides support for caching query results. When enabled, it caches the results
  of queries executed against the database, allowing subsequent executions of the same query to retrieve results from
  the cache rather than hitting the database again.

### Configuring Second Level Cache:

- Add dependency for second level cache provider (ehcache in this example)

- Configure following properties in **hibernate.conf.xml**:
    - `hibernate.cache.use_second_level_cache=true`
    - `hibernate.cache.region.factory_class=org.hibernate.cache.ehcache.EhCacheRegionFactory`

- Add following annotations to entity class to enable caching for the entity:
    - `@Cacheable`
    - `@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)`

**NOTE:** Ehcache has not received updates since 2022 and is no longer actively maintained. To ensure continued support
and compatibility, consider switching to alternative cache providers.

## Hibernate Dirty Checking:

Hibernate dirty checking mechanism is a key feature that helps hibernate to manage changes to persistent objects. This
helps Hibernate optimize database queries so that only the fields that have changed are updated.

Here's how it works internally:

- **Persistent Object Tracking**: When an object is associated with a Hibernate session, it becomes a managed entity.
  Hibernate keeps track of these managed entities using an internal data structure called the `Persistence Context`.

- **Snapshot of Original State**: When an entity is loaded into the Persistence Context, Hibernate takes a snapshot of
  its state. This snapshot represents the object's original state when it was retrieved from or saved to the database.

- **Detecting Changes**: As the application modifies the entity's properties, Hibernate compares the current state of
  the entity with its snapshot to detect any changes. This process is known as dirty checking.

- **Automatic Updates**: When Hibernate detects that an entity's state has changed, it marks the entity as "dirty." This
  means that the entity needs to be synchronized with the database to reflect the changes. Hibernate automatically
  generates the necessary SQL statements to update the database with the modified state of the entity.

- **Optimization**: Hibernate employs various optimizations to minimize the overhead of dirty checking. For example, it
  may use lazy loading to defer loading related entities until they are actually needed, reducing the number of objects
  that need to be tracked and checked for changes.

- **Transaction Boundary**: Dirty checking occurs within the scope of a transaction. When a transaction is committed,
  Hibernate flushes the changes to the database, ensuring that the database remains consistent with the changes made to
  the managed entities. Even after the transaction is committed, Hibernate still keeps track of the entities in its
  Persistence Context. This means that if any further changes are made to these entities outside a transaction,
  Hibernate will continue to perform dirty checking on them as necessary.

- **Session Persistence**: Hibernate dirty checking mechanism operates within the context of a Hibernate Session, which
  typically corresponds to a database transaction. However, Hibernate sessions can be configured to extend beyond a
  single transaction, allowing dirty checking to continue across multiple transactions within the same session (as
  mentioned in above point).

NOTE: Last two points (Transaction Boundary and Session Persistence) might be confusing but this is one of the most
important concept in Hibernate. Consider following example to understand above points better.

```java
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
```

Output of above code would be:

```text
Hibernate: insert into student (name,team) values (?,?)
2024-06-02 23:56:19.630 [main] DirtyCheckingExample.main - before committing first transaction
2024-06-02 23:56:19.637 [main] DirtyCheckingExample.main - first transaction committed
2024-06-02 23:56:19.641 [main] DirtyCheckingExample.main - student after first transaction: Student(id=1, name=John Doe, team=Team 1)
2024-06-02 23:56:19.641 [main] DirtyCheckingExample.main - student after first transaction after making changes: Student(id=1, name=John Doe, team=61d1a34a-0c4c-45cf-9d5c-58b9aa34e268)

2024-06-02 23:56:19.641 [main] DirtyCheckingExample.main - starting second transaction
2024-06-02 23:56:19.642 [main] DirtyCheckingExample.main - before committing second transaction
Hibernate: update student set name=?,team=? where id=?
2024-06-02 23:56:19.648 [main] DirtyCheckingExample.main - second transaction committed

Hibernate: select s1_0.id,s1_0.name,s1_0.team from student s1_0 where s1_0.id=?
2024-06-02 23:56:19.658 [main] DirtyCheckingExample.main - student after object detach: Student(id=1, name=John Doe, team=61d1a34a-0c4c-45cf-9d5c-58b9aa34e268)

2024-06-02 23:56:19.659 [main] DirtyCheckingExample.main - student after object detach update: Student(id=1, name=John Doe, team=61d1a34a-0c4c-45cf-9d5c-58b9aa34e268)

2024-06-02 23:56:19.660 [main] DirtyCheckingExample.main - student after object detach update transaction: Student(id=1, name=John Doe, team=61d1a34a-0c4c-45cf-9d5c-58b9aa34e268)
```

Exploring Hibernate's dirty check mechanism provides insight into its inner workings and highlights how it can lead to
unexpected behavior in a program. Recognizing its operation is crucial for managing and mitigating any unforeseen
consequences
