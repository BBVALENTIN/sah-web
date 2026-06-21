package com.sah.dto.misc;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponseDTO {
    private String pageTitle = "Page not found";
    private String errorTitle = "Error 404 - Page not found";
    private String errorDescription = "We couldn't find the page you are looking for";
}
