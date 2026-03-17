package com.enzo.restaurant_api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantRequest {
    private String name;
    private String cnpj;
    private String phone;
    private String email;
    private String address;
    private Boolean active;
}
