package me.yushust.message.core.intercept;

import java.util.Collection;
import java.util.Optional;

import me.yushust.message.core.MessageProvider;
import me.yushust.message.core.placeholder.PlaceholderProvider;

/**
 * Manages the message interception, either with simple
 * {@link MessageInterceptor} or with {@link PlaceholderProvider}
 * @param <T> The property holder type
 */
public interface InterceptManager<T> {

    /**
     * Sets the message provider, if the message
     * is already provided, throws a {@link IllegalStateException}
     * This method marks the end of the stage of
     * {@link MessageProvider}'s construction, and the
     * start of a functional {@link MessageProvider}
     * @param messageProvider The new message provider
     */
    void setMessageProvider(MessageProvider<T> messageProvider);

    /**
     * @return Returns the message provider, if the
     * message provider isn't defined, throws a
     * {@link IllegalStateException}. Internal usage only.
     */
    MessageProvider<T> getMessageProvider();

    /**
     * Adds a message interceptor to the list
     * of message interceptors
     * @param interceptor The new message interceptor
     * @return The intercept manager, for a fluent api
     */
    InterceptManager<T> add(MessageInterceptor<T> interceptor);

    /**
     * Adds a placeholder replacer that corresponds to the
     * specified placeholders.
     * @param replacer The placeholder replacer
     * @return The intercept manager, for a fluent api
     */
    InterceptManager<T> add(PlaceholderProvider<T> replacer);

    /**
     * Finds a placeholder replacer for the specified placeholder
     * @param placeholder The placeholder
     * @return The placeholder replacer, wrapped with Optional
     */
    Optional<PlaceholderProvider<T>> findProvider(String placeholder);

    /**
     * Calls all message interceptors and placeholder
     * replacers for the specified property holder
     * and the provided text.
     * @param context The replacing context
     * @param text The text that will be modified
     * @param linkedPaths Internal parameter, indicates the
     *                    paths that depend of this conversion
     * @return The text already converted
     */
    String convert(InterceptContext<T> context, String text, Collection<String> linkedPaths);

}
