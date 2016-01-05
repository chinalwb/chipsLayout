package com.example.chipslayout;

public class SimpleChipModel implements IChipModel {

  private String chipKey;
  private int chipIcon;
  private CharSequence chipText;
  
  public SimpleChipModel (String chipKey, int chipIcon, CharSequence chipText) {
    this.chipKey = chipKey;
    this.chipIcon = chipIcon;
    this.chipText = chipText;
  }
  
  @Override
  public int getChipIcon() {
    return this.chipIcon;
  }

  @Override
  public CharSequence getChipText() {
    return this.chipText;
  }

  @Override
  public String getChipKey() {
    return this.chipKey;
  }

  public void setChipText(CharSequence chipText) {
    this.chipText = chipText;
  }
  
  
}
