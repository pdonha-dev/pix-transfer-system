package com.pdonha.pix.domain.exception;

/**
 * Thrown when attempting to block an account that is already blocked.
 */
public class AccountBlockedException extends PixException {
    private final String accountIdHash;

    public AccountBlockedException(String message, String accountId) {
        super(message);
        this.accountIdHash = maskAccountId(accountId);
    }

    public AccountBlockedException(String message, String accountId, Throwable cause) {
        super(message, cause);
        this.accountIdHash = maskAccountId(accountId);
    }

    private static String maskAccountId(String accountId) {
        if (accountId != null && accountId.length() >= 4) {
            return "***" + accountId.substring(accountId.length() - 4);
        }
        return "***";
    }

    public String getAccountIdHash() {
        return accountIdHash;
    }
}
