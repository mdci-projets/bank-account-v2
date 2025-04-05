package com.mdci.bankaccount.application.port.out;

public interface PdfGenerator<T> {
    byte[] generate(T dto);
}