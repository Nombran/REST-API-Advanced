package com.epam.esm.user;

import com.epam.esm.service.Service;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
//@RequiredArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "login", unique = true)
    private String login;
    @Column(name = "password")
    private String password;
    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;
    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY,mappedBy = "creator")
    private List<Service> createdServices;
    @ManyToMany(cascade = {CascadeType.PERSIST,CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(name = "user_desired_services",
            joinColumns = {@JoinColumn(name = "user_id", nullable = false)},
            inverseJoinColumns = {@JoinColumn(name = "service_id", nullable = false)})
    private List<Service> desiredServices;
    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY,mappedBy = "developer")
    private List<Service> takenServices;
}
