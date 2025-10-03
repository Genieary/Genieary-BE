package com.hongik.genieary.domain.friend.repository;

import com.hongik.genieary.domain.friend.entity.Friend;
import com.hongik.genieary.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FriendRepository extends JpaRepository<Friend, Long> {

    List<Friend> findAllByUser(User user);
    boolean existsByUserAndFriend(User user, User friend);
    void deleteByUserAndFriend(User user, User friend);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        delete from Friend f
        where (f.user.id = :uId and f.friend.id = :fId)
           or (f.user.id = :fId and f.friend.id = :uId)
    """)
    int deletePair(@Param("uId") Long uId, @Param("fId") Long fId);
}
