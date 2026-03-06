package com.example.exo14.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.example.exo14.model.Room;

@Repository
public interface RoomRepository extends ReactiveCrudRepository<Room, Long> {
}