package pbo;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

///12S23007 Joy Valeda Silalahi
//12S23020 Rachel Simorangkir

public class App {
    private static final String PERSISTENCE_UNIT_NAME = "study_plan_pu";
    private static EntityManagerFactory factory;

    public static void main(String[] args) {
        factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManager em = factory.createEntityManager();
        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            if (command.equals("---")) {
                break;
            }
            String[] tokens = command.split("#");

            if (tokens[0].equals("student-add")) {
                String nim = tokens[1];
                String name = tokens[2];
                String program = tokens[3];
                registerStudent(em, nim, name, program);
            } else if (tokens[0].equals("student-show-all")) {
                showAllStudents(em);
            } else if (tokens[0].equals("course-add")) {
                String code = tokens[1];
                String name = tokens[2];
                int semester = Integer.parseInt(tokens[3]);
                int credits = Integer.parseInt(tokens[4]);
                addCourse(em, code, name, semester, credits);
            } else if (tokens[0].equals("course-show-all")) {
                showAllCourses(em);
            } else if (tokens[0].equals("enroll")) {
                String nim = tokens[1];
                String courseCode = tokens[2];
                enrollStudentInCourse(em, nim, courseCode);
            } else if (tokens[0].equals("student-show")) {
                String nim = tokens[1];
                showStudentDetail(em, nim);
            }
        }

        em.close();
        factory.close();
    }

    private static void registerStudent(EntityManager em, String nim, String name, String program) {
        em.getTransaction().begin();

        Student student = em.find(Student.class, nim);
        if (student == null) {
            student = new Student();
            student.setNim(nim);
            student.setName(name);
            student.setProgram(program);
            student.setEnrolledCourses(new ArrayList<>());

            em.persist(student);
            em.getTransaction().commit();

        } else {
            em.getTransaction().rollback();
        }
    }

    private static void showAllStudents(EntityManager em) {
        TypedQuery<Student> query = em.createQuery("SELECT s FROM Student s ORDER BY s.nim", Student.class);
        List<Student> students = query.getResultList();

        for (Student student : students) {
            System.out.println(student.getNim() + "|" + student.getName() + "|" + student.getProgram());
        }
    }

    private static void addCourse(EntityManager em, String code, String name, int semester, int credits) {
        em.getTransaction().begin();

        Course course = em.find(Course.class, code);
        if (course == null) {
            course = new Course();
            course.setCode(code);
            course.setName(name);
            course.setSemester(semester);
            course.setCredits(credits);

            em.persist(course);
            em.getTransaction().commit();

        } else {
            em.getTransaction().rollback();
        }
    }

    private static void showAllCourses(EntityManager em) {
        TypedQuery<Course> query = em.createQuery("SELECT c FROM Course c ORDER BY c.semester, c.code", Course.class);
        List<Course> courses = query.getResultList();

        for (Course course : courses) {
            System.out.println(course.getCode() + "|" + course.getName() + "|" +
                    course.getSemester() + "|" + course.getCredits());
        }
    }

    private static void enrollStudentInCourse(EntityManager em, String nim, String courseCode) {
        em.getTransaction().begin();

        Student student = em.find(Student.class, nim);
        Course course = em.find(Course.class, courseCode);

        if (student != null && course != null) {
            List<Course> enrolledCourses = student.getEnrolledCourses();
            if (!enrolledCourses.contains(course)) {
                enrolledCourses.add(course);
            }
        }

        em.getTransaction().commit();
    }

    private static void showStudentDetail(EntityManager em, String nim) {
        Student student = em.find(Student.class, nim);
        if (student != null) {
            System.out.println(student.getNim() + "|" + student.getName() + "|" + student.getProgram());
            List<Course> enrolledCourses = student.getEnrolledCourses();
            enrolledCourses.sort((c1, c2) -> {
                if (c1.getSemester() == c2.getSemester()) {
                    return c1.getCode().compareTo(c2.getCode());
                }
                return Integer.compare(c1.getSemester(), c2.getSemester());
            });
            for (Course course : enrolledCourses) {
                System.out.println(course.getCode() + "|" + course.getName() + "|" +
                        course.getSemester() + "|" + course.getCredits());
            }
        }
    }
}