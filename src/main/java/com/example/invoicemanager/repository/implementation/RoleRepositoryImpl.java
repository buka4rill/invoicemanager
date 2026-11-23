package com.example.invoicemanager.repository.implementation;

import com.example.invoicemanager.domain.Role;
import com.example.invoicemanager.exception.ApiException;
import com.example.invoicemanager.mapper.RoleRowMapper;
import com.example.invoicemanager.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.example.invoicemanager.enumeration.RoleType.ROLE_USER;
import static com.example.invoicemanager.enumeration.VerificationType.ACCOUNT;
import static com.example.invoicemanager.query.UserQuery.INSERT_ACCOUNT_VERIFICATION_URL_QUERY;
import static com.example.invoicemanager.query.UserQuery.INSERT_USER_QUERY;
import static java.util.Objects.requireNonNull;


@Repository
@RequiredArgsConstructor
@Slf4j
public class RoleRepositoryImpl implements RoleRepository<Role> {
    private static final String INSERT_ROLE_TO_USER_QUERY = "";
    private static final String SELECT_ROLE_BY_NAME_QUERY = "";
    private final NamedParameterJdbcTemplate jdbcTemplate;


    @Override
    public Role create(Role data) {
        return null;
    }

    @Override
    public Collection<Role> list(int page, int pageSize) {
        return List.of();
    }

    @Override
    public Role get(Long id) {
        return null;
    }

    @Override
    public Role update(Role data) {
        return null;
    }

    @Override
    public Boolean delete(Long id) {
        return null;
    }

    @Override
    public void addRoleToUser(Long userId, String roleName) {
        log.info("Adding role {} to user id: {}", roleName, userId);

        try {
            Role role =  jdbcTemplate.queryForObject(SELECT_ROLE_BY_NAME_QUERY, Map.of("roleName", roleName), new RoleRowMapper());
            jdbcTemplate.update(INSERT_ROLE_TO_USER_QUERY, Map.of("userId", userId, "roleId", role.getId()));

        } catch (EmptyResultDataAccessException e) {
            throw new ApiException("No role found by name: " + ROLE_USER.name());
        } catch (Exception e) {
            throw new ApiException("An error occurred while creating user: " + e.getMessage() + ". Please try again.");
        }
    }

    @Override
    public Role getRoleByUserId(Long userId) {
        return null;
    }

    @Override
    public Role getRoleByUserEmail(String email) {
        return null;
    }

    @Override
    public void updateUserRole(Long userId, String roleName) {

    }
}
