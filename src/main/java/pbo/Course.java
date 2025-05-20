package pbo;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

/**
 * @author 12S23007 Joy Valeda Silalahi
 * @author 12S23020 Rachel Simorangkir
 */


@Entity
@Table(name = "courses")
public class Course {
    public Course(String code, String name, int semester, int credits, List<Student> enrolledStudents) {
        this.code = code;
        this.name = name;
        this.semester = semester;
        this.credits = credits;
        this.enrolledStudents = enrolledStudents;
    }

    public Course() {
    }

    @Id
    private String code;
    private String name;
    private int semester;
    private int credits;

    @ManyToMany(mappedBy = "enrolledCourses")
    private List<Student> enrolledStudents;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public List<Student> getEnrolledStudents() {
        return enrolledStudents;
    }

    public void setEnrolledStudents(List<Student> enrolledStudents) {
        this.enrolledStudents = enrolledStudents;
    }

    @Override
    public String toString() {
        return "Course [code=" + code + ", name=" + name + ", semester=" + semester + ", credits=" + credits
                + ", enrolledStudents=" + enrolledStudents + "]";
    }
}