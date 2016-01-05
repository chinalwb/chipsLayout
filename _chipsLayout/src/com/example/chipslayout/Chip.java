package com.example.chipslayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Chip view.
 * Each chip contains a LinearLayout which consists of an icon (ImageView) and
 * a text (TextView).
 * 
 * @author Wenbin Liu
 */
public class Chip<E extends IChipModel> extends LinearLayout {

  /**
   * Chip LinearLayout.
   */
  private LinearLayout chipLayout;
  
  /**
   * Chip icon.
   */
  private CircleImageView chipIcon;
  
  /**
   * Chip text.
   */
  private TextView chipText;
  
  /**
   * Chip model.
   */
  private E chipModel;
  
  /**
   * Chip icon res id.
   */
  private int chipIconResId;
  
  /**
   * Chip Listener for handling delete event.
   */
  private IChipListener<E> mListener;
  
  /**
   * Chip status, by default it is in {@link ChipStatus#NORMAL}.
   */
  private ChipStatus mStatus = ChipStatus.NORMAL;
  
  /**
   * 
   * Chip status.
   * 
   * @author Wenbin Liu
   */
  private enum ChipStatus {
    NORMAL,
    SELECTED
  }
  
  /**
   * Simple constructor.
   * 
   * @param context
   */
  public Chip(Context context) {
    this(context, null);
  }

  /**
   * Constructor.
   * 
   * @param context
   * @param attrs
   */
  public Chip(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }
  
  /**
   * Constructor.
   * 
   * @param context
   * @param attrs
   * @param defStyle
   */
  public Chip(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    init(context);
  }
  
  /**
   * 
   * Inits the variables.
   * 
   * @param context
   */
  private void init(Context context) {
    LinearLayout chipContainer = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.chip, this);
    this.chipLayout = (LinearLayout) chipContainer.findViewById(R.id.chipLayout);
    this.chipIcon = (CircleImageView) this.chipLayout.findViewById(R.id.chipIcon);
    this.chipText = (TextView) this.chipLayout.findViewById(R.id.chipText);
    
    this.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        if (mStatus == ChipStatus.NORMAL) {
          selectChip();
        }
        else if (mStatus == ChipStatus.SELECTED) {
          normalizeChip();
        }
        else {
          normalizeChip();
        }
      }
    });
    
    this.chipIcon.setOnClickListener(new OnClickListener() {
      
      @Override
      public void onClick(View v) {
        if (mStatus == ChipStatus.NORMAL) {
          selectChip();
        }
        else if (mStatus == ChipStatus.SELECTED) {
          deleteChip();
        }
      }
    });
  }
  
  /**
   * Click the chip then the chip enters {@link ChipStatus#SELECTED} mode.
   */
  private void selectChip() {
    this.mStatus = ChipStatus.SELECTED;
    this.setBackgroundResource(R.drawable.layout_pressed);
    this.chipIcon.setImageResource(R.drawable.delete);
  }
  
  /**
   * Re-Click the chip then the chip enters {@link ChipStatus#NORMAL} mode.
   */
  private void normalizeChip() {
    this.mStatus = ChipStatus.NORMAL;
    this.setBackgroundResource(R.drawable.chip_layout_normal);
    this.setChipIcon(this.chipIconResId);
  }
  
  /**
   * Hits the delete icon to delete a chip.
   */
  private void deleteChip() {
    if (null != this.mListener) {
      this.mListener.onDelete(this);
    }
  }
  
  /**
   * Sets model on the chip.
   * 
   * @param chipModel
   */
  public void setChipModel(E chipModel) {
    this.chipModel = chipModel;
    if (null == this.chipModel) {
      return;
    }
    this.setChipIcon(this.chipModel.getChipIcon());
    this.setChipText(this.chipModel.getChipText());
  }
  
  /**
   * Gets the chip model bind with this chip.
   * @return
   */
  public E getChipModel() {
    return this.chipModel;
  }
  
  /**
   * Sets the chip icon res id.
   */
  public void setChipIcon(int resId) {
    this.chipIconResId = resId;
    this.chipIcon.setImageResource(resId);
  }
  
  /**
   * Sets chip text by res id.
   * @param resId
   */
  public void setChipText(int resId) {
    this.chipText.setText(resId);
  }
  
  /**
   * Sets chip text by CharSequence.
   * 
   * @param text
   */
  public void setChipText(CharSequence text) {
    this.chipText.setText(text);
  }
  
  @Override
  public void setBackgroundResource(int resid) {
    this.chipLayout.setBackgroundResource(resid);
  }

  /**
   * Sets the {@link IChipListener} for handling delete event.
   * @param mListener
   */
  public void setChipListener(IChipListener<E> mListener) {
    this.mListener = mListener;
  }
  
}
