package com.example.chipslayout;

public interface IChipListener<E extends IChipModel> {

  public void onDelete(Chip<E> chip);
}
