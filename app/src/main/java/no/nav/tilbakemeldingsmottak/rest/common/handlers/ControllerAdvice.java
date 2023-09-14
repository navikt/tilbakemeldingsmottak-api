package no.nav.tilbakemeldingsmottak.rest.common.handlers;

import jakarta.servlet.http.HttpServletRequest;
import no.nav.security.token.support.core.exceptions.JwtTokenMissingException;
import no.nav.security.token.support.spring.validation.interceptor.JwtTokenUnauthorizedException;
import no.nav.tilbakemeldingsmottak.exceptions.*;
import no.nav.tilbakemeldingsmottak.rest.common.domain.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Optional;

@RestControllerAdvice
public class ControllerAdvice {

    private static final Logger log = LoggerFactory.getLogger(ControllerAdvice.class);

    private HttpStatus getHttpStatus(Exception ex) {
        return Optional.ofNullable(AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class))
                .map(ResponseStatus::code)
                .orElse(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {JwtTokenMissingException.class, JwtTokenUnauthorizedException.class})
    public ResponseEntity<ErrorResponse> loginRequiredExceptionHandler(HttpServletRequest request, Exception ex) {
        HttpStatus status = getHttpStatus(ex);
        log.warn("Autentisering feilet ved kall til " + request.getRequestURI() + ": " + ex.getMessage(), ex);
        return ResponseEntity
                .status(status)
                .body(new ErrorResponse(status.getReasonPhrase(), ErrorCode.AUTH_ERROR.value));
    }

    @ExceptionHandler(value = {MissingServletRequestParameterException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErrorResponse> failedParametersHandler(HttpServletRequest request, Exception ex) {
        log.error("Feil i kall til " + request.getRequestURI() + ": " + ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(ex.getMessage(), ErrorCode.GENERAL_ERROR.value));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> technicalExceptionHandler(HttpServletRequest request, Exception ex) {
        HttpStatus status = getHttpStatus(ex);
        log.error("Feil i kall til " + request.getRequestURI() + ": " + ex.getMessage(), ex);
        return ResponseEntity
                .status(status)
                .body(new ErrorResponse(status.getReasonPhrase(), ErrorCode.GENERAL_ERROR.value));
    }

    // 200
    @ExceptionHandler(EksterntKallException.class)
    public ResponseEntity<ErrorResponse> eksterntKallExceptionHandler(HttpServletRequest request, EksterntKallException ex) {
        log.warn("Feil i kall til {}: ({}) {}", request.getRequestURI(), ex.getErrorCode().value, ex.getMessage(), ex);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ErrorResponse(ex.getMessage(), ex.getErrorCode().value));
    }

    // 204
    @ExceptionHandler(value = {NoContentException.class})
    public ResponseEntity<ErrorResponse> noContentResponse(HttpServletRequest request, NoContentException ex) {
        log.warn("Feil i kall til {}: ({}) {}", request.getRequestURI(), ex.getErrorCode().value, ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(new ErrorResponse(ex.getMessage(), ex.getErrorCode().value));
    }

    // 400
    @ExceptionHandler(value = {ClientErrorException.class})
    public ResponseEntity<ErrorResponse> clientErrorResponse(HttpServletRequest request, ClientErrorException ex) {
        log.warn("Feil i kall til {}: ({}) {}", request.getRequestURI(), ex.getErrorCode().value, ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(ex.getMessage(), ex.getErrorCode().value));
    }

    // 401
    @ExceptionHandler(value = {ClientErrorUnauthorizedException.class})
    public ResponseEntity<ErrorResponse> unauthorizedErrorResponse(HttpServletRequest request, ClientErrorUnauthorizedException ex) {
        log.warn("Feil i kall til {}: ({}) {}", request.getRequestURI(), ex.getErrorCode().value, ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(ex.getMessage(), ex.getErrorCode().value));
    }

    // 403
    @ExceptionHandler(value = {ClientErrorForbiddenException.class})
    public ResponseEntity<ErrorResponse> forbidddenErrorResponse(HttpServletRequest request, ClientErrorForbiddenException ex) {
        log.warn("Feil i kall til {}: ({}) {}", request.getRequestURI(), ex.getErrorCode().value, ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(ex.getMessage(), ex.getErrorCode().value));
    }

    // 404
    @ExceptionHandler(value = {ClientErrorNotFoundException.class})
    public ResponseEntity<ErrorResponse> notFoundErrorResponse(HttpServletRequest request, ClientErrorNotFoundException ex) {
        log.warn("Feil i kall til {}: ({}) {}", request.getRequestURI(), ex.getErrorCode().value, ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(ex.getMessage(), ex.getErrorCode().value));
    }

    // 500
    @ExceptionHandler(value = {ServerErrorException.class})
    public ResponseEntity<ErrorResponse> serverErrorResponse(HttpServletRequest request, ServerErrorException ex) {
        log.error("Feil i kall til {}: ({}) {}", request.getRequestURI(), ex.getErrorCode().value, ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(ex.getMessage(), ex.getErrorCode().value));
    }
}
