package com.mosca.pneus.exception;

public abstract class ValidacaoException extends RuntimeException {

    protected ValidacaoException(String message) {
        super(message);
    }
}
