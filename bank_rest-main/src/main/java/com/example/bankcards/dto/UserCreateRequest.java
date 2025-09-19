package com.example.bankcards.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserCreateRequest {

    @NotBlank(message = "Имя пользователя не может быть пустым")
    @Size(min = 3, max = 50, message = "Имя пользователя должно содержать от 3 до 50 символов")
    private String username;

    @NotBlank(message = "Пароль не может быть пустым")
    @Size(min = 6, message = "Пароль должен содержать не менее 6 символов")
    private String password;

    public @NotBlank(message = "Пароль не может быть пустым")
    @Size(min = 6, message = "Пароль должен содержать не менее 6 символов")
    String getPassword() {
        return password;
    }

    public void setPassword(@NotBlank(message = "Пароль не может быть пустым")
                            @Size(min = 6, message = "Пароль должен содержать не менее 6 символов")
                            String password) {
        this.password = password;
    }

    public @NotBlank(message = "Имя пользователя не может быть пустым")
    @Size(min = 3, max = 50, message = "Имя пользователя должно содержать от 3 до 50 символов")
    String getUsername() {
        return username;
    }

    public void setUsername(@NotBlank(message = "Имя пользователя не может быть пустым")
                            @Size(min = 3, max = 50, message = "Имя пользователя должно содержать от 3 до 50 символов")
                            String username) {
        this.username = username;
    }
}
