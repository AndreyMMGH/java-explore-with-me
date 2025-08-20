package ru.practicum.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.service.RequestService;
import ru.practicum.request.dto.ParticipationRequestDto;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users/{userId}/requests")
public class RequestPrivateController {
    private final RequestService requestService;

    @GetMapping
    public List<ParticipationRequestDto> getRequestsByUserId(@PathVariable(value = "userId") Long userId) {
        log.info("GET /users/{}/requests", userId);
        return requestService.getRequestsByUserId(userId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ParticipationRequestDto createRequest(@PathVariable(value = "userId") Long userId,
                                                 @RequestParam(name = "eventId") Long eventId) {
        log.info("Запрос на участие в событии с id= {}. POST /users/{}/requests", eventId, userId);
        return requestService.createRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable(value = "userId") Long userId,
                                                 @PathVariable(value = "requestId") Long requestId) {
        log.info("GET /users/{}/requests/{}/cancel", userId, requestId);
        return requestService.cancelRequest(userId, requestId);
    }
}
