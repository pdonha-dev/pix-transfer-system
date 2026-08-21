package com.pdonha.pix.domain.exception;

public class InvalidMoneyException extends PixException {
    public InvalidMoneyException(String message) {
        super(message);
    }

    public InvalidMoneyException(String message, Throwable cause) {
        super(message, cause);
    }
}
