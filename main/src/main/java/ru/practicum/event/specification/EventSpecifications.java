package ru.practicum.event.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.practicum.event.model.Event;
import ru.practicum.event.state.EventState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class EventSpecifications {
    public static Specification<Event> textContains(String text) {
        return (root, query, cb) -> {
            if (text == null || text.isBlank()) return null;
            String pattern = "%" + text.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("annotation")), pattern),
                    cb.like(cb.lower(root.get("description")), pattern)
            );
        };
    }

    public static Specification<Event> categoryIn(List<Long> categories) {
        return (root, query, cb) -> {
            if (categories == null || categories.isEmpty()) return null;
            return root.get("category").get("id").in(categories);
        };
    }

    public static Specification<Event> paidIs(Boolean paid) {
        return (root, query, cb) -> {
            if (paid == null) return null;
            return cb.equal(root.get("paid"), paid);
        };
    }

    public static Specification<Event> dateBetween(LocalDateTime start, LocalDateTime end) {
        LocalDateTime startDate = (start != null) ? start : LocalDateTime.now();

        return (root, query, cb) -> {
            if (end != null) {
                return cb.between(root.get("eventDate"), startDate, end);
            } else {
                return cb.greaterThanOrEqualTo(root.get("eventDate"), startDate);
            }
        };
    }

    public static Specification<Event> onlyAvailable(Boolean onlyAvailable) {
        return (root, query, cb) -> {
            if (onlyAvailable == null || !onlyAvailable) return null;
            return cb.greaterThan(root.get("participantLimit"), root.get("confirmedRequests"));
        };
    }

    public static Specification<Event> published() {
        return (root, query, cb) -> cb.equal(root.get("state"), EventState.PUBLISHED);
    }

    public static Specification<Event> userIn(List<Long> userIds) {
        return (root, query, cb) -> {
            if (userIds == null || userIds.isEmpty()) return null;
            return root.get("initiator").get("id").in(userIds);
        };
    }

    public static Specification<Event> stateIn(List<String> states) {
        return (root, query, cb) -> {
            if (states == null || states.isEmpty()) return null;
            List<EventState> eventStates = states.stream()
                    .map(EventState::valueOf)
                    .collect(Collectors.toList());
            return root.get("state").in(eventStates);
        };
    }
}
