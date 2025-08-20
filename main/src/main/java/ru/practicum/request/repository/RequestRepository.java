package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.request.model.Request;
import ru.practicum.request.status.RequestStatus;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    Boolean existsByRequesterIdAndEventId(Long requesterId, Long eventId);

    Long countByEventIdAndStatus(Long eventId, RequestStatus status);

    List<Request> findAllByRequesterId(Long requesterId);

    List<Request> findAllByEventId(Long eventId);

    List<Request> findAllByIdIn(List<Long> ids);

    List<Request> findAllByEventIdAndStatus(Long eventId, RequestStatus status);
}
