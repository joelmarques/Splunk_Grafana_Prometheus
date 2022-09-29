package br.com.onoma.infrastructure.logging;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ErrorMessageLoggable implements Loggable {

    private ErrorMessageResponse response;
    private String ip;
    private String user;
    private String stacktrace;
}
