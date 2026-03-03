package com.aikero.otel.extension;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.TraceFlags;
import io.opentelemetry.api.trace.TraceState;
import io.opentelemetry.context.Context;
import io.opentelemetry.javaagent.bootstrap.http.HttpServerResponseMutator;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TraceIdResponseCustomizerTest {

    private final TraceIdResponseCustomizer customizer = new TraceIdResponseCustomizer();

    @Test
    void shouldAddTraceIdAndSpanIdHeaders() {
        String traceId = "0af7651916cd43dd8448eb211c80319c";
        String spanId = "b7ad6b7169203331";

        SpanContext spanContext = SpanContext.create(
                traceId, spanId, TraceFlags.getSampled(), TraceState.getDefault());
        Context context = Context.root().with(Span.wrap(spanContext));

        List<String[]> headers = new ArrayList<>();
        HttpServerResponseMutator<List<String[]>> mutator = (resp, name, value) -> resp.add(new String[]{name, value});

        customizer.customize(context, headers, mutator);

        assertEquals(2, headers.size());
        assertEquals("TraceId", headers.get(0)[0]);
        assertEquals(traceId, headers.get(0)[1]);
        assertEquals("SpanId", headers.get(1)[0]);
        assertEquals(spanId, headers.get(1)[1]);
    }

    @Test
    void shouldSkipWhenSpanContextIsInvalid() {
        Context context = Context.root();

        List<String[]> headers = new ArrayList<>();
        HttpServerResponseMutator<List<String[]>> mutator = (resp, name, value) -> resp.add(new String[]{name, value});

        customizer.customize(context, headers, mutator);

        assertTrue(headers.isEmpty());
    }
}
