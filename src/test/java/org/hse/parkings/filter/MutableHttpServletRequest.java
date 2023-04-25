package org.hse.parkings.filter;

import org.springframework.util.LinkedCaseInsensitiveMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

// Helper class for adding 'Authorization' header to HttpServletRequest. Used for testing
class MutableHttpServletRequest extends HttpServletRequestWrapper {

    private final Map<String, String> customHeaders;

    public MutableHttpServletRequest(HttpServletRequest request) {
        super(request);
        this.customHeaders = new LinkedCaseInsensitiveMap<>();
    }

    public void putHeader(String name, String value) {
        this.customHeaders.put(name, value);
    }

    private HttpServletRequest getServletRequest() {
        return (HttpServletRequest) getRequest();
    }

    @Override
    public String getHeader(String name) {
        return Optional.ofNullable(customHeaders.get(name))
                .orElseGet(() -> getServletRequest().getHeader(name));
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        return Optional.ofNullable(customHeaders.get(name)).map(val -> Collections.enumeration(List.of(val)))
                .orElseGet(() -> getServletRequest().getHeaders(name));
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return Collections.enumeration(
                Stream.concat(
                                customHeaders.keySet().stream(),
                                StreamSupport.stream(
                                        Spliterators.spliteratorUnknownSize(
                                                getServletRequest().getHeaderNames().asIterator(),
                                                Spliterator.ORDERED), false))
                        .collect(Collectors.toSet()));
    }
}
