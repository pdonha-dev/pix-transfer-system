package com.pdonha.pix.domain.model;

import java.util.UUID;

public enum PixKeyType {
    CPF {
        @Override
        public boolean isValid(String value) {
            return value != null && value.matches("^\\d{11}$");
        }
    },
    EMAIL {
        @Override
        public boolean isValid(String value) {
            return value != null && value.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
        }
    },
    PHONE {
        @Override
        public boolean isValid(String value) {
            return value != null && value.matches("^\\+[1-9]\\d{9,14}$");
        }
    },
    RANDOM {
        @Override
        public boolean isValid(String value) {
            if (value == null) {
                return false;
            }
            try {
                UUID.fromString(value);
                return true;
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
    };

    public abstract boolean isValid(String value);
}

