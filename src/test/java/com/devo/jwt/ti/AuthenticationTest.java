package com.devo.jwt.ti;

import com.devo.jwt.dto.LoginForm;
import com.devo.jwt.models.AppRole;
import com.devo.jwt.models.AppUser;
import com.devo.jwt.services.impl.AccountServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static io.swagger.v3.core.util.Json.mapper;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AuthenticationTest {

    @Autowired
    MockMvc mockMvc;

   @SpyBean
   AccountServiceImpl accountService;

   @Autowired
   BCryptPasswordEncoder passwordEncoder;

   @Test
   void optionsWithoutAuthenticationTest() throws Exception {
       this.mockMvc.perform(options("/tasks"))
                       .andExpect(status().isOk())
               .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*"))
               .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, notNullValue()))
               .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, notNullValue()));
   }

    @Test
    void shouldDenyWhenNotAuthenticated() throws Exception {

        this.mockMvc
                .perform(
                        post("/tasks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"taskName\" : \"TX2\"}")
                                .with(SecurityMockMvcRequestPostProcessors.user("coucou").roles("ADMIN", "USER"))
                                .with(csrf())
                )
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void shouldAllowWhenAuthenticated() throws Exception {
        // Get jwt token with a good user
        String bearer = loginOk(true);

        // call POST tasks
        MvcResult taskCreationResult = this.mockMvc
                .perform(
                        post("/tasks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", bearer)
                                .content("{\"taskName\" : \"TX2\"}")
                )
                .andExpect(status().isCreated()).andReturn();

        // check that we have the location in the header
        String taskLocation = extractHeader(taskCreationResult, "Location");
        assertThat(taskLocation, startsWith("/tasks/"));
    }

    @Test
    void userRoleCantCreateTask() throws Exception {
        // Get jwt token with a good user
        String bearer = loginOk(false);

        // call POST tasks
        this.mockMvc
                .perform(
                        post("/tasks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", bearer)
                                .content("{\"taskName\" : \"TX2\"}")
                )
                .andExpect(status().isForbidden());
    }

    private static String extractHeader(MvcResult mvcResult, String headerName){
        assertThat(mvcResult, notNullValue());

        return mvcResult.getResponse().getHeader(headerName);
    }

    private AppUser buildUser(String userName, String password, Set<AppRole> usesRoles){
        return AppUser.builder()
                .userName(userName)
                .password(this.passwordEncoder.encode(password))
                .firstName("fName")
                .lastName("lName")
                .birth(LocalDate.of(2021, 12, 15))
                .roles(usesRoles)
                .build();
    }

    /**
     * call /login and mock user search
     *
     * @return Bearer
     * @throws Exception
     */
    private String loginOk(boolean isAdmin) throws Exception {

        // userName, passWord, roles
        String userName, password;
        Set<AppRole> roles;
        if(isAdmin){
            userName = "admin";
            password = "admin";
            roles = Set.of(AppRole.builder().roleName("ADMIN").build(),
                    AppRole.builder().roleName("USER").build());
        }else {
            userName = "user";
            password = "user";
            roles = Set.of(AppRole.builder().roleName("USER").build());
        }

        // mock user search
        AppUser userToReturn = buildUser(userName, password, roles);
        Mockito.doReturn(Optional.of(userToReturn)).when(this.accountService).findUserByUserName(ArgumentMatchers.eq(userToReturn.getUserName()));

        MvcResult mvcResult = this.mockMvc
                .perform(post("/login")
                        .content(mapper().writeValueAsString(loginForm(userName, password)))).andExpect(status().isOk()).andReturn();

        String bearer = extractHeader(mvcResult, "Authorization");

        assertThat(bearer, startsWith("Bearer"));

        return bearer;
    }

    private static LoginForm loginForm(String userName, String password){
        return LoginForm.builder()
                .userName(userName)
                .password(password)
                .build();
    }
}
