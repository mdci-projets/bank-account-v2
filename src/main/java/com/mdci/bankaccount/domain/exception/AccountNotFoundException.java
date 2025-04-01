package com.mdci.bankaccount.domain.exception;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(String id) {
        super("Aucun compte trouvé pour l'identifiant : " + id);
    }
}