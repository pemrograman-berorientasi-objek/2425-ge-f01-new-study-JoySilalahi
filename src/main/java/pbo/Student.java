package pbo;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

//12S23007 Joy Valeda Silalahi
//12S23020 Rachel Simorangkir

@Entity
@Table(name = "students")
public class Student {
    public Student(String nim, String name, String program, List<Course> enrolledCourses) {
        this.nim = nim;
        this.name = name;
        this.program = program;
        this.enrolledCourses = enrolledCourses;
    }

    public Student() {
    }

    @Id
    private String nim;
    private String name;
    private String program;

    @ManyToMany
    @JoinTable(name = "enrollments",
            joinColumns = @JoinColumn(name = "student_nim"),
            inverseJoinColumns = @JoinColumn(name = "course_code"))
    private List<Course> enrolledCourses;

    public String getNim() {
        return nim;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    @Override
    public String toString() {
        return "Student [nim=" + nim + ", name=" + name + ", program=" + program + ", enrolledCourses="
                + enrolledCourses + "]";
    }

    public List<Course> getEnrolledCourses() {
        return enrolledCourses;
    }

    public void setEnrolledCourses(List<Course> enrolledCourses) {
        this.enrolledCourses = enrolledCourses;
    }
}