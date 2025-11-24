package com.senai.plataforma_de_treinamento_saep.interface_ui.exception;

import com.senai.plataforma_de_treinamento_saep.domain.exception.EntidadeNaoEncontradaException;
import com.senai.plataforma_de_treinamento_saep.domain.exception.RegraDeNegocioException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ProblemDetail construtorDoProblema(HttpStatus statusCode, String titulo, String detalheErro, String caminho){
        ProblemDetail problema = ProblemDetail.forStatus(statusCode);
        problema.setTitle(titulo);
        problema.setDetail(detalheErro);
        problema.setInstance(URI.create(caminho));
        return problema;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail badRequest(MethodArgumentNotValidException ex, HttpServletRequest req) {
        ProblemDetail problema = construtorDoProblema(
                HttpStatus.BAD_REQUEST,
                "Erro de validação",
                "Um ou mais campos são inválidos",
                req.getRequestURI()
        );

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        problema.setProperty("errors", errors);
        return problema;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail constraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        ProblemDetail problem = construtorDoProblema(
                HttpStatus.BAD_REQUEST,
                "Erro de validação em parâmetros",
                "Um ou mais parâmetros são inválidos",
                request.getRequestURI()
        );

        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String campo = violation.getPropertyPath().toString();
            String mensagem = violation.getMessage();
            errors.put(campo, mensagem);
        });

        problem.setProperty("errors", errors);
        return problem;
    }

    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail handleAuthenticationException(AuthenticationException ex, HttpServletRequest request) {
        return construtorDoProblema(
                HttpStatus.UNAUTHORIZED,
                "Não autenticado",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        return construtorDoProblema(
                HttpStatus.FORBIDDEN,
                "Acesso negado",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ProblemDetail handleBadCredentials(BadCredentialsException ex, HttpServletRequest request) {
        return construtorDoProblema(
                HttpStatus.UNAUTHORIZED,
                "Credenciais inválidas",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ProblemDetail handleMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        return construtorDoProblema(
                HttpStatus.METHOD_NOT_ALLOWED,
                "Método não permitido",
                String.format("O método %s não é suportado para esta rota. Métodos suportados: %s",
                        ex.getMethod(),
                        String.join(", ", ex.getSupportedMethods() != null ? ex.getSupportedMethods() : new String[]{})),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(EntidadeNaoEncontradaException.class)
    public ProblemDetail handleUsuarioNaoEncontrado(EntidadeNaoEncontradaException ex, HttpServletRequest request) {
        return construtorDoProblema(
                HttpStatus.NOT_FOUND,
                "Recurso não encontrado",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(RegraDeNegocioException.class)
    public ProblemDetail handleRegraDeNegocio(RegraDeNegocioException ex, HttpServletRequest request) {
        return construtorDoProblema(
                HttpStatus.BAD_REQUEST, // 400
                "Violação de Regra de Negócio",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUncaught(Exception ex, HttpServletRequest request) {
        ex.printStackTrace();

        return construtorDoProblema(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro interno do servidor",
                "Ocorreu um erro inesperado. Entre em contato com o suporte.",
                request.getRequestURI()
        );
    }

    @ExceptionHandler(RuntimeException.class)
    public ProblemDetail handleRuntimeException(RuntimeException ex, HttpServletRequest request) {

        ex.printStackTrace(); // Logar o erro

        return construtorDoProblema(
                HttpStatus.BAD_REQUEST, // Ou BAD_REQUEST se você usa RuntimeException para erros do cliente
                "Erro de processamento",
                "Ocorreu um erro no processamento da requisição.",
                request.getRequestURI()
        );
    }
}