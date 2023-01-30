package no.nav.tilbakemeldingsmottak.rest.common.handlers;

import com.fasterxml.jackson.core.JsonParseException;
import lombok.extern.slf4j.Slf4j;
import no.nav.security.token.support.core.exceptions.JwtTokenMissingException;
import no.nav.security.token.support.spring.validation.interceptor.JwtTokenUnauthorizedException;
import no.nav.tilbakemeldingsmottak.exceptions.*;
import no.nav.tilbakemeldingsmottak.exceptions.saf.SafHentDokumentFunctionalException;
import no.nav.tilbakemeldingsmottak.exceptions.saf.SafJournalpostIkkeFunnetFunctionalException;
import no.nav.tilbakemeldingsmottak.rest.common.domain.ErrorResponse;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@RestControllerAdvice
@Slf4j
public class ControllerAdvice {

    private HttpStatus getHttpStatus(Exception ex) {
        return Optional.ofNullable(AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class))
                .map(ResponseStatus::code)
                .orElse(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(EksterntKallException.class)
    public ResponseEntity<ErrorResponse> eksterntKallExceptionHandler(HttpServletRequest request, Exception ex) {
        HttpStatus status = HttpStatus.OK;
        log.error("Feil i kall til " + request.getRequestURI() + ": " + ex.getMessage(), ex);
        return ResponseEntity.status(status).body(ErrorResponse.builder()
                .message(ex.getMessage())
                .build());
    }

    @ExceptionHandler(PdfException.class)
    public ResponseEntity<ErrorResponse> pdfGenerationExceptionHandler(HttpServletRequest request, Exception ex) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        log.error("Feil ved generering av PDF " + ex.getMessage(), ex);
        return ResponseEntity.status(status).body(ErrorResponse.builder()
                .message(ex.getMessage())
                .build());
    }


    @ExceptionHandler(value={JwtTokenMissingException.class, JwtTokenUnauthorizedException.class})
    public ResponseEntity<ErrorResponse> loginRequiredExceptionHandler(HttpServletRequest request, Exception ex) {
        HttpStatus status = getHttpStatus(ex);
        log.warn("Autentisering feilet ved kall til " + request.getRequestURI() + ": " + ex.getMessage(), ex);
        return ResponseEntity.status(status).body(ErrorResponse.builder()
                .message(status.getReasonPhrase())
                .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> technicalExceptionHandler(HttpServletRequest request, Exception ex) {
        HttpStatus status = getHttpStatus(ex);
        log.error("Feil i kall til " + request.getRequestURI() + ": " + ex.getMessage(), ex);
        return ResponseEntity.status(status).body(ErrorResponse.builder()
                .message(status.getReasonPhrase())
                .build());
    }

    @ExceptionHandler(value = {JsonParseException.class, InvalidRequestException.class})
    public ResponseEntity<ErrorResponse> validationExceptionHandler(HttpServletRequest request, Exception ex) {
        log.warn("Feil i kall til " + request.getRequestURI() + ": " + ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse.builder()
                .message(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .build());
    }

    @ExceptionHandler(value = {SafHentDokumentFunctionalException.class, SafJournalpostIkkeFunnetFunctionalException.class})
    public ResponseEntity<ErrorResponse> safTilgangExceptionHandler(HttpServletRequest request, Exception ex) {
        HttpStatus status = getHttpStatus(ex);
        log.warn("Feil i kall til " + request.getRequestURI() + ": " + ex.getMessage(), ex);
        return ResponseEntity.status(status).body(ErrorResponse.builder()
                .message(status.getReasonPhrase())
                .build());
    }

    @ExceptionHandler(InvalidIdentException.class)
    public ResponseEntity<ErrorResponse> invalidIdentExceptionHandler(HttpServletRequest request, InvalidIdentException ex) {
        log.warn("Feil i kall til " + request.getRequestURI() + ": " + ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse.builder()
                .message(ex.getMessage())
                .build());
    }

    @ExceptionHandler(value = {OppgaveAlleredeFerdigstiltException.class})
    public ResponseEntity<ErrorResponse> klassifiseringExceptionHandler(HttpServletRequest request, Exception ex) {
        log.warn("Feil i kall til " + request.getRequestURI() + ": " + ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse.builder()
                .message(ex.getMessage())
                .build());
    }
}
