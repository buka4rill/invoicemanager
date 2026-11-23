package com.example.invoicemanager.query;

public class UserQuery {
    public static final String INSERT_USER_QUERY =
            "INSERT INTO users (first_name, last_name, email, password) " +
            "VALUES (:firstName, :lastName, :email, :password)";
    public static final String COUNT_USER_EMAIL_QUERY =
            "SELECT COUNT(*) FROM users WHERE email = :email";
    public static final String SELECT_USER_BY_ID_QUERY =
            "SELECT * FROM users WHERE id = :id";
    public static final String INSERT_ACCOUNT_VERIFICATION_URL_QUERY =
            "INSERT INTO account_verifications (user_id, url) " +
                    "VALUES (:userId, :url)";
}
