package com.chachatte.graphql.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

/**
 * @author yann39
 * @since may 2019
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "participant")
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column
    private String email;

    @Column
    private String phone;

    @Column
    private int numberOfGuests;

    @Column
    private boolean validated;

    @Column
    private boolean aperitif;

    @Column
    private boolean byCar;

    @Column
    private boolean accessGranted;

    @Column
    private int numberOfAccessGranted;

    @Column
    private boolean acceptedConditions;

    @Column
    private Date registrationDate;

    @Column
    private Date ticketSentDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

}
