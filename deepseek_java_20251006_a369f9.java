package com.coursehost.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "modules")
public class Module {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String title;

    private String description;
    private Integer moduleOrder;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    // Constructors, Getters, and Setters
    public Module() {}

    public Module(String title, String description, Integer moduleOrder, Course course) {
        this.title = title;
        this.description = description;
        this.moduleOrder = moduleOrder;
        this.course = course;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getModuleOrder() { return moduleOrder; }
    public void setModuleOrder(Integer moduleOrder) { this.moduleOrder = moduleOrder; }
    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
}