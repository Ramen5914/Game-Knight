package com.r4men.game_knight.client.auth;

import com.microsoft.credentialstorage.SecretStore;
import com.microsoft.credentialstorage.StorageProvider;
import com.microsoft.credentialstorage.model.StoredToken;
import com.microsoft.credentialstorage.model.StoredTokenType;

public final class CredentialStorage {
    private static final String SERVICE = "com.r4men.game_knight:lichess-token";

    private final SecretStore<StoredToken> store;

    public CredentialStorage() {
        this.store = StorageProvider.getTokenStorage(
                true,
                StorageProvider.SecureOption.REQUIRED
        );

        if (store == null) {
            throw new IllegalStateException(
                    "No secure OS credential storage is available."
            );
        }
    }

    public void saveLichessToken(String token) {
        StoredToken storedToken = new StoredToken(
                token.toCharArray(),
                StoredTokenType.ACCESS
        );

        try {
            store.add(SERVICE, storedToken);
        } finally {
            storedToken.clear();
        }
    }

    public String loadLichessToken() {
        StoredToken storedToken = store.get(SERVICE);

        if (storedToken == null) {
            return null;
        }

        try {
            return new String(storedToken.getValue());
        } finally {
            storedToken.clear();
        }
    }

    public boolean hasLichessToken() {
        StoredToken storedToken = store.get(SERVICE);

        if (storedToken == null) {
            return false;
        }

        try {
            return true;
        } finally {
            storedToken.clear();
        }
    }

    public void deleteLichessToken() {
        store.delete(SERVICE);
    }
}