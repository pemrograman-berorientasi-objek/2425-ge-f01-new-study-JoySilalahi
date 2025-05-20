package pbo.model;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity                                                 
@Table(name = "students")
public class Student {
    @Id
    @Column(name = "nim", nullable = false, length = 225)
    private String nim;
    @Column(name = "nama", nullable = false, length = 225)
    private String nama;
    @Column(name = "prodi", nullable = false, length = 225)
    private String prodi;

    public Student(){
        
    }

    public Student(String nim, String nama, String prodi) {
        this.nim = nim;
        this.nama = nama;
        this.prodi = prodi;
    }

    public String getNim() {
        return nim;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public void setProdi(String prodi) {
        this.prodi = prodi;
    }

    public String getNama() {
        return nama;
    }

    public String getProdi() {
        return prodi;
    }

    @Override
    public String toString() {
        return nim + "|" + nama + "|" + prodi;
    }
}



