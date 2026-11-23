package com.example.invoicemanager.repository.implementation;

import com.example.invoicemanager.domain.Role;
import com.example.invoicemanager.domain.User;
import com.example.invoicemanager.exception.ApiException;
import com.example.invoicemanager.mapper.UserRowMapper;
import com.example.invoicemanager.repository.RoleRepository;
import com.example.invoicemanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Collection;
import java.util.*;
import java.util.UUID;

import static com.example.invoicemanager.enumeration.RoleType.*;
import static com.example.invoicemanager.enumeration.VerificationType.*;
import static com.example.invoicemanager.query.UserQuery.*;
import static java.util.Objects.requireNonNull;


@Repository
@RequiredArgsConstructor
@Slf4j
public class UserRepositoryImpl<T extends User> implements UserRepository<T> {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final RoleRepository<Role> roleRepository;
    private final BCryptPasswordEncoder encoder;


    @Override
    public T create(T user) {
        // Check the email is unique
        if (getEmailCount(user.getEmail().trim().toLowerCase()) > 0)
            throw new ApiException("Email already in use. Please use a different email and try again.");

        // Save new user
        try {
            KeyHolder holder = new GeneratedKeyHolder();
            SqlParameterSource parameters = getSqlParameterSource(user);
            jdbcTemplate.update(INSERT_USER_QUERY, parameters, holder);

            user.setId(requireNonNull(holder.getKey()).longValue());

            // Add role to the user
            roleRepository.addRoleToUser(user.getId(), ROLE_USER.name());

            // Send verification URL
            String verificationUrl = getVerificationUrl(UUID.randomUUID().toString(), ACCOUNT.getType());

            // Save URL in verification table
            jdbcTemplate.update(INSERT_ACCOUNT_VERIFICATION_URL_QUERY,
                    Map.of("userId", user.getId(), "url", verificationUrl));
            // Send email to user with verification URL
//            emailService.sendVerificationUrl(user.getFirstName(), user.getEmail(), verificationUrl, ACCOUNT);

            // Return the newly created user, re-read from the DB so defaulted/generated
            // columns (created_at, updated_at, non_locked, image_url, etc.) are accurate
            return (T) getUserById(user.getId());
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            log.error("An error occurred while creating user with email {}", user.getEmail(), e);
            throw new ApiException("An error occurred while creating user: " + e.getMessage() + ". Please try again.", e);
        }
    }

    @Override
    public Collection<T> list(int page, int pageSize) {
        return List.of();
    }

    @Override
    public T get(Long id) {
        return null;
    }

    @Override
    public T update(T data) {
        return null;
    }

    @Override
    public Boolean delete(Long id) {
        return null;
    }

    private Integer getEmailCount(String email) {
        return jdbcTemplate.queryForObject(COUNT_USER_EMAIL_QUERY, Map.of("email", email), Integer.class);
    }

    private User getUserById(Long id) {
        return jdbcTemplate.queryForObject(SELECT_USER_BY_ID_QUERY, Map.of("id", id), new UserRowMapper());
    }

    private SqlParameterSource getSqlParameterSource(T user) {
        return new MapSqlParameterSource()
                .addValue("firstName", user.getFirstName())
                .addValue("lastName", user.getLastName())
                .addValue("email", user.getEmail())
                .addValue("password", encoder.encode(user.getPassword()));
    }

    private String getVerificationUrl(String key, String type) {
        return ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/user/verify" + type + "/" + key)
                .toUriString();
    }
}
