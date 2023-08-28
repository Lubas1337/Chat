package org.lubas.domain.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class RegisterRequest {
    private String username;
    private String password;
}