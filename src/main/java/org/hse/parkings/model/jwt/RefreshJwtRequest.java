package org.hse.parkings.model.jwt;

import lombok.Data;

@Data
public class RefreshJwtRequest {

    public String refreshToken;
}
