package com.pdonha.pix.domain.exception;

public class InvalidCpfException extends PixException {
    private String cpfHash;

    public InvalidCpfException(String message) {
        super(message);
    }

    public InvalidCpfException(String message, String cpf) {
        super(message);
        this.cpfHash = hashCpf(cpf);
    }

    private static String hashCpf(String cpf) {
        if (cpf != null && cpf.length() >= 4) {
            return "***-" + cpf.substring(cpf.length() - 4);
        }
        return "***-****";
    }

    public String getCpfHash() {
        return cpfHash;
    }
}