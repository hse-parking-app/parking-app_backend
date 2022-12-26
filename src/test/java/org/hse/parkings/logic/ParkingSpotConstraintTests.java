package org.hse.parkings.logic;

import org.hse.parkings.AbstractTest;
import org.hse.parkings.model.ParkingSpot;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@WithMockUser(username = "admin", roles = {"ADMIN"})
public class ParkingSpotConstraintTests extends AbstractTest {
    //    final ParkingSpot parkingSpot2 = new ParkingSpot(-2, true);
    final ParkingSpot parkingSpot2 = ParkingSpot.builder()
            .parkingNumber("")
            .isFree(true).build();

    @Test
    public void whenParkingSpotNameBlank_thenBadRequest() throws Exception {
        String parkingSpot2String = jackson.writeValueAsString(parkingSpot2);
        this.mockMvc.perform(post("/parkingSpots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(parkingSpot2String))
                .andExpect(status().isBadRequest());
    }
}