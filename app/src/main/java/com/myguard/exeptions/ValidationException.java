package com.myguard.exeptions;

/**
 * Created by user on 03.03.2018.
 */

public class ValidationException extends Exception {

    public final int messageId;

    public ValidationException(int messageId) {
        super();
        this.messageId = messageId;
    }

}
