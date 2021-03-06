package me.yushust.message.source.properties;

import me.yushust.message.MessageProvider;
import me.yushust.message.config.ConfigurationHandle;
import me.yushust.message.impl.MessageProviderImpl;
import me.yushust.message.source.MessageSource;
import me.yushust.message.source.MessageSourceDecorator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ResourceMessageGetTest {

  @Test
  public void test() {
    MessageSource source = MessageSourceDecorator
      .decorate(new PropertiesResourceSource("test_%lang%.properties"))
      .addFallbackLanguage("en")
      .get();
    MessageProvider provider = new MessageProviderImpl(source, new ConfigurationHandle());

    assertEquals("nefasto", provider.get(null, "test1"));
    assertEquals("terrible", provider.get(null, "test2"));
    assertEquals("oremos", provider.get(null, "test3"));
  }

}
