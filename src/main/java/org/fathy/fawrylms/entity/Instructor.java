package org.fathy.fawrylms.entity;


import jakarta.persistence.*;

import java.util.Locale;

@Entity
@Table(name ="instructors")
public class Instructor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false,unique = true)
    private String email;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    protected Instructor() {}

    public Instructor(String name, String email) {
        this.name = name;
        this.email = normalizeEmail(email);
    }

    public Instructor(String name, String email, User user) {
        this.name = name;
        this.email = normalizeEmail(email);
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = normalizeEmail(email);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
