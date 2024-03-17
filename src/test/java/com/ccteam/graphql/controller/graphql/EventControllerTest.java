/*
 * Copyright (c) 2024 by Yann39
 *
 * This file is part of CCTeam GraphQL application.
 *
 * CCTeam GraphQL is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * CCTeam GraphQL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with CCTeam GraphQL. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.ccteam.graphql.controller.graphql;

import com.ccteam.graphql.entities.Event;
import com.ccteam.graphql.entities.EventMember;
import com.ccteam.graphql.entities.Member;
import com.ccteam.graphql.entities.Track;
import com.ccteam.graphql.service.EventService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.graphql.test.tester.GraphQlTester;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@GraphQlTest(EventController.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(OutputCaptureExtension.class)
class EventControllerTest {

    @Autowired
    private GraphQlTester graphQlTester;

    @MockBean
    private EventService eventService;

    private Event eventBresse;

    private Event eventMagnyCours;

    @BeforeAll
    void setup() {

        final Track trackBresse = new Track();
        trackBresse.setId(1L);
        trackBresse.setName("Bresse");
        trackBresse.setLapRecord(84330);
        trackBresse.setDistance(3000);
        trackBresse.setWebsite("https://www.circuitdebresse.com");
        trackBresse.setLatitude(BigDecimal.valueOf(46.551756882687776));
        trackBresse.setLongitude(BigDecimal.valueOf(5.3285273408879394));

        final Track trackMagnyCours = new Track();
        trackMagnyCours.setId(2L);
        trackMagnyCours.setName("Magny-cours");
        trackMagnyCours.setLapRecord(96950);
        trackMagnyCours.setDistance(4411);
        trackMagnyCours.setWebsite("https://www.circuitmagnycours.com/");
        trackMagnyCours.setLatitude(BigDecimal.valueOf(46.86390367017831));
        trackMagnyCours.setLongitude(BigDecimal.valueOf(3.162750730649732));

        final Member memberBobAdmin = new Member();
        memberBobAdmin.setId(1L);
        memberBobAdmin.setFirstName("Bob");
        memberBobAdmin.setLastName("Douglas");
        memberBobAdmin.setEmail("Bob.Douglas@example.com");
        memberBobAdmin.setAdmin(true);

        final Member memberJohn = new Member();
        memberJohn.setId(2L);
        memberJohn.setFirstName("John");
        memberJohn.setLastName("Doe");
        memberJohn.setEmail("John.Doe@example.com");
        memberJohn.setAdmin(false);

        final Member memberJane = new Member();
        memberJane.setId(3L);
        memberJane.setFirstName("Jane");
        memberJane.setLastName("Doe");
        memberJane.setEmail("Jane.Doe@example.com");
        memberJane.setAdmin(false);

        eventBresse = new Event();
        eventBresse.setId(1L);
        eventBresse.setTitle("Event Bresse");
        eventBresse.setDescription("Event Bresse description");
        eventBresse.setStartDate(LocalDateTime.of(2018, 7, 13, 8, 0, 0));
        eventBresse.setEndDate(LocalDateTime.of(2018, 7, 13, 16, 30, 0));
        eventBresse.setOrganizer("Organizer A");
        eventBresse.setPrice(BigDecimal.valueOf(180));
        eventBresse.setCreatedOn(LocalDateTime.of(2018, 4, 11, 20, 41, 5));
        eventBresse.setModifiedOn(LocalDateTime.of(2018, 4, 11, 20, 46, 17));
        eventBresse.setTrack(trackBresse);
        eventBresse.setCreatedBy(memberBobAdmin);
        eventBresse.setModifiedBy(memberJohn);

        eventMagnyCours = new Event();
        eventMagnyCours.setId(2L);
        eventMagnyCours.setTitle("Event Magny-Cours");
        eventMagnyCours.setDescription("Event Magny-Cours description");
        eventMagnyCours.setStartDate(LocalDateTime.of(2019, 5, 24, 9, 0, 0));
        eventMagnyCours.setEndDate(LocalDateTime.of(2019, 5, 24, 18, 0, 0));
        eventMagnyCours.setOrganizer("Organizer B");
        eventMagnyCours.setPrice(BigDecimal.valueOf(240));
        eventMagnyCours.setCreatedOn(LocalDateTime.of(2019, 1, 29, 22, 15, 14));
        eventMagnyCours.setModifiedOn(null);
        eventMagnyCours.setTrack(trackMagnyCours);
        eventMagnyCours.setCreatedBy(memberBobAdmin);
        eventMagnyCours.setModifiedBy(null);

        final EventMember eventBresseMemberJane = new EventMember();
        eventBresseMemberJane.setId(1L);
        eventBresseMemberJane.setEvent(eventBresse);
        eventBresseMemberJane.setMember(memberJane);
        eventBresseMemberJane.setCreatedOn(LocalDateTime.of(2018, 3, 10, 9, 35, 41));

        eventBresse.setParticipants(Set.of(eventBresseMemberJane));

        final EventMember eventMagnyCoursMemberJohn = new EventMember();
        eventMagnyCoursMemberJohn.setId(1L);
        eventMagnyCoursMemberJohn.setEvent(eventMagnyCours);
        eventMagnyCoursMemberJohn.setMember(memberJohn);
        eventMagnyCoursMemberJohn.setCreatedOn(LocalDateTime.of(2019, 2, 9, 19, 33, 31));

        final EventMember eventMagnyCoursMemberJane = new EventMember();
        eventMagnyCoursMemberJane.setId(1L);
        eventMagnyCoursMemberJane.setEvent(eventMagnyCours);
        eventMagnyCoursMemberJane.setMember(memberJohn);
        eventMagnyCoursMemberJane.setCreatedOn(LocalDateTime.of(2019, 2, 18, 21, 3, 10));

        eventMagnyCours.setParticipants(Set.of(eventMagnyCoursMemberJohn, eventMagnyCoursMemberJane));
    }

    @Test
    void should_get_all_events(CapturedOutput output) {
        Mockito.when(eventService.getAllEvents()).thenReturn(List.of(eventBresse, eventMagnyCours));

        graphQlTester.documentName("allEvents")
                .execute()
                .path("getAllEvents")
                .matchesJsonStrictly("""
                        [
                          {
                            "id": "1",
                            "title": "Event Bresse",
                            "description": "Event Bresse description",
                            "startDate": "2018-07-13 08:00:00",
                            "endDate": "2018-07-13 16:30:00",
                            "track": {
                              "name": "Bresse",
                              "distance": 3000,
                              "lapRecord": 84330,
                              "website": "https://www.circuitdebresse.com",
                              "latitude": 46.551756882687776,
                              "longitude": 5.3285273408879394
                            },
                            "participants": [
                              {
                                "member": {
                                  "firstName": "Jane",
                                  "lastName": "Doe",
                                  "email": "Jane.Doe@example.com"
                                }
                              }
                            ],
                            "organizer": "Organizer A",
                            "price": 180,
                            "createdOn": "2018-04-11 20:41:05",
                            "createdBy": {
                              "id": "1",
                              "firstName": "Bob",
                              "lastName": "Douglas"
                            },
                            "modifiedOn": "2018-04-11 20:46:17",
                            "modifiedBy": {
                              "id": "2",
                              "firstName": "John",
                              "lastName": "Doe"
                            }
                          },
                          {
                            "id": "2",
                            "title": "Event Magny-Cours",
                            "description": "Event Magny-Cours description",
                            "startDate": "2019-05-24 09:00:00",
                            "endDate": "2019-05-24 18:00:00",
                            "track": {
                              "name": "Magny-cours",
                              "distance": 4411,
                              "lapRecord": 96950,
                              "website": "https://www.circuitmagnycours.com/",
                              "latitude": 46.86390367017831,
                              "longitude": 3.162750730649732
                            },
                            "participants": [
                              {
                                "member": {
                                  "firstName": "John",
                                  "lastName": "Doe",
                                  "email": "John.Doe@example.com"
                                }
                              },
                              {
                                "member": {
                                  "firstName": "John",
                                  "lastName": "Doe",
                                  "email": "John.Doe@example.com"
                                }
                              }
                            ],
                            "organizer": "Organizer B",
                            "price": 240,
                            "createdOn": "2019-01-29 22:15:14",
                            "createdBy": {
                              "id": "1",
                              "firstName": "Bob",
                              "lastName": "Douglas"
                            },
                            "modifiedOn": null,
                            "modifiedBy": null
                          }
                        ]
                        """
                );

        verify(eventService, times(1)).getAllEvents();
        assertThat(output).contains("Received call to getAllEvents");
    }

    @Test
    void should_get_events_by_year(CapturedOutput output) {

        Mockito.when(eventService.getEventsByYear(2018)).thenReturn(List.of(eventBresse));

        graphQlTester.documentName("eventsByYear")
                .variable("year", 2018)
                .execute()
                .path("getEventsByYear")
                .matchesJsonStrictly("""
                        [
                          {
                            "id": "1",
                            "title": "Event Bresse",
                            "description": "Event Bresse description",
                            "startDate": "2018-07-13 08:00:00",
                            "endDate": "2018-07-13 16:30:00",
                            "track": {
                              "name": "Bresse",
                              "distance": 3000,
                              "lapRecord": 84330,
                              "website": "https://www.circuitdebresse.com",
                              "latitude": 46.551756882687776,
                              "longitude": 5.3285273408879394
                            },
                            "participants": [
                              {
                                "member": {
                                  "firstName": "Jane",
                                  "lastName": "Doe",
                                  "email": "Jane.Doe@example.com"
                                }
                              }
                            ],
                            "organizer": "Organizer A",
                            "price": 180,
                            "createdOn": "2018-04-11 20:41:05",
                            "createdBy": {
                              "id": "1",
                              "firstName": "Bob",
                              "lastName": "Douglas"
                            },
                            "modifiedOn": "2018-04-11 20:46:17",
                            "modifiedBy": {
                              "id": "2",
                              "firstName": "John",
                              "lastName": "Doe"
                            }
                          }
                        ]
                        """
                );

        verify(eventService, times(1)).getEventsByYear(2018);
        assertThat(output).contains("Received call to getEventsByYear with parameter year = 2018");
    }

    @Test
    void should_get_empty_result_if_no_events_in_year(CapturedOutput output) {

        Mockito.when(eventService.getEventsByYear(2018)).thenReturn(List.of(eventBresse));

        graphQlTester.documentName("eventsByYear")
                .variable("year", 2020)
                .execute()
                .path("getEventsByYear")
                .matchesJsonStrictly("[]"
                );

        verify(eventService, times(1)).getEventsByYear(2020);
        assertThat(output).contains("Received call to getEventsByYear with parameter year = 2020");
    }

    @Test
    void should_get_events_by_month_and_year(CapturedOutput output) {

        Mockito.when(eventService.getEventsByMonthAndYear(7, 2018)).thenReturn(List.of(eventBresse));

        graphQlTester.documentName("eventsByMonthAndYear")
                .variable("month", 7)
                .variable("year", 2018)
                .execute()
                .path("getEventsByMonthAndYear")
                .matchesJsonStrictly("""
                        [
                          {
                            "id": "1",
                            "title": "Event Bresse",
                            "description": "Event Bresse description",
                            "startDate": "2018-07-13 08:00:00",
                            "endDate": "2018-07-13 16:30:00",
                            "track": {
                              "name": "Bresse",
                              "distance": 3000,
                              "lapRecord": 84330,
                              "website": "https://www.circuitdebresse.com",
                              "latitude": 46.551756882687776,
                              "longitude": 5.3285273408879394
                            },
                            "participants": [
                              {
                                "member": {
                                  "firstName": "Jane",
                                  "lastName": "Doe",
                                  "email": "Jane.Doe@example.com"
                                }
                              }
                            ],
                            "organizer": "Organizer A",
                            "price": 180,
                            "createdOn": "2018-04-11 20:41:05",
                            "createdBy": {
                              "id": "1",
                              "firstName": "Bob",
                              "lastName": "Douglas"
                            },
                            "modifiedOn": "2018-04-11 20:46:17",
                            "modifiedBy": {
                              "id": "2",
                              "firstName": "John",
                              "lastName": "Doe"
                            }
                          }
                        ]
                        """
                );

        verify(eventService, times(1)).getEventsByMonthAndYear(7, 2018);
        assertThat(output).contains("Received call to getEventsByMonthAndYear with parameters month = 7, year = 2018");
    }

    @Test
    void should_get_empty_result_if_no_events_in_month(CapturedOutput output) {

        Mockito.when(eventService.getEventsByMonthAndYear(7, 2018)).thenReturn(List.of(eventBresse));

        graphQlTester.documentName("eventsByMonthAndYear")
                .variable("month", 6)
                .variable("year", 2018)
                .execute()
                .path("getEventsByMonthAndYear")
                .matchesJsonStrictly("[]"
                );

        verify(eventService, times(1)).getEventsByMonthAndYear(6, 2018);
        assertThat(output).contains("Received call to getEventsByMonthAndYear with parameters month = 6, year = 2018");
    }

    @Test
    void should_get_events_by_day_and_month_and_year(CapturedOutput output) {

        Mockito.when(eventService.getEventsByDayAndMonthAndYear(13, 7, 2018)).thenReturn(List.of(eventBresse));

        graphQlTester.documentName("eventsByDayAndMonthAndYear")
                .variable("day", 13)
                .variable("month", 7)
                .variable("year", 2018)
                .execute()
                .path("getEventsByDayAndMonthAndYear")
                .matchesJsonStrictly("""
                        [
                          {
                            "id": "1",
                            "title": "Event Bresse",
                            "description": "Event Bresse description",
                            "startDate": "2018-07-13 08:00:00",
                            "endDate": "2018-07-13 16:30:00",
                            "track": {
                              "name": "Bresse",
                              "distance": 3000,
                              "lapRecord": 84330,
                              "website": "https://www.circuitdebresse.com",
                              "latitude": 46.551756882687776,
                              "longitude": 5.3285273408879394
                            },
                            "participants": [
                              {
                                "member": {
                                  "firstName": "Jane",
                                  "lastName": "Doe",
                                  "email": "Jane.Doe@example.com"
                                }
                              }
                            ],
                            "organizer": "Organizer A",
                            "price": 180,
                            "createdOn": "2018-04-11 20:41:05",
                            "createdBy": {
                              "id": "1",
                              "firstName": "Bob",
                              "lastName": "Douglas"
                            },
                            "modifiedOn": "2018-04-11 20:46:17",
                            "modifiedBy": {
                              "id": "2",
                              "firstName": "John",
                              "lastName": "Doe"
                            }
                          }
                        ]
                        """
                );

        verify(eventService, times(1)).getEventsByDayAndMonthAndYear(13, 7, 2018);
        assertThat(output).contains("Received call to getEventsByDayAndMonthAndYear with parameters day = 13, month = 7, year = 2018");
    }

    @Test
    void should_get_empty_result_if_no_events_in_day(CapturedOutput output) {

        Mockito.when(eventService.getEventsByDayAndMonthAndYear(13, 7, 2018)).thenReturn(List.of(eventBresse));

        graphQlTester.documentName("eventsByDayAndMonthAndYear")
                .variable("day", 14)
                .variable("month", 7)
                .variable("year", 2018)
                .execute()
                .path("getEventsByDayAndMonthAndYear")
                .matchesJsonStrictly("[]"
                );

        verify(eventService, times(1)).getEventsByDayAndMonthAndYear(14, 7, 2018);
        assertThat(output).contains("Received call to getEventsByDayAndMonthAndYear with parameters day = 14, month = 7, year = 2018");
    }

    @Test
    void should_get_events_by_id(CapturedOutput output) {

        Mockito.when(eventService.getEventById(2L)).thenReturn(eventMagnyCours);

        graphQlTester.documentName("eventById")
                .variable("id", 2L)
                .execute()
                .path("getEventById")
                .matchesJsonStrictly("""
                        {
                          "id": "2",
                          "title": "Event Magny-Cours",
                          "description": "Event Magny-Cours description",
                          "startDate": "2019-05-24 09:00:00",
                          "endDate": "2019-05-24 18:00:00",
                          "track": {
                            "name": "Magny-cours",
                            "distance": 4411,
                            "lapRecord": 96950,
                            "website": "https://www.circuitmagnycours.com/",
                            "latitude": 46.86390367017831,
                            "longitude": 3.162750730649732
                          },
                          "participants": [
                            {
                              "member": {
                                "firstName": "John",
                                "lastName": "Doe",
                                "email": "John.Doe@example.com"
                              }
                            },
                            {
                              "member": {
                                "firstName": "John",
                                "lastName": "Doe",
                                "email": "John.Doe@example.com"
                              }
                            }
                          ],
                          "organizer": "Organizer B",
                          "price": 240,
                          "createdOn": "2019-01-29 22:15:14",
                          "createdBy": {
                            "id": "1",
                            "firstName": "Bob",
                            "lastName": "Douglas"
                          },
                          "modifiedOn": null,
                          "modifiedBy": null
                        }
                        """
                );

        verify(eventService, times(1)).getEventById(2L);
        assertThat(output).contains("Received call to getEventById with parameter ID = 2");
    }

    @Test
    void should_get_null_result_if_no_event_with_id(CapturedOutput output) {

        Mockito.when(eventService.getEventById(2L)).thenReturn(eventMagnyCours);

        graphQlTester.documentName("eventById")
                .variable("id", 8L)
                .execute()
                .path("getEventById")
                .valueIsNull();

        verify(eventService, times(1)).getEventById(8L);
        assertThat(output).contains("Received call to getEventById with parameter ID = 8");
    }

    @Test
    void should_get_events_by_title(CapturedOutput output) {

        Mockito.when(eventService.getEventsByTitle("Bres")).thenReturn(List.of(eventBresse));

        graphQlTester.documentName("eventsByTitle")
                .variable("title", "Bres")
                .execute()
                .path("getEventsByTitle")
                .matchesJsonStrictly("""
                        [
                          {
                            "id": "1",
                            "title": "Event Bresse",
                            "description": "Event Bresse description",
                            "startDate": "2018-07-13 08:00:00",
                            "endDate": "2018-07-13 16:30:00",
                            "track": {
                              "name": "Bresse",
                              "distance": 3000,
                              "lapRecord": 84330,
                              "website": "https://www.circuitdebresse.com",
                              "latitude": 46.551756882687776,
                              "longitude": 5.3285273408879394
                            },
                            "participants": [
                              {
                                "member": {
                                  "firstName": "Jane",
                                  "lastName": "Doe",
                                  "email": "Jane.Doe@example.com"
                                }
                              }
                            ],
                            "organizer": "Organizer A",
                            "price": 180,
                            "createdOn": "2018-04-11 20:41:05",
                            "createdBy": {
                              "id": "1",
                              "firstName": "Bob",
                              "lastName": "Douglas"
                            },
                            "modifiedOn": "2018-04-11 20:46:17",
                            "modifiedBy": {
                              "id": "2",
                              "firstName": "John",
                              "lastName": "Doe"
                            }
                          }
                        ]
                        """
                );

        verify(eventService, times(1)).getEventsByTitle("Bres");
        assertThat(output).contains("Received call to getEventsByTitle with parameter title = Bres");
    }

    @Test
    void should_get_empty_result_if_no_events_with_title(CapturedOutput output) {

        Mockito.when(eventService.getEventsByTitle("Bres")).thenReturn(List.of(eventBresse));

        graphQlTester.documentName("eventsByTitle")
                .variable("title", "blabla")
                .execute()
                .path("getEventsByTitle")
                .matchesJsonStrictly("[]"
                );

        verify(eventService, times(1)).getEventsByTitle("blabla");
        assertThat(output).contains("Received call to getEventsByTitle with parameter title = blabla");
    }
}