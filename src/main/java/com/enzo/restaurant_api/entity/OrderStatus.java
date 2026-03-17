package com.enzo.restaurant_api.entity;

public enum OrderStatus {
    PENDING,      // Aguardando confirmação
    CONFIRMED,    // Confirmado
    PREPARING,    // Em preparo
    READY,        // Pronto para entrega/retirada
    DELIVERED,    // Entregue
    CANCELLED     // Cancelado
}
