# chipsLayout
Android chips layout which follow the latest Material Design of [Chips](http://www.google.com/design/spec/components/chips.html "Chips")



![image](https://raw.githubusercontent.com/chinalwb/chipsLayout/master/imgs/P1.png)
![image](https://raw.githubusercontent.com/chinalwb/chipsLayout/master/imgs/P2.png)
![image](https://raw.githubusercontent.com/chinalwb/chipsLayout/master/imgs/P3.png)

This layout can show multiple chips in several lines, and at the end of the chips, 
there is an EditText, which accepts user's input and can do anything in the TextWatcher.

##Usage is very simple, in Layout:

    <com.example.chipslayout.ChipsLayout
        android:id="@+id/chipsLayout"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        chipsLayoutPkg:editTextHint="Hard code string or @string/"
        chipsLayoutPkg:editTextBackground="@null"
        chipsLayoutPkg:iconRight="@drawable/add">
    </com.example.chipslayout.ChipsLayout>
    

##Activity:
  
    this.chipsLayout = (ChipsLayout<SimpleChipModel>) this.findViewById(R.id.chipsLayout);
    for (int i = 0; i < 9; i++) {
      SimpleChipModel chip = new SimpleChipModel(""+i, R.drawable.boy, "xxxxx " + i);
      if (i == 1) {
        chip.setChipText("This is one of the selected Space.");
      }
      this.chipsLayout.addChip(chip);
    }
    
    
You can add anything (the chip icon and the text) you want into the chip.


#License
Copyright (C) 2016 Wenbin Liu

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
