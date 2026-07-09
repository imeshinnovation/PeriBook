package com.peribook.like.domain;

public interface EventPublisher {
    void publish(LikeRegistrado evento);
}
