package org.joelson.turf.dailyinc.api;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public final class ControllerIntegrationTestUtil {

    private ControllerIntegrationTestUtil() throws InstantiationException {
        throw new InstantiationException("Should not be instantiated");
    }

    public static <T> void verifyOKListContentResponse(
            MockMvc mvc, String getUrl, String rangeUnit, int size, Function<String, List<T>> asList,
            Function<T, Integer> getter) throws Exception {
        ResultActions actions = mvc.perform(MockMvcRequestBuilders.get(getUrl)
                .accept(MediaType.APPLICATION_JSON));
        MvcResult result = verifyInitial(HttpStatus.OK, rangeUnit, actions);
        MockHttpServletResponse response = result.getResponse();
        String content = response.getContentAsString();

        if (content.isEmpty()) {
            assertFalse(response.containsHeader(HttpHeaders.CONTENT_TYPE));
            assertEquals(RangeUtil.getUnsatisfiableContentRange(rangeUnit),
                    response.getHeader(HttpHeaders.CONTENT_RANGE));
        } else {
            verifySizeAndContentRange(rangeUnit, size, asList, getter, content, response);
        }
    }

    public static <T> void verifyPartialContentResponse(
            MockMvc mvc, String getUrl, String range, String rangeUnit, int size, Function<String, List<T>> asList,
            Function<T, Integer> getter) throws Exception {
        ResultActions actions = mvc.perform(MockMvcRequestBuilders.get(getUrl)
                .header(HttpHeaders.RANGE, range)
                .accept(MediaType.APPLICATION_JSON));
        MvcResult result = verifyInitial(HttpStatus.PARTIAL_CONTENT, rangeUnit, actions);
        MockHttpServletResponse response = result.getResponse();
        String content = response.getContentAsString() ;

        verifySizeAndContentRange(rangeUnit, size, asList, getter, content, response);
    }

    private static MvcResult verifyInitial(HttpStatus status, String rangeUnit, ResultActions actions) throws Exception {
        return actions
                .andExpect(header().string(HttpHeaders.ACCEPT_RANGES, RangeUtil.getAcceptRanges(rangeUnit)))
                .andExpect(status().is(status.value()))
                .andReturn();
    }

    private static <T> void verifySizeAndContentRange(
            String rangeUnit, int size, Function<String, List<T>> asList, Function<T, Integer> getter, String content,
            MockHttpServletResponse response) {
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getHeader(HttpHeaders.CONTENT_TYPE));
        assertNotEquals("", content);
        List<T> list = asList.apply(content);
        assertEquals(size, list.size());
        if (getter != null && !list.isEmpty()) {
            assertEquals(
                    RangeUtil.getContentRange(rangeUnit, getter.apply(list.getFirst()), getter.apply(list.getLast())),
                    response.getHeader(HttpHeaders.CONTENT_RANGE));
        } else {
            String contentRange = response.getHeader(HttpHeaders.CONTENT_RANGE);
            assertTrue(contentRange != null && !contentRange.isEmpty());
        }
    }

    public static void verifyStatusRangeNotSatisfiableResponse(
            MockMvc mvc, String getUrl, String range, String rangeUnit) throws Exception {
        String content = mvc.perform(MockMvcRequestBuilders.get(getUrl)
                        .header(HttpHeaders.RANGE, range)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(header().doesNotExist(HttpHeaders.CONTENT_TYPE))
                .andExpect(header().string(HttpHeaders.ACCEPT_RANGES, RangeUtil.getAcceptRanges(rangeUnit)))
                .andExpect(header().string(HttpHeaders.CONTENT_RANGE, RangeUtil.getUnsatisfiableContentRange(rangeUnit)))
                .andExpect(status().isRequestedRangeNotSatisfiable())
                .andReturn().getResponse().getContentAsString() ;

        assertEquals("", content);
    }
}
