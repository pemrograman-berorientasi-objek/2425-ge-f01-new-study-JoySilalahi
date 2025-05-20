package pbo;

import javax.persistence.*;
import pbo.model.Student;
import pbo.model.Course;
import pbo.model.Enrollments;

import java.util.List;
import java.util.Scanner;

public class App {
    private static final EntityManagerFactory factory = Persistence.createEntityManagerFactory("StudentPU");
    private static final EntityManager entityManager = factory.createEntityManager();

    public static void main(String[] args) {
        System.out.println("masuk");
        Scanner scanner = new Scanner(System.in);

        while (true) {
            String command = scanner.nextLine();
            String[] argsCommand = command.split("#");

            if ("---".equals(command)) {
                clearData();
                break;
            }

            switch (argsCommand[0]) {
                case "student-add":
                    if (argsCommand.length == 4) addStudent(argsCommand);
                    break;
                case "student-show-all":
                    displayAllStudents();
                    break;
                case "course-add":
                    if (argsCommand.length == 5) addCourse(argsCommand);
                    break;
                case "course-show-all":
                    displayAllCourses();
                    break;
                case "enroll":
                    if (argsCommand.length == 3) enrollStudent(argsCommand);
                    break;
                case "student-detail":
                    if (argsCommand.length == 2) showStudentDetails(argsCommand);
                    break;
                default:
                    System.out.println("Invalid command.");
                    break;
            }
        }

        entityManager.close();
        factory.close();
    }

    private static void addStudent(String[] argsCommand) {
        String nim = argsCommand[1];
        if (entityManager.find(Student.class, nim) != null) {
            System.out.println("Student already exists.");
            return;
        }

        Student student = new Student(nim, argsCommand[2], argsCommand[3]);
        performTransaction(() -> entityManager.persist(student));
    }

    private static void displayAllStudents() {
        List<Student> students = entityManager.createQuery("SELECT s FROM Student s ORDER BY s.nim ASC", Student.class).getResultList();
        if (students.isEmpty()) {
            System.out.println("No students found.");
        } else {
            students.forEach(student -> System.out.println(student.getNim() + "|" + student.getNama() + "|" + student.getProdi()));
        }
    }

    private static void addCourse(String[] argsCommand) {
        String kode = argsCommand[1];
        if (entityManager.find(Course.class, kode) != null) {
            System.out.println("Course already exists.");
            return;
        }

        Course course = new Course(kode, argsCommand[2], Integer.parseInt(argsCommand[3]), Integer.parseInt(argsCommand[4]));
        performTransaction(() -> entityManager.persist(course));
    }

    private static void displayAllCourses() {
        List<Course> courses = entityManager.createQuery("SELECT c FROM Course c ORDER BY c.semester ASC, c.kode ASC", Course.class).getResultList();
        if (courses.isEmpty()) {
            System.out.println("No courses found.");
        } else {
            courses.forEach(course -> System.out.println(course.getKode() + "|" + course.getNama() + "|" + course.getSemester() + "|" + course.getKredit()));
        }
    }

    private static void enrollStudent(String[] argsCommand) {
        String nim = argsCommand[1];
        String kode = argsCommand[2];
        Student student = entityManager.find(Student.class, nim);
        Course course = entityManager.find(Course.class, kode);

        if (student == null || course == null) {
            System.out.println("Student or course not found.");
            return;
        }

        List<Enrollments> enrollments = entityManager.createQuery("SELECT e FROM Enrollments e WHERE e.student = :student AND e.course = :course", Enrollments.class)
                .setParameter("student", student)
                .setParameter("course", course)
                .getResultList();

        if (!enrollments.isEmpty()) {
            System.out.println("Already enrolled.");
        } else {
            performTransaction(() -> entityManager.persist(new Enrollments(student, course)));
        }
    }

    private static void showStudentDetails(String[] argsCommand) {
        String nim = argsCommand[1];
        Student student = entityManager.find(Student.class, nim);
        if (student != null) {
            System.out.println(student.getNim() + "|" + student.getNama() + "|" + student.getProdi());
            List<Course> courses = entityManager.createQuery("SELECT e.course FROM Enrollments e WHERE e.student = :student ORDER BY e.course.semester ASC, e.course.kode ASC", Course.class)
                    .setParameter("student", student)
                    .getResultList();

            if (courses.isEmpty()) {
                System.out.println("No courses enrolled.");
            } else {
                courses.forEach(course -> System.out.println(course.getKode() + "|" + course.getNama() + "|" + course.getSemester() + "|" + course.getKredit()));
            }
        } else {
            System.out.println("Student not found.");
        }
    }

    private static void clearData() {
        performTransaction(() -> {
            entityManager.createQuery("DELETE FROM Enrollments").executeUpdate();
            entityManager.createQuery("DELETE FROM Student").executeUpdate();
            entityManager.createQuery("DELETE FROM Course").executeUpdate();
        });
    }

    private static void performTransaction(Runnable action) {
        try {
            entityManager.getTransaction().begin();
            action.run();
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            System.out.println("Error: " + e.getMessage());
        }
    }
}