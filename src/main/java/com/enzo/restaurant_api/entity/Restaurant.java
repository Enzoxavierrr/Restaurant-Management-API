package com.enzo.restaurant_api.entity;

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
    private Boolean active;
    private Long ownerId;

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }
    
}
