package com.chachatte.graphql.repository;

import com.chachatte.graphql.entities.Member;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * @author yann39
 * @since sept 2020
 */
public interface MemberRepository extends PagingAndSortingRepository<Member, Long> {

    List<Member> findByFirstNameLikeOrLastNameLikeOrEmailLike(@Param("text") String firstName, @Param("text") String lastName, @Param("text") String email);

    @Query("select m from Member m inner join LikedNews ln on m.id = ln.member.id where ln.news.id = ?1")
    List<Member> findByNewsId(@Param("newsId") long newsId);

    Member findByEmailAndPassword(@Param("email") String email, @Param("password") String password);

    Optional<Member> findByEmail(@Param("email") String email);

    boolean existsMemberByEmail(String email);
}
