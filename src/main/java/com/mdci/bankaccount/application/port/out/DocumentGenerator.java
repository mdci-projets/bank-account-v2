package com.mdci.bankaccount.application.port.out;

public interface DocumentGenerator<T> {
    byte[] generate(T dto);
}