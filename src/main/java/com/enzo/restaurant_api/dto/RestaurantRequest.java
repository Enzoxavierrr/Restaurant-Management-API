package com.enzo.restaurant_api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantRequest {
    @NotBlank(message = "O campo 'name' (nome do restaurante) é obrigatório e não pode ser vazio.")
    private String name;
    @NotBlank(message = "O campo 'cnpj' é obrigatório e não pode ser vazio.")
    private String cnpj;
    private String phone;
    private String email;
    private String address;
    private Boolean active;
    @NotNull(message = "O campo 'ownerId' é obrigatório para registrar um restaurante.")
    private Long ownerId;
}
