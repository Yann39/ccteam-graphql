package com.chachatte.graphql.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * @author yann39
 * @since may 2019
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "event")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String titleFr;

    @Column
    private String titleEn;

    @Column
    private Date startDate;

    @Column
    private Date endDate;

    @Column
    private String location;

    @Column
    private String status;

    @Column
    private String email;

    @Column(columnDefinition = "longtext")
    private String confirmationEmailFr;

    @Column(columnDefinition = "longtext")
    private String confirmationEmailEn;

    @Column
    private String imageFileName;

    @Lob
    @Column(length = 100000)
    private byte[] image;

    @Column
    private String urlInformationEn;

    @Column
    private String urlInformationFr;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "event")
    private List<Participant> participants;

}
