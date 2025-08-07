package com.dashboard.parser;

import com.dashboard.exception.InvalidFormatException;
import com.dashboard.exception.ValidationException;
import com.dashboard.model.Users;
import com.dashboard.model.UserRole;
import com.dashboard.repository.UsersRepository;
import org.apache.commons.lang3.StringUtils;
public class UsersCsvParser implements CsvEntityParser<Users>{

    private final UsersRepository usersRepository;

    public UsersCsvParser(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public Users parse(String[] row) {
        var expectedColumns = 3;
        if (row.length < expectedColumns) {
            throw new InvalidFormatException(String.format("Invalid user column number. Actual: %d, expected: %d", row.length, expectedColumns));
        }

        var username = row[0].trim();
        if (StringUtils.isBlank(username)) {
            throw new ValidationException("Username cannot be blank");
        }

        String password = row[1].trim();
        if (StringUtils.isBlank(password)) {
            throw new ValidationException("Password cannot be blank");
        }

        String role = row[2].trim();
        if (StringUtils.isBlank(role)) {
            throw new ValidationException("Role cannot be blank");
        }

        UserRole userRole;
        try {
            userRole = UserRole.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid role: " + role);
        }

        return Users.builder()
            .username(username)
            .password(password)
            .role(userRole)
            .deleted(false)
            .build();
    }
}
