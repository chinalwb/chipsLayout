
package com.example.chipslayout;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {

  protected static final int SELECTED = 0;

  private ChipsLayout<SimpleChipModel> chipsLayout;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    this.chipsLayout = (ChipsLayout<SimpleChipModel>) this.findViewById(R.id.chipsLayout);
    
    for (int i = 0; i < 9; i++) {
      SimpleChipModel chip = new SimpleChipModel(""+i, R.drawable.boy, "xxxxx " + i);
      if (i == 1) {
        chip.setChipText("This is one of the selected Space.");
      }
      this.chipsLayout.addChip(chip);
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();
    if (id == R.id.action_settings) {
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
}
