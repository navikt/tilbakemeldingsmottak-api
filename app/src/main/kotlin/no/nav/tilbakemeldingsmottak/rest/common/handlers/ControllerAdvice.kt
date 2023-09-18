package no.nav.tilbakemeldingsmottak.rest.common.handlers

import jakarta.servlet.http.HttpServletRequest
import no.nav.security.token.support.core.exceptions.JwtTokenMissingException
import no.nav.security.token.support.spring.validation.interceptor.JwtTokenUnauthorizedException
import no.nav.tilbakemeldingsmottak.exceptions.*
import no.nav.tilbakemeldingsmottak.rest.common.domain.ErrorResponse
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.AnnotationUtils
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

@RestControllerAdvice
class ControllerAdvice {
    private val log = LoggerFactory.getLogger(javaClass)

    private fun getHttpStatus(ex: Exception): HttpStatus {
        val responseStatus = AnnotationUtils.findAnnotation(ex.javaClass, ResponseStatus::class.java)
        return responseStatus?.code ?: HttpStatus.INTERNAL_SERVER_ERROR
    }

    @ExceptionHandler(value = [JwtTokenMissingException::class, JwtTokenUnauthorizedException::class])
    fun loginRequiredExceptionHandler(request: HttpServletRequest, ex: Exception): ResponseEntity<ErrorResponse> {
        val status = getHttpStatus(ex)
        log.warn("Autentisering feilet ved kall til " + request.requestURI + ": " + ex.message, ex)
        return ResponseEntity
            .status(status)
            .body(ErrorResponse(status.reasonPhrase, ErrorCode.AUTH_ERROR.value))
    }

    @ExceptionHandler(value = [MissingServletRequestParameterException::class, MethodArgumentTypeMismatchException::class])
    fun failedParametersHandler(request: HttpServletRequest, ex: Exception): ResponseEntity<ErrorResponse> {
        log.error("Feil i kall til " + request.requestURI + ": " + ex.message, ex)
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse(ex.message, ErrorCode.GENERAL_ERROR.value))
    }

    @ExceptionHandler(Exception::class)
    fun technicalExceptionHandler(request: HttpServletRequest, ex: Exception): ResponseEntity<ErrorResponse> {
        val status = getHttpStatus(ex)
        log.error("Feil i kall til " + request.requestURI + ": " + ex.message, ex)
        return ResponseEntity
            .status(status)
            .body(ErrorResponse(status.reasonPhrase, ErrorCode.GENERAL_ERROR.value))
    }

    // 200
    @ExceptionHandler(EksterntKallException::class)
    fun eksterntKallExceptionHandler(
        request: HttpServletRequest,
        ex: EksterntKallException
    ): ResponseEntity<ErrorResponse> {
        log.warn("Feil i kall til {}: ({}) {}", request.requestURI, ex.errorCode.value, ex.message, ex)
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ErrorResponse(ex.message, ex.errorCode.value))
    }

    // 204
    @ExceptionHandler(value = [NoContentException::class])
    fun noContentResponse(request: HttpServletRequest, ex: NoContentException): ResponseEntity<ErrorResponse> {
        log.warn("Feil i kall til {}: ({}) {}", request.requestURI, ex.errorCode.value, ex.message, ex)
        return ResponseEntity
            .status(HttpStatus.NO_CONTENT)
            .body(ErrorResponse(ex.message, ex.errorCode.value))
    }

    // 400
    @ExceptionHandler(value = [ClientErrorException::class])
    fun clientErrorResponse(request: HttpServletRequest, ex: ClientErrorException): ResponseEntity<ErrorResponse> {
        log.warn("Feil i kall til {}: ({}) {}", request.requestURI, ex.errorCode.value, ex.message, ex)
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse(ex.message, ex.errorCode.value))
    }

    // 401
    @ExceptionHandler(value = [ClientErrorUnauthorizedException::class])
    fun unauthorizedErrorResponse(
        request: HttpServletRequest,
        ex: ClientErrorUnauthorizedException
    ): ResponseEntity<ErrorResponse> {
        log.warn("Feil i kall til {}: ({}) {}", request.requestURI, ex.errorCode.value, ex.message, ex)
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(ErrorResponse(ex.message, ex.errorCode.value))
    }

    // 403
    @ExceptionHandler(value = [ClientErrorForbiddenException::class])
    fun forbidddenErrorResponse(
        request: HttpServletRequest,
        ex: ClientErrorForbiddenException
    ): ResponseEntity<ErrorResponse> {
        log.warn("Feil i kall til {}: ({}) {}", request.requestURI, ex.errorCode.value, ex.message, ex)
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(ErrorResponse(ex.message, ex.errorCode.value))
    }

    // 404
    @ExceptionHandler(value = [ClientErrorNotFoundException::class])
    fun notFoundErrorResponse(
        request: HttpServletRequest,
        ex: ClientErrorNotFoundException
    ): ResponseEntity<ErrorResponse> {
        log.warn("Feil i kall til {}: ({}) {}", request.requestURI, ex.errorCode.value, ex.message, ex)
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse(ex.message, ex.errorCode.value))
    }

    // 500
    @ExceptionHandler(value = [ServerErrorException::class])
    fun serverErrorResponse(request: HttpServletRequest, ex: ServerErrorException): ResponseEntity<ErrorResponse> {
        log.error("Feil i kall til {}: ({}) {}", request.requestURI, ex.errorCode.value, ex.message, ex)
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse(ex.message, ex.errorCode.value))
    }


}
