package br.com.onoma.infrastructure.logging;

import br.com.onoma.OnomaApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestControllerAdvice
public class RestCustomExceptionHandler {

        private final MessageSource messageSource;

        @Autowired
        public RestCustomExceptionHandler(MessageSource messageSource){
            this.messageSource = messageSource;
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorMessageResponse> handlerArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request){

            ErrorMessageResponse response = ErrorMessageResponse.builder()
                    .uuid(UUID.randomUUID().toString())
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.BAD_REQUEST.value())
                    .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                    .messages(getMessages(ex))
                    .path(request.getRequestURI())
                    .build();

            ErrorMessageLoggable errorMessageLoggable = ErrorMessageLoggable.builder()
                    .response(response)
                    .ip(request.getRemoteAddr())
                    .user(request.getRemoteUser())
                    .stacktrace(LogUtils.getStackTrace(ex))
                    .build();

            LogUtils.error(errorMessageLoggable);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

    private List<String> getMessages(MethodArgumentNotValidException ex) {

        return ex.getBindingResult().getAllErrors()
                .stream()
                .map(objectError -> messageSource.getMessage(objectError, OnomaApplication.LOCALE_PT_BR))
                .toList();
    }
}
