package com.fitness.gymsup.Config;

import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)throws IOException, ServletException {

        if (exception instanceof LockedException){
            request.setAttribute("errorMessage","계정이 정지되었습니다.");
        }else {
            request.setAttribute("errorMessage","아이디나 비밀번호가 다릅니다.");
        }

        RequestDispatcher dispatcher = request.getRequestDispatcher("/user_login_error");
        dispatcher.forward(request, response);
    }
}
