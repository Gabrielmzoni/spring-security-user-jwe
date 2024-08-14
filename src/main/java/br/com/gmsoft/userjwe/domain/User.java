package br.com.gmsoft.userjwe.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "userr")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userr_seq")
    @SequenceGenerator(name = "userr_seq", sequenceName = "userr_SEQ", allocationSize = 1)
    private Long id;

    @Column(name = "name",length = 100)
    private String name;

    @Column(name = "email",length = 100)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "phone",length = 11)
    private String phone;

    @Column(name = "secret_Phrase",length = 100)
    private String secretPhrase;

    @Column(name="secret_Answer",length = 100)
    private String secretAnswer;

    }