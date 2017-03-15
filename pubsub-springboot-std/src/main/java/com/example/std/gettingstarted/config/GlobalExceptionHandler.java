package com.example.std.gettingstarted.config;

import com.example.std.gettingstarted.exceptions.NoTopicFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    public GlobalExceptionHandler(){
        logger.info("== GlobalExceptionHandler contr called ==");
    }
    /**
     * 500 Other Generic Exception Handler
     *
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleGeneralInternal(final Exception ex, final WebRequest request) {
        logger.error("500 Exception General" + exceptionStackTraceToString(ex));
        return handleExceptionInternal(ex, "internal error happened", new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler({NoTopicFoundException.class})
    public ResponseEntity<Object> handleNoTopicFoundException(final NoTopicFoundException ex, final WebRequest request) {
        logger.error(exceptionStackTraceToString(ex));
        return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    private static String exceptionStackTraceToString(Exception e)
    {
        StackTraceElement[] traces =  e.getStackTrace();
        StringBuilder sb = new StringBuilder();
        for(StackTraceElement traceElement : traces){
            sb.append(traceElement.toString() + "\n");
        }
        return sb.toString();
    }


}

