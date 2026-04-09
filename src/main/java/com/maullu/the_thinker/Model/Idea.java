package com.maullu.the_thinker.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Idea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Visibility is required")
    @Enumerated(EnumType.STRING)
    private Visibility visibility;

    @NotNull(message = "Owner ID is required")
    private Long ownerId;

    public Idea() {}

    public Idea(Long id, String title, String description, Visibility visibility, Long ownerId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.visibility = visibility;
        this.ownerId = ownerId;
    }

    // Getters y setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Visibility getVisibility() { return visibility; }
    public void setVisibility(Visibility visibility) { this.visibility = visibility; }

    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
}
