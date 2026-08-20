package com.pdonha.pix.domain.port;

import com.pdonha.pix.domain.model.PixKey;

public interface PixKeyRepository {
    PixKey findByKey(String key);
    void save(PixKey pixKey);
}