package org.citywatcher.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Column(name = "profile_image_path")
    private String profileImagePath;

    @OneToMany(mappedBy = "reporter")
    @JsonIgnore
    private List<Issue> reportedIssues = new ArrayList<>();

    @OneToMany(mappedBy = "assignedOfficial")
    @JsonIgnore
    private List<Issue> assignedIssues = new ArrayList<>();

    @ManyToMany(mappedBy = "volunteers")
    @JsonIgnore
    private List<Issue> volunteerIssues = new ArrayList<>();

    public User() {
        this.id = null;
        this.username = null;
        this.email = null;
        this.role = null;
    }

    public User(Long id, String username, String email, String password, UserRole role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
    }

    public User(String username, String email, String password, UserRole role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public List<Issue> getVolunteerIssues() {
        return volunteerIssues;
    }

    public void setVolunteerIssues(List<Issue> volunteerIssues) {
        this.volunteerIssues = volunteerIssues;
    }

    public String getProfileImagePath() {
        return profileImagePath;
    }

    public void setProfileImagePath(String profileImagePath) {
        this.profileImagePath = profileImagePath;
    }
}
