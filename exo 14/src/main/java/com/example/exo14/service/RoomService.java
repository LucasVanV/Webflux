package com.example.exo14.service;

import com.example.exo14.model.Room;
import com.example.exo14.repository.RoomRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public Flux<Room> findAll() {
        return roomRepository.findAll();
    }

    public Mono<Room> save(Room room) {
        return roomRepository.save(room);
    }

    public Mono<Void> deleteById(Long id) {
        return roomRepository.deleteById(id);
    }
}