package com.example.bankcards.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthRequest {
    @NotBlank(message = "Имя пользователя не может быть пустым")
    private String username;
    @NotBlank(message = "пароль не может быть пустым")
    private String password;


    public @NotBlank(message = "пароль не может быть пустым") String getPassword() {
        return password;
    }

    public void setPassword(@NotBlank(message = "пароль не может быть пустым") String password) {
        this.password = password;
    }

    public @NotBlank(message = "Имя пользователя не может быть пустым") String getUsername() {
        return username;
    }

    public void setUsername(@NotBlank(message = "Имя пользователя не может быть пустым") String username) {
        this.username = username;
    }
}