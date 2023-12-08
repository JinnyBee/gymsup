package com.fitness.gymsup.Config.oauth;

import com.fitness.gymsup.Service.OAuthUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@Log4j2
public class OAuthLoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    private final OAuthUserService oAuthUserService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception)throws IOException, ServletException{
        log.error("LOGIN FAILED : {}",exception.getMessage());



        response.sendRedirect("/user_login_error");

        /*if(exception instanceof LockedException){
            request.setAttribute("errorMessage","계정이 정지되었습니다.");
        }

        RequestDispatcher dispatcher = request.getRequestDispatcher("/user_login_error");
        dispatcher.forward(request,response);*/
    }
}