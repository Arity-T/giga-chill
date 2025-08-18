package ru.gigachill.service.validator;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.gigachill.exception.NotFoundException;
import ru.gigachill.service.UserService;

@Component
@RequiredArgsConstructor
public class UserServiceValidator {
    private final UserService userService;

    public void checkAreExisted(List<UUID> allUsersIds) {
        if (!userService.allUsersExistByIds(allUsersIds)) {
            throw new NotFoundException("The list contains a user that is not in the database");
        }
    }

    public void checkIsExisted(UUID userId) {
        if (!userService.userExistsById(userId)) {
            throw new NotFoundException("User with id: " + userId + " not found");
        }
    }
}
