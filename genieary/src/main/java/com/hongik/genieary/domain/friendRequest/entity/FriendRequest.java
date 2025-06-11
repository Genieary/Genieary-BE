package com.hongik.genieary.domain.friendRequest.entity;

import com.hongik.genieary.domain.common.BaseEntity;
import com.hongik.genieary.domain.enums.FriendStatus;
import com.hongik.genieary.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class FriendRequest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestId;

    @ManyToOne
    @JoinColumn(name = "requester_id")
    private User requesterId;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private User receiverId;

    @Enumerated(EnumType.STRING)
    private FriendStatus status;
}