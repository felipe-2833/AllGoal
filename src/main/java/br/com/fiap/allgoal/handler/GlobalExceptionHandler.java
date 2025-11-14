package br.com.fiap.allgoal.handler;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleException(Exception e, Model model) {

        model.addAttribute("error", "Erro Interno do Servidor");
        model.addAttribute("message", "Ocorreu um problema inesperado. Por favor, tente novamente mais tarde. (Detalhe: " + e.getMessage() + ")");

        return "error";
    }
}