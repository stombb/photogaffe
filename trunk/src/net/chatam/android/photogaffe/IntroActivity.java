/*
Copyright (C) 2011  Wade Chatam

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.chatam.android.photogaffe;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * This is the introductory Activity for the PhotoGaffe application.  It gives
 * the user some basic options for starting a new game and learning more about
 * the application.
 * @author wadechatam
 *
 */
public final class IntroActivity extends Activity implements OnClickListener{

   public static final int DIALOG_ABOUT_ID = 0;   
   
   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.intro);
      findViewById(R.id.play_button).setOnClickListener(this);
      findViewById(R.id.settings_button).setOnClickListener(this);
      findViewById(R.id.about_button).setOnClickListener(this);
   }

   public void onClick(View v) {
      switch(v.getId()) {
      case R.id.play_button:
         Intent i = new Intent(this, PuzzleActivity.class);
         startActivity(i);
         break;
      case R.id.settings_button:
         Intent i2 = new Intent(this, SettingsActivity.class);
         startActivity(i2);
         break;
      case R.id.about_button:
         showDialog(DIALOG_ABOUT_ID);
         break;
      }
   }  
   
   @Override
   protected Dialog onCreateDialog(int id) {
      Dialog dialog;
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      switch(id) {
      case DIALOG_ABOUT_ID:
         builder.setMessage(R.string.about_text)
         .setCancelable(false)
         .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
               dialog.dismiss();
            }
         });
         dialog = builder.create();
         break;
      default:
         dialog = null;
      }
      return dialog;
   }

}
