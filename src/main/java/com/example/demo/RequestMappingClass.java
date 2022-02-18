package com.example.demo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class RequestMappingClass {

    @RequestMapping("/student")
    public ResponseEntity addAll() {
        Student student = new Student(1, "Jasur", "Jasurov", "4-kurs");
        Student student1 = new Student(2, "Mansur", "Mansurov", "4-kurs");
        Student student2 = new Student(3, "Alisher", "Alisherov", "4-kurs");
        Student student3 = new Student(4, "Qahhor", "Qahharov", "4-kurs");

        List<Student> students = new ArrayList<>();
        students.add(student);
        students.add(student1);
        students.add(student2);
        students.add(student3);

        return ResponseEntity.ok(students);

    }

    @RequestMapping("/student1")
    public ResponseEntity addAll1() {
        Student student = new Student(1, "Shaxlo", "Rahimova", "3-kurs");
        Student student1 = new Student(2, "Diyora", "BAxtiyorova", "3-kurs");
        Student student2 = new Student(3, "Gulzoda", "Solijonova", "3-kurs");
        Student student3 = new Student(4, "Dilnoza", "Tolanova", "3-kurs");

        List<Student> studen = new ArrayList<>();
        studen.add(student);
        studen.add(student1);
        studen.add(student2);
        studen.add(student3);

        return ResponseEntity.ok(studen);
    }

    @GetMapping("/student2/{id}")
    public ResponseEntity bir(@PathVariable long id) {
        Student student = new Student(id, "Sardor", "Sardorov", "3-kurs");
        return ResponseEntity.ok(student);
    }

    @GetMapping("/student3/{name}")
    public ResponseEntity bir1(@PathVariable String name) {
        Student student = new Student(2, name, "Toxirov", "2-kurs");
        return ResponseEntity.ok(student);
    }

    @PutMapping("/student4")
    public ResponseEntity<Student> create(@RequestBody Student student) {
        return ResponseEntity.ok(student);
    }

    @PutMapping("/student5")
    public ResponseEntity as(@RequestBody String name) {
        return ResponseEntity.ok(name);
    }

    @PostMapping("/student6")
    public ResponseEntity ass(@RequestBody String name) {
        return ResponseEntity.ok(name);
    }

    @GetMapping("/oq")
    public ResponseEntity param(@RequestParam long id,
                                @RequestParam String name,
                                @RequestParam String lastName,
                                @RequestParam String course) {
        Student student = new Student(id, name, lastName, course);
        return ResponseEntity.ok(student);
    }


}