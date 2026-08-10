package com.techlab.store.service;

import org.hashids.Hashids;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
public class HashidService {

    private final Hashids hashids;

    public HashidService(Hashids hashids) {
        this.hashids = hashids;
    }

    // Codifica el ID Long a Hash String (ej: 123 -> "X9b2aQ8z")
    @Named("encodeId")
    public String encode(Long id) {
        if (id == null) return null;
        return hashids.encode(id);
    }

    // Decodifica el Hash String a ID Long (ej: "X9b2aQ8z" -> 123)
    @Named("decodeId")
    public Long decode(String hash) {
        if (hash == null || hash.isBlank()) return null;
        long[] res = hashids.decode(hash);
        if (res.length == 0) {
            throw new IllegalArgumentException("El hash proporcionado es inválido.");
        }
        return res[0];
    }
}