package com.example.chipslayout;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * ChipsLayout.
 * 
 * @author Wenbin Liu
 *
 */
public class ChipsLayout<E extends IChipModel> extends ViewGroup implements IChipListener<E> {

  /**
   * When layout in collapse, at least {@value #MIN_CHILDCOUNT} being shown.
   * The others chips if any, will be hidden.
   */
  private static final int MIN_CHILDCOUNT = 4;

  /**
   * The min space (in dip) for displaying the ImageView to the right of the
   * EditText.
   */
  private static final int IMAGEVIEW_MINSPACE_DP = 30;

  /**
   * The size (in dip) of the ImageView, which is placed to the right of the
   * EditText.
   */
  private static final int IMAGEVIEW_SIZE = 24;
  
  /**
   * The min width (in dip) of the EditText.
   * If there is more than {@value #EDITTEXT_MINWIDTH_DP} space in the row,
   * then place the EditText to this line, or else to the next row. 
   */
  private static final int EDITTEXT_MINWIDTH_DP = 150;

  /**
   * All of the chips list being shown in the UI.
   */
  private ArrayList<E> chipsList = new ArrayList<E>();
  
  /**
   * Android context
   */
  private Context context;
  
  /**
   * The EditText at the end of chips.
   */
  private EditText editText;

  /**
   * The measured height of the EditText. 
   */
  private int heightOfEditText;
  
  /**
   * The ImageView to the right of the EditText.
   */
  private ImageView imageView;

  /**
   * Because we want to center vertical aligned the ImageView to the EditText.
   * we need the top margin for the ImageView, which moves the ImageView a bit
   * lower.
   */
  private int imageViewTopMargin;
  
  /**
   * The ImageView to show how many chips are hidden when layout in collapsed
   * status.
   */
  private ImageView hideCountImageView;
  
  /**
   * Custom attribute: the hint of the EditText
   */
  private CharSequence editTextHint;
  
  /**
   * Custom attribute: the background of the EditText
   */
  private Drawable editTextBackgroundDrawable;
  
  /**
   * Custom attribute: the resource id of the icon which is to the right of the
   * EditText
   */
  private int iconRightInt;
  
  /**
   * Current collapse status of this layout.
   */
  private LayoutStatus layoutStatus = LayoutStatus.NORMAL;
  
  /**
   * The collapse status of this layout.
   * 
   * @author Wenbin Liu
   *
   */
  private enum LayoutStatus {
    /**
     * The layout is in collapsed status now
     */
    COLLAPSED,
    
    /**
     * The layout is in normal status now - Expanded, all children visible
     */
    NORMAL
  }
  
  /**
   * Simple constructor.
   *  
   * @param context
   */
  public ChipsLayout(Context context) {
    this(context, null);
  }
  
  /**
   * Constructor.
   * 
   * @param context
   * @param attrSet
   */
  public ChipsLayout(Context context, AttributeSet attrSet) {
    this(context, attrSet, 0);
  }
  
  /**
   * Constructor to be used when initializing the Layout by inflating from 
   * layout xml file.
   * 
   * @param context
   * @param attrs
   * @param defStyle
   */
  @SuppressWarnings("deprecation")
  public ChipsLayout(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.ChipsLayout, 0, 0);
    
    //
    // EditText background resource
    int NO_BACKGROUND_SET_INT = -1;
    int editTextBackgroundInt = t.getResourceId(
        R.styleable.ChipsLayout_editTextBackground, 
        NO_BACKGROUND_SET_INT);
    
    if (NO_BACKGROUND_SET_INT == editTextBackgroundInt) {
      this.editTextBackgroundDrawable = null;
    }
    else {
      this.editTextBackgroundDrawable = getResources().getDrawable(editTextBackgroundInt);
    }
    
    //
    // EditText hint
    this.editTextHint = t.getString(R.styleable.ChipsLayout_editTextHint);
    if (TextUtils.isEmpty(this.editTextHint)) {
      int editTextHintInt = t.getInt(R.styleable.ChipsLayout_editTextHint, R.string.app_name);
      this.editTextHint = getResources().getString(editTextHintInt);
    }
    
    //
    // ImageView icon resource.
    this.iconRightInt = t.getResourceId(R.styleable.ChipsLayout_iconRight, R.drawable.add);
    t.recycle();
    
