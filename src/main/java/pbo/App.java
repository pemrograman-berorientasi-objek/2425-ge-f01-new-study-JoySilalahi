package pbo;

//12S23007-Joy Valeda Silalahi
//12S23020-Rachel Simorangkir

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import pbo.model.Student;
import pbo.model.Course;
import pbo.model.Enrollments;

import java.util.List;
import java.util.Scanner;

public class App {
    private static EntityManagerFactory factory;
    private static EntityManager entityManager;

    public static void main(String[] args) {
        factory = Persistence.createEntityManagerFactory("StudentPU");
        entityManager = factory.createEntityManager();
    
        String command;
        Scanner scanner = new Scanner(System.in);
    
        // Main loop for processing commands
        while (true) {
            command = scanner.nextLine();
            String[] split = command.split("#");

            if (command.equals("---")) {
                cleanTables();
                break;
            }

            // Handle student addition
            if (split[0].equals("student-add")) {
                if (split.length < 4) {
                    System.out.println("Invalid command format! Example: student-add#nim#name#program");
                    continue;
                }
                addStudent(split[1], split[2], split[3]);
            }

            // Handle displaying all students
            else if (split[0].equals("student-show-all")) {
                showAllStudents();
            }

            // Handle course addition
            else if (split[0].equals("course-add")) {
                if (split.length < 5) {
                    System.out.println("Invalid command format! Example: course-add#code#name#semester#credits");
                    continue;
                }
                addCourse(split[1], split[2], Integer.parseInt(split[3]), Integer.parseInt(split[4]));
            }

            else if (split[0].equals("course-show-all")) {
                showAllCourses();
            }

            else if (split[0].equals("enroll")) {
                if (split.length < 3) {
                    System.out.println("Invalid command format! Example: enroll#nim#courseCode");
                    continue;
                }
                enrollStudentInCourse(split[1], split[2]);
            }

            // Handle showing student details and enrolled courses
            else if (split[0].equals("student-detail")) {
                if (split.length < 2) {
                    System.out.println("Invalid command format! Example: student-detail#nim");
                    continue;
                }
                showStudentDetail(split[1]);
            }
        }

        entityManager.close();
        factory.close();
    }

    private static void addStudent(String nim, String nama, String prodi) {
        Student existingStudent = entityManager.find(Student.class, nim);
        if (existingStudent != null) {
            System.out.println("Student already exists.");
            return;
        }

        Student student = new Student(nim, nama, prodi);

        try {
            entityManager.getTransaction().begin();
            entityManager.persist(student);
            entityManager.flush();
            entityManager.getTransaction().commit();
            System.out.println("Student added successfully.");
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            System.out.println("Failed to add student: " + e.getMessage());
        }
    }

    private static void showAllStudents() {
        entityManager.getTransaction().begin();
        TypedQuery<Student> query = entityManager.createQuery("SELECT s FROM Student s ORDER BY s.nim ASC", Student.class);
        List<Student> students = query.getResultList();
        entityManager.getTransaction().commit();

        if (students.isEmpty()) {
            System.out.println("No students found.");
        } else {
            students.forEach(student -> System.out.println(student.getNim() + "|" + student.getNama() + "|" + student.getProdi()));
        }
    }

    private static void addCourse(String kode, String nama, int semester, int kredit) {
        Course existingCourse = entityManager.find(Course.class, kode);
        if (existingCourse != null) {
            System.out.println("Course already exists.");
            return;
        }

        Course course = new Course(kode, nama, semester, kredit);

        try {
            entityManager.getTransaction().begin();
            entityManager.persist(course);
            entityManager.flush();
            entityManager.getTransaction().commit();
            System.out.println("Course added successfully.");
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            System.out.println("Failed to add course: " + e.getMessage());
        }
    }

    private static void showAllCourses() {
        entityManager.getTransaction().begin();
        TypedQuery<Course> query = entityManager.createQuery("SELECT c FROM Course c ORDER BY c.semester ASC, c.kode ASC", Course.class);
        List<Course> courses = query.getResultList();
        entityManager.getTransaction().commit();

        if (courses.isEmpty()) {
            System.out.println("No courses found.");
        } else {
            courses.forEach(course -> System.out.println(course.getKode() + "|" + course.getNama() + "|" + course.getSemester() + "|" + course.getKredit()));
        }
    }

    private static void enrollStudentInCourse(String nim, String kode) {
        Student student = entityManager.find(Student.class, nim);
        if (student == null) {
            System.out.println("Student not found.");
            return;
        }

        Course course = entityManager.find(Course.class, kode);
        if (course == null) {
            System.out.println("Course not found.");
            return;
        }

        TypedQuery<Enrollments> enrollmentQuery = entityManager.createQuery("SELECT e FROM Enrollments e WHERE e.student = :student AND e.course = :course", Enrollments.class)
                .setParameter("student", student)
                .setParameter("course", course);

        List<Enrollments> existingEnrollments = enrollmentQuery.getResultList();

        if (!existingEnrollments.isEmpty()) {
            System.out.println("Student is already enrolled in the course.");
            return;
        }

        Enrollments enrollment = new Enrollments(student, course);

        try {
            entityManager.getTransaction().begin();
            entityManager.persist(enrollment);
            entityManager.flush();
            entityManager.getTransaction().commit();
            System.out.println("Student enrolled in the course successfully.");
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            System.out.println("Failed to enroll student: " + e.getMessage());
        }
    }

    private static void showStudentDetail(String nim) {
        Student student = entityManager.find(Student.class, nim);
        if (student == null) {
            System.out.println("Student not found.");
            return;
        }

        System.out.println(student.getNim() + "|" + student.getNama() + "|" + student.getProdi());

        TypedQuery<Course> query = entityManager.createQuery("SELECT e.course FROM Enrollments e WHERE e.student = :student ORDER BY e.course.semester ASC, e.course.kode ASC", Course.class)
                .setParameter("student", student);
        List<Course> enrolledCourses = query.getResultList();

        if (enrolledCourses.isEmpty()) {
            System.out.println("No enrolled courses found.");
        } else {
            enrolledCourses.forEach(course -> System.out.println(course.getKode() + "|" + course.getNama() + "|" + course.getSemester() + "|" + course.getKredit()));
        }
    }

    private static void cleanTables() {
        try {
            entityManager.getTransaction().begin();

            entityManager.createQuery("DELETE FROM Enrollments").executeUpdate();
            entityManager.createQuery("DELETE FROM Student").executeUpdate();
            entityManager.createQuery("DELETE FROM Course").executeUpdate();

            entityManager.getTransaction().commit();
            System.out.println("Tables cleaned successfully.");
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            System.out.println("Failed to clean tables: " + e.getMessage());
        }
    }
}
