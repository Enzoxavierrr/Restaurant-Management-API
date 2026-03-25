package com.enzo.restaurant_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDTO {

    @NotBlank(message = "O campo 'email' é obrigatório e não pode ser vazio.")
    @Email(message = "O campo 'email' deve ser um endereço válido.")
    private String email;

    @NotBlank(message = "O campo 'password' é obrigatório e não pode ser vazio.")
    private String password;
}
