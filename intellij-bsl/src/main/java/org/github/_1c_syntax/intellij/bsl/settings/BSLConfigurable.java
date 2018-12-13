package org.github._1c_syntax.intellij.bsl.settings;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.UnnamedConfigurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;

public class BSLConfigurable implements UnnamedConfigurable {

  private BSLConfigurabeGUI form;

  @Nls(capitalization = Nls.Capitalization.Title)
//  @Override
  public String getDisplayName() {
    return "Language 1C (BSL)";
  }


  @Nullable
  @Override
  public JComponent createComponent() {
    form = new BSLConfigurabeGUI();
    return form.getRootPanel();
  }

  @Override
  public void disposeUIResources() {
    form = null;
  }

  @Override
  public boolean isModified() {
    return false;
  }

  @Override
  public void apply() throws ConfigurationException {

  }
}
