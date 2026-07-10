package com.project.Elevate.ConnectionService.services;

import com.project.Elevate.ConnectionService.entity.Person;
import com.project.Elevate.ConnectionService.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConnectionsService {

    private final PersonRepository personRepository;

    public List<Person> getFirstDegreeConnectionsOfUser(Long userId) {
        log.info("Getting first degree connections of user with ID: {}", userId);

        return personRepository.getFirstDegreeConncetions(userId);
    }
}
