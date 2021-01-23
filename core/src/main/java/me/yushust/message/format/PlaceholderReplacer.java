package me.yushust.message.format;

import me.yushust.message.track.TrackingContext;

public class PlaceholderReplacer {

  private final PlaceholderValueProvider valueProvider;
  private final String startDelimiter;
  private final String endDelimiter;

  public PlaceholderReplacer(
      PlaceholderValueProvider valueProvider,
      String startDelimiter,
      String endDelimiter
  ) {
    this.valueProvider = valueProvider;
    this.startDelimiter = startDelimiter;
    this.endDelimiter = endDelimiter;
  }

  /**
   * Determines if the given {@code text} can contain a
   * placeholder by checking its length
   */
  public boolean hasMinimumLength(String text) {
    return text.length() >= (3 + startDelimiter.length() + endDelimiter.length());
  }

  public String setPlaceholders(
      TrackingContext context,
      String text,
      Object[] jitEntities
  ) {
    if (
        startDelimiter.isEmpty()
            || endDelimiter.isEmpty()
            || !hasMinimumLength(text)
    ) {
      return text;
    }

    // builder that holds the result of the replaced text
    StringBuilder result = new StringBuilder();

    // builder that temporally contains the reading identifier
    StringBuilder identifier = new StringBuilder();

    // builder that temporally contains the reading placeholder parameters
    StringBuilder placeholder = new StringBuilder();

    // cursor that indicates the progress while reading start or end delimiter
    int cursor = 0;

    // boolean that determines if we are reading a placeholder or not
    boolean readingPlaceholder = false;

    // boolean that determines if the identifier was already read
    boolean identified = false;

    // the current delimiter being compared, it's initially the start delimiter,
    // then it's the end delimiter
    String delimiter = startDelimiter;

    // start reading character by character the text
    for (int i = 0; i < text.length(); i++) {

      char current = text.charAt(i);
      char expectedDelimiter = delimiter.charAt(cursor);

      // we are completing the delimiter? (start/end)
      if (current == expectedDelimiter) {
        // then, update the cursor and check for the
        // next character
        cursor++;
      } else {
        // no, then, if we were reading a placeholder
        // add the characters to the identifier or placeholder
        if (readingPlaceholder) {
          // if the placeholder was already identified, then
          // append the character to the placeholder parameters
          if (identified) {
            placeholder.append(current);
          } else {
            // if not, check if the current character is the
            // identifier-parameter separator, if true, then
            // set 'identifier' as true and ignore this char
            if (current == '_') {
              identified = true;
            } else {
              // if it's not a '_', append the character to the
              // identifier builder
              identifier.append(current);
            }
          }
        } else {
          // if not, just append the character to the result
          result.append(current);
        }

        // back cursor to zero
        cursor = 0;
      }

      // we end the delimiter read
      if (cursor >= delimiter.length()) {
        // we were reading a placeholder, now, it's finished
        // so get the value for this placeholder and re-set
        // everything to its initial state to start reading
        // another placeholder
        if (readingPlaceholder) {

          // boolean that determines if the identifier or the placeholder are invalids
          boolean invalid = identifier.length() == 0 || placeholder.length() == 0;

          // the value for this placeholder
          String value;

          String identifierStr = identifier.toString();
          String placeholderStr = placeholder.toString();

          if (invalid || (value = valueProvider.getValue(
              context, identifierStr, placeholderStr, jitEntities
          )) == null) {
            result.append(startDelimiter);
            result.append(identifierStr);
            if (identified) {
              result.append("_");
              result.append(placeholderStr);
              result.append(endDelimiter);
            }
          } else {
            result.append(value);
          }

          placeholder.setLength(0);
          identifier.setLength(0);

          identified = false;
          readingPlaceholder = false;
          delimiter = startDelimiter;
        } else {
          delimiter = endDelimiter;
          readingPlaceholder = true;
        }
        cursor = 0;
      }
    }

    //
    if (identifier.length() > 0) {
      result.append(startDelimiter);
      result.append(identifier);
    }

    if (placeholder.length() > 0) {
      if (identified) {
        result.append("_");
      }
      result.append(placeholder);
    }

    return result.toString();
  }

}