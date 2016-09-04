package com.ashbysoft.asyncdb.postgres;

import java.nio.channels.CompletionHandler;
import java.util.function.BiConsumer;

/**
 * So I can use more functional type stuff.
 * Created by mfash on 31/08/2016.
 */
public class DefaultCompletionHandler<V> implements CompletionHandler<V, Void> {
    private BiConsumer<V, Throwable> consumer = null;

    public static <X> DefaultCompletionHandler<X> handle(BiConsumer<X, Throwable> consumer) {
        DefaultCompletionHandler<X> handler= new DefaultCompletionHandler<>();
        handler.consumer = consumer;
        return handler;
    }

    @Override
    public void completed(V result, Void attachment) {
        consumer.accept(result, null);
    }

    @Override
    public void failed(Throwable exc, Void attachment) {
        consumer.accept(null, exc);
    }
}
