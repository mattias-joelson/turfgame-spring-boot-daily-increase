package org.joelson.turf.dailyinc.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.joelson.turf.dailyinc.projection.UserIdAndName;
import org.joelson.turf.dailyinc.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class UsersControllerTest {

    @Mock
    UserService userService;

    @InjectMocks
    UsersController usersController;

    //@Autowired
    private MockMvc mvc;

    private static class UserWrapper implements UserIdAndName {

        private final Long id;
        private final String name;

        @JsonCreator
        private UserWrapper(@JsonProperty("id") Long id, @JsonProperty("name") String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public Long getId() {
            return id;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    private static List<UserIdAndName> createUsersList(int size) {
        List<UserIdAndName> users = new ArrayList<>(size);
        for (int i = 0; i < size; i += 1) {
            long id = 1001L + i;
            users.add(new UserWrapper(id, "User" +id));
        }
        return users;
    }

    @BeforeEach
    public void setup() {
        try (AutoCloseable c = MockitoAnnotations.openMocks(this)) {

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.mvc = MockMvcBuilders.standaloneSetup(usersController).build();
    }

    @Test
    public void getUsersTest() throws Exception {
        when(userService.getSortedUsers(UserIdAndName.class)).thenReturn(createUsersList(1000));

//        mvc.perform(MockMvcRequestBuilders.get("/api/users").accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk());
                //.andExpect(content().)
                //.andExpect(content().string(equalTo("Hello, world!")));

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/api/users")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
        ObjectMapper objectMapper = new ObjectMapper();
        String content = mvcResult.getResponse().getContentAsString();
        UserIdAndName[] idAndNames = objectMapper.readValue(content, UserWrapper[].class);
        assertEquals(1000, idAndNames.length);
        //assertEquals(1000, array.length());

        verify(userService).getSortedUsers(UserIdAndName.class);
    }
}
