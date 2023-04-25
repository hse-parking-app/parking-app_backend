package org.hse.parkings.model.jwt;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class RefreshJwtRequest {

    @NotNull(message = "Refresh token is required parameter")
    @NotBlank(message = "Refresh token cannot be blank")
    public String refreshToken;
}
