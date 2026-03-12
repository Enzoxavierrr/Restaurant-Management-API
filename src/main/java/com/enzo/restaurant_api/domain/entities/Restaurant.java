package com.enzo.restaurant_api.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Restaurant {

    private Long id;
    private String name;
    private String cnpj;
    private String phone;
    private String email;
    private String address;
    private boolean active;

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }
}