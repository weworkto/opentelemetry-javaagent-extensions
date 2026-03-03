package com.aikero.otel.extension;

import com.google.auto.service.AutoService;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.context.Context;
import io.opentelemetry.javaagent.bootstrap.http.HttpServerResponseCustomizer;
import io.opentelemetry.javaagent.bootstrap.http.HttpServerResponseMutator;

/**
 * OpenTelemetry Java Agent extension that automatically injects TraceId and SpanId
 * into HTTP response headers.
 *
 * <p>This enables frontend/client-side correlation with backend distributed traces,
 * making it easy to look up traces directly from HTTP responses.
 *
 * <p>Response headers added:
 * <ul>
 *   <li>{@code TraceId} - W3C trace ID (32 hex characters)</li>
 *   <li>{@code SpanId} - span ID (16 hex characters)</li>
 * </ul>
 *
 * <p>Usage: add this JAR to the agent extensions path:
 * <pre>-Dotel.javaagent.extensions=/path/to/opentelemetry-javaagent-extensions.jar</pre>
 */
@AutoService(HttpServerResponseCustomizer.class)
public class TraceIdResponseCustomizer implements HttpServerResponseCustomizer {

    @Override
    public <RESPONSE> void customize(
            Context context,
            RESPONSE response,
            HttpServerResponseMutator<RESPONSE> responseMutator) {
        SpanContext spanContext = Span.fromContext(context).getSpanContext();
        if (!spanContext.isValid()) {
            return;
        }
        responseMutator.appendHeader(response, "TraceId", spanContext.getTraceId());
        responseMutator.appendHeader(response, "SpanId", spanContext.getSpanId());
    }
}
