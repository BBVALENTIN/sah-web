package com.sah.controller.pages;

import com.sah.dto.misc.ErrorResponseDTO;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class GlobalExceptionHandler implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest req, Model model)
    {
        Object statusObj = req.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        int statusCode = statusObj != null ? Integer.parseInt(statusObj.toString()) : 500;

        ErrorResponseDTO err = buildError(statusCode);
        model.addAttribute("error", err);

        return "errors/errorpage";
    }

    private ErrorResponseDTO buildError(int statusCode) {
        return switch (statusCode) {
            case 404 -> ErrorResponseDTO.builder()
                    .pageTitle("Page not found")
                    .errorTitle("Error 404 - Page not found")
                    .errorDescription("We couldn't find the page you are looking for")
                    .build();
            case 403 -> ErrorResponseDTO.builder()
                    .pageTitle("Access denied")
                    .errorTitle("Error 403 - Forbidden")
                    .errorDescription("You don't have permission to access this page")
                    .build();
            case 500 -> ErrorResponseDTO.builder()
                    .pageTitle("Server error")
                    .errorTitle("Error 500 - Internal server error")
                    .errorDescription("Something went wrong on our end")
                    .build();
            default -> new ErrorResponseDTO(
                    "Error",
                    "Error " + statusCode,
                    "An unexpected error occurred"
            );
        };
    }
}