    //
    // Initialize the layout
    initialize(context);
  }

  /**
   * Initializing the layout: by default, the layout will show EditText and 
   * ImageView.
   * 
   * @param context
   */
  private void initialize(Context context) {
    this.context = context;
    // 
    // By default add the EditText and ImageView
    this.addChildren(context);
    //
    // Add the click event for the layout to collapse or expand.
    this.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        if (layoutStatus == LayoutStatus.NORMAL) {
          collapse();
        }
        else if (layoutStatus == LayoutStatus.COLLAPSED){
          expand();
        }
      }
    });
  }
  
  /**
   * By default, the ChipsLayout at least will show EditText and an Add icon.
   * @param context
   */
  @SuppressWarnings("deprecation")
  private void addChildren(Context context) {
    this.editText = new EditText(context);
    this.editText.setHint(this.editTextHint);
    this.editText.setBackgroundDrawable(this.editTextBackgroundDrawable);
    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    this.editText.setLayoutParams(new LayoutParams(params));
    this.addView(editText);
    this.editText.setSingleLine(true);
    
    this.imageView = new ImageView(context);
    this.imageView.setImageResource(this.iconRightInt);
    int iconSize = getPixelsByDp(context, IMAGEVIEW_SIZE);
    LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(iconSize, iconSize);
    iconParams.gravity = Gravity.CENTER_VERTICAL;
    this.addView(imageView, new LayoutParams(iconParams));
    
    this.hideCountImageView = new ImageView(context);
    this.addView(this.hideCountImageView, new LayoutParams(iconParams));
    this.hideCountImageView.setVisibility(View.GONE);
    this.hideCountImageView.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        expand();
      }
    });
  }
  
  
  /**
   * Collapse the layout. 
   */
  private void collapse() {
    int childCount = this.getChildCount();
    if (childCount < MIN_CHILDCOUNT) {
      return;
    }
    
    View firstChild = this.getChildAt(0);
    int firstChildTop = firstChild.getTop();
    int halfHeight = firstChild.getHeight() / 2;
    
    boolean toHide = false;
    
    int hideCount = 0;
    for (int i = 1; i < childCount; i++) {
      View child = this.getChildAt(i);
      if (toHide) {
        if (child instanceof Chip) {
          ++hideCount;
          child.setVisibility(View.GONE);
        }
      }
      
      int childTop = child.getTop();
      if (childTop > (firstChildTop + halfHeight)) {
        // 
        // This view is in another line.
        // Find the hide position chip.
        // The other chips should be hidden after this.
        toHide = true;
        continue;
      }
    }
    Bitmap bitmap = addTextToBitmap(context, "+" + hideCount);
    this.hideCountImageView.setImageBitmap(bitmap);
    this.hideCountImageView.setVisibility(View.VISIBLE);
    this.imageView.setVisibility(View.GONE);
    if (toHide) {
      this.layoutStatus = LayoutStatus.COLLAPSED;
    }
  }
  
  /**
   * Generates a Bitmap with text on it.
   * 
   * @param context
   * @param text
   * @return
   */
  private static Bitmap addTextToBitmap(Context context, String text) {
    int width = getPixelsByDp(context, IMAGEVIEW_SIZE);
    Bitmap bitmap = Bitmap.createBitmap(width, width, Config.ARGB_8888);
    float scale = context.getResources().getDisplayMetrics().density;
    Canvas canvas = new Canvas(bitmap);
    Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DEV_KERN_TEXT_FLAG);
    textPaint.setTextSize(13*scale);
    textPaint.setTypeface(Typeface.DEFAULT);
    textPaint.setColor(Color.GRAY);
    Rect bounds = new Rect();
    textPaint.getTextBounds(text, 0, text.length(), bounds);
    int x = (bitmap.getWidth() - bounds.width())/2;
    int y = (bitmap.getHeight() + bounds.height())/2;
    canvas.drawText(text, x, y, textPaint);
    return bitmap;
  }
  
  /**
   * Expand the layout. 
   */
  private void expand() {
    if (this.layoutStatus == LayoutStatus.NORMAL) {
      return;
    }
    int childCount = this.getChildCount();
    for (int i = 0; i < childCount; i++) {
      View child = this.getChildAt(i);
      child.setVisibility(View.VISIBLE);
    }
    
    this.hideCountImageView.setVisibility(View.GONE);
    this.imageView.setVisibility(View.VISIBLE);
    this.layoutStatus = LayoutStatus.NORMAL;
  }


  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int specWidth = MeasureSpec.getSize(widthMeasureSpec);
    
    int finalWidth = 0;
    int finalHeight = 0;
    
    int widthPos = 0;
    int heightPos = 0;
    
    int numOfChildren = this.getChildCount();
    for (int i = 0; i < numOfChildren; i++) {
      View child = this.getChildAt(i);
      int visibility = child.getVisibility();
      if (visibility == View.GONE) {
        continue;
      }
      this.measureChild(child, widthMeasureSpec, heightMeasureSpec);
      int childWidth = child.getMeasuredWidth();
      int childHeight = child.getMeasuredHeight();
      
      if (childWidth > specWidth) {
        childWidth = specWidth - 10; // 10 is hard coded!
      }
      
      //
      // Special layout for EditText
      if (child instanceof EditText) {
        if (this.heightOfEditText <= 0) {
          this.heightOfEditText = childHeight;
        }
        LayoutParams lp = (LayoutParams) child.getLayoutParams();        
        int editTextMinWidth = getPixelsByDp(context, EDITTEXT_MINWIDTH_DP);
        if (specWidth - widthPos < editTextMinWidth) {
          int iconMinWidth = getPixelsByDp(context, IMAGEVIEW_MINSPACE_DP);
          childWidth = specWidth - iconMinWidth;
          lp.width = childWidth;
        }
        else {
          int availableWidth = specWidth - widthPos;
          int iconMinWidth = getPixelsByDp(context, IMAGEVIEW_MINSPACE_DP);
          childWidth = availableWidth - iconMinWidth;
          lp.width = childWidth;
        }
        ((EditText) child).setMaxHeight(this.heightOfEditText);
      }
      
      //
      // Special layout for ImageView
      if (child instanceof ImageView) {
        int heightOffset = this.heightOfEditText - childHeight;
        this.imageViewTopMargin = heightOffset / 2;
      }
      
      
      if (widthPos + childWidth > specWidth) {
        // 
        // New Line
        
        //
        // Calculate the real width
        finalWidth = Math.max(finalWidth, widthPos);
        
        //
        // Move the width pos to the beginning
        widthPos = 0;
        
        //
        // Move the cursor to the next line
        heightPos += childHeight;
      }
      LayoutParams lp = (LayoutParams) child.getLayoutParams();
      lp.x = widthPos;
      lp.y = heightPos;
      
      // Log.e("XX", i + ">>>> x == " + widthPos + ">>> y == " + heightPos);
      widthPos += childWidth;
      
      finalHeight = Math.max(finalHeight, heightPos + childHeight);
    } // #End of for
    

    int resolvedWidth = resolveSize(finalWidth, widthMeasureSpec);
    int resolvedHeight = resolveSize(finalHeight, heightMeasureSpec);
    // Log.e("ZZZ", "resolved width == " + resolvedWidth + " >>> resolved height == " + resolvedHeight);
    setMeasuredDimension(resolvedWidth, resolvedHeight);
  }
  
  
  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {
    int numOfChildren = this.getChildCount();
    
    for (int i = 0; i < numOfChildren; i++) {
      View child = this.getChildAt(i);
      LayoutParams lp = (LayoutParams) child.getLayoutParams();
      Log.e("YY", i + " Left: " + lp.x + " >> top: " + lp.y + " >> right: " + ((int) lp.x + child.getMeasuredWidth()) + " >> bottom: " + ((int) lp.y + child.getMeasuredHeight()));
      
      int left = lp.x;
      int top = lp.y;
      if (child instanceof ImageView) {
        top = lp.y + this.imageViewTopMargin;
      }
      child.layout(left, top, left + child.getMeasuredWidth(), top + child.getMeasuredHeight());
    }
  }
  
  /**
   * Adds a chip into the chip layout.
   * 
   * @param chipModel
   */
  public void addChip(E chipModel) {
    this.chipsList.add(chipModel);
    Chip<E> chip = createChip(context, chipModel);
    chip.setChipListener(this);
    
    //
    // Append the chip to the last position before the EditText.
    // So the EditText will be always after the last chip.
    int indexOfEditText = this.indexOfChild(this.editText);
    this.addViewInLayout(chip, indexOfEditText, chip.getLayoutParams());
  }
  
  /**
   * Adds a collections of chips.
   * @param chipsList
   */
  public void addChips(List<E> chipsList) {
    this.chipsList.addAll(chipsList);
    for (E chipModel : chipsList) {
      this.addChip(chipModel);
    }
  }
  
  /**
   * Create a {@link Chip} view instance.
   * @param context
   * @param chipModel
   * @return
   */
  public Chip<E> createChip(Context context, E chipModel) {
    Chip<E> chip = new Chip<E>(context);
    LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    ChipsLayout.LayoutParams chipLayoutParam = new ChipsLayout.LayoutParams(lp);
    chip.setLayoutParams(chipLayoutParam);
    chip.setChipModel(chipModel);
    return chip;
  }
  
  /**
   * Returns the dip according to the given value.
   * 
   * @param context
   * @param dps
   * @return
   */
  public static int getPixelsByDp(Context context, int dps) {
    final float scale = context.getResources().getDisplayMetrics().density;
    int pixels = (int) (dps * scale + 0.5f);
    return pixels;
  }
  
  
  
  /* ------------------------------------ *
   * Getter / Setter
   * ------------------------------------ */
  
  /**
   * Returns the EditText widget in ChipsLayout.
   * @return
   */
  public EditText getEditText () {
    return this.editText;
  }
  
  public ImageView getImageView () {
    return this.imageView;
  }
  
  /**
   * Returns the list of the chips currently being shown.
   * @return
   */
  public ArrayList<E> getAllChips() {
    return this.chipsList;
  }

  @Override
  public void onDelete(Chip<E> chip) {
    this.removeView(chip);
    E e = chip.getChipModel();
    this.chipsList.remove(e);
  }
  
  /**
   * The LayoutParams of ChipsLayout.
   * 
   * @author Wenbin Liu
   *
   */
  public static class LayoutParams extends MarginLayoutParams {

    public int spacing = -1;
    public int x = 0;
    public int y = 0;
    
    public LayoutParams(Context c, AttributeSet attrs) {
      super(c, attrs);
    }
    
    public LayoutParams(int width, int height) {
      super(width, height);
    }
    
    public LayoutParams(ViewGroup.LayoutParams p) {
      super(p);
    }
    
    public LayoutParams(MarginLayoutParams source) {
      super(source);
    }
  }
}

