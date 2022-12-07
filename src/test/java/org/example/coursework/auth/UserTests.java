package org.example.coursework.auth;

import org.example.coursework.AbstractTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

@SpringBootTest
@WithMockUser(username = "user")
public class UserTests extends AbstractTest {

}
