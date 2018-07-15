package org.bslplugin;

import com.intellij.testFramework.ParsingTestCase;
import org.bslplugin.util.TestUtils;

public class BSLParserTest extends ParsingTestCase {

  public BSLParserTest() {
    super("parser", "bsl", new BSLParserDefinition());
  }

  public void testHello() {
    doTest(true);
  }

  @Override
  protected String getTestDataPath() {
    return TestUtils.BASE_TEST_DATA_PATH;
  }

}
