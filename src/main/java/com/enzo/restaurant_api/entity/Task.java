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
public class Task {

    private Long id;
    private String title;
    private String description;
    private Boolean completed;

    public void complete() {
        this.completed = true;
    }

    public void uncomplete() {
        this.completed = false;
    }
}
