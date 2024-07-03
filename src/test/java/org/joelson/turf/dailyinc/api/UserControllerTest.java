package org.joelson.turf.dailyinc.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.joelson.turf.dailyinc.projection.UserIdAndName;
import org.joelson.turf.dailyinc.projection.UserIdAndNameImpl;
import org.joelson.turf.dailyinc.util.ListTestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static org.joelson.turf.dailyinc.api.ControllerIntegrationTestUtil.verifyOKListContentResponse;
import static org.joelson.turf.dailyinc.api.ControllerIntegrationTestUtil.verifyPartialContentResponse;
import static org.joelson.turf.dailyinc.api.ControllerIntegrationTestUtil.verifyStatusRangeNotSatisfiableResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    private static final long INTEGER_MAX_VALUE = Integer.MAX_VALUE;

    private static final String API_USERS = "/api/users";
    private static final String USERS_RANGE_UNIT = "users";

    private static final Function<String, List<UserIdAndNameImpl>>
            USER_ID_AND_NAME_JSON_AS_LIST = content -> asList(content, UserIdAndNameImpl[].class);
    private static final Function<UserIdAndNameImpl, Integer>
            USER_ID_AND_NAME_INTEGER_GETTER = RangeRequestUtil.integerGetter(UserIdAndNameImpl::getId);
    public static final List<Object> INVALID_USER_LIST = List.of(new UserIdAndNameImpl(404L, "Hello, world!"));

    @Mock
    UserAPIService userAPIService;
    @InjectMocks
    UserController usersController;
    private MockMvc mvc;

    private static List<UserIdAndName> createUserList(long minId, long maxId) {
        return ListTestUtil.createList(minId, maxId, 3L, UserControllerTest::createUser);
    }

    private static List<UserIdAndName> createUserList(int size) {
        return ListTestUtil.createListOfSize(1001L, 10L, size, UserControllerTest::createUser);
    }

    private static UserIdAndName createUser(Long id) {
        return new UserIdAndNameImpl(id, "User" + id);
    }

    private static <T> List<T> asList(String content, Class<T[]> arrayClass) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            T[] array = objectMapper.readValue(content, arrayClass);
            return Arrays.asList(array);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        this.mvc = MockMvcBuilders.standaloneSetup(usersController).build();
    }

    @Test
    public void givenNoRangeNorUsers_whenGetUsers_thenNoneAreReturned_statusOK() throws Exception {
        when(userAPIService.getSortedUsersBetween(anyLong(), anyLong(), any())).thenReturn(INVALID_USER_LIST);
        when(userAPIService.getSortedUsersBetween(0L, INTEGER_MAX_VALUE, UserIdAndName.class)).thenReturn(List.of());

        verifyOKListContentResponse(mvc, API_USERS, USERS_RANGE_UNIT, 0, USER_ID_AND_NAME_JSON_AS_LIST,
                USER_ID_AND_NAME_INTEGER_GETTER);

        verify(userAPIService).getSortedUsersBetween(0L, INTEGER_MAX_VALUE, UserIdAndName.class);
    }

    @Test
    public void givenNoRange_whenGetUsers_thenSomeAreReturned_statusOK() throws Exception {
        final int SIZE = 100;
        when(userAPIService.getSortedUsersBetween(anyLong(), anyLong(), any())).thenReturn(INVALID_USER_LIST);
        when(userAPIService.getSortedUsersBetween(0L, INTEGER_MAX_VALUE, UserIdAndName.class))
                .thenReturn(createUserList(SIZE));

        verifyOKListContentResponse(mvc, API_USERS, USERS_RANGE_UNIT, SIZE, USER_ID_AND_NAME_JSON_AS_LIST,
                USER_ID_AND_NAME_INTEGER_GETTER);

        verify(userAPIService).getSortedUsersBetween(0L, INTEGER_MAX_VALUE, UserIdAndName.class);
    }

    @Test
    public void givenRangeAndNoUsers_whenGetUsers_thenNoneReturned_statusRequestRangeNotSatisfiable() throws Exception {
        final long MIN_ID = 100;
        final long MAX_ID = 150;
        when(userAPIService.getSortedUsersBetween(anyLong(), anyLong(), any())).thenReturn(INVALID_USER_LIST);
        when(userAPIService.getSortedUsersBetween(MIN_ID, MAX_ID, UserIdAndName.class)).thenReturn(List.of());
        String range = RangeUtil.getRange(USERS_RANGE_UNIT, (int) MIN_ID, (int) MAX_ID);
        verifyStatusRangeNotSatisfiableResponse(mvc, API_USERS, range, USERS_RANGE_UNIT);

        verify(userAPIService).getSortedUsersBetween(MIN_ID, MAX_ID, UserIdAndName.class);
    }

    @Test
    public void givenRangeAndUsers_whenGetUsers_thenNoneReturned_statusPartialContentResponse() throws Exception {
        final long MIN_ID = 100;
        final long MAX_ID = 150;
        List<UserIdAndName> USER_LIST = createUserList(MIN_ID, MAX_ID);
        when(userAPIService.getSortedUsersBetween(anyLong(), anyLong(), any())).thenReturn(INVALID_USER_LIST);
        when(userAPIService.getSortedUsersBetween(MIN_ID, MAX_ID, UserIdAndName.class)).thenReturn(USER_LIST);
        String range = RangeUtil.getRange(USERS_RANGE_UNIT, (int) MIN_ID, (int) MAX_ID);
        verifyPartialContentResponse(mvc, API_USERS, range, USERS_RANGE_UNIT, USER_LIST.size(),
                USER_ID_AND_NAME_JSON_AS_LIST, USER_ID_AND_NAME_INTEGER_GETTER);

        verify(userAPIService).getSortedUsersBetween(MIN_ID, MAX_ID, UserIdAndName.class);
    }

    @Test
    public void givenSuffixRangeAndNoUsers_whenGetUsers_thenNoneReturned_statusRequestRangeNotSatisfiable() throws Exception {
        final int LAST = 50;
        when(userAPIService.getLastSortedUsers(anyInt(), any())).thenReturn(INVALID_USER_LIST);
        when(userAPIService.getLastSortedUsers(LAST, UserIdAndName.class)).thenReturn(List.of());
        String range = RangeUtil.getRangeSuffix(USERS_RANGE_UNIT, LAST);
        verifyStatusRangeNotSatisfiableResponse(mvc, API_USERS, range, USERS_RANGE_UNIT);

        verify(userAPIService).getLastSortedUsers(LAST, UserIdAndName.class);
    }

    @Test
    public void givenSuffixRangeAndUsers_whenGetUsers_thenNoneReturned_statusPartialContentResponse() throws Exception {
        final int LAST = 50;
        List<UserIdAndName> USER_LIST = createUserList(LAST / 3);
        when(userAPIService.getLastSortedUsers(anyInt(), any())).thenReturn(INVALID_USER_LIST);
        when(userAPIService.getLastSortedUsers(LAST, UserIdAndName.class)).thenReturn(USER_LIST);
        String range = RangeUtil.getRangeSuffix(USERS_RANGE_UNIT, LAST);
        verifyPartialContentResponse(mvc, API_USERS, range, USERS_RANGE_UNIT, USER_LIST.size(),
                USER_ID_AND_NAME_JSON_AS_LIST, USER_ID_AND_NAME_INTEGER_GETTER);

        verify(userAPIService).getLastSortedUsers(LAST, UserIdAndName.class);
    }

    @Test
    public void givenZonesRange_whenGetUsers_then_statusRequestRangeNotSatisfiable() throws Exception {
        verifyStatusRangeNotSatisfiableResponse(mvc, API_USERS, "zones=0-9", USERS_RANGE_UNIT);
        verifyStatusRangeNotSatisfiableResponse(mvc, API_USERS, "users=0--9", USERS_RANGE_UNIT);
        verifyStatusRangeNotSatisfiableResponse(mvc, API_USERS, "users=-0--9", USERS_RANGE_UNIT);
        verifyStatusRangeNotSatisfiableResponse(mvc, API_USERS, "users=--0", USERS_RANGE_UNIT);
    }
}
