package com.enzo.restaurant_api.repository.firestore;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.firebase", name = "enabled", havingValue = "true", matchIfMissing = true)
public class FirestoreSequenceService {

    private final Firestore firestore;

    public long nextId(String sequenceName) {
        DocumentReference counterRef = firestore.collection("counters").document(sequenceName);

        return FirestoreSupport.get(firestore.runTransaction(transaction -> {
            DocumentSnapshot snapshot = transaction.get(counterRef).get();
            long nextValue = 1L;

            if (snapshot.exists()) {
                Long currentValue = snapshot.getLong("value");
                nextValue = currentValue == null ? 1L : currentValue + 1L;
            }

            transaction.set(counterRef, java.util.Map.of("value", nextValue));
            return nextValue;
        }));
    }
}
