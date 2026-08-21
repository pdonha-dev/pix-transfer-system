package com.pdonha.pix.domain.port;

import com.pdonha.pix.domain.model.PixKey;

import java.util.List;

public interface PixKeyRepository {
    PixKey findByKey(String key);
    List<PixKey> findAllByAccountId(java.util.UUID accountId);
    void save(PixKey pixKey);
}