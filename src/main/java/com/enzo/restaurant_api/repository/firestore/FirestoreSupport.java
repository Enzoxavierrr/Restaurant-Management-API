package com.enzo.restaurant_api.repository.firestore;

import com.google.api.core.ApiFuture;

final class FirestoreSupport {

    private FirestoreSupport() {
    }

    static <T> T get(ApiFuture<T> future) {
        try {
            return future.get();
        } catch (Exception ex) {
            throw new IllegalStateException("Falha ao acessar o Firestore.", ex);
        }
    }
}
