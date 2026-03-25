package com.enzo.restaurant_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {
    @NotBlank(message = "O campo 'name' é obrigatório e não pode ser vazio.")
    private String name;

    @NotBlank(message = "O campo 'email' é obrigatório e não pode ser vazio.")
    @Email(message = "O campo 'email' deve ser um endereço de e-mail válido.")
    private String email;

    @NotBlank(message = "O campo 'password' é obrigatório e não pode ser vazio.")
    @Size(min = 8, message = "O campo 'password' deve ter pelo menos 8 caracteres.")
    private String password;
}
