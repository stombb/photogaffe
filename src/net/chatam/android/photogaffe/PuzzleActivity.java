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

import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TableLayout;

/**
 * This is the primary @class Activity for the PhotoGaffe application.  It
 * creates the game board and handles any menu items selected.
 * @author wadechatam
 *
 */
public final class PuzzleActivity extends Activity {

	public static final int IMAGEREQUESTCODE = 8242008;
	public static final int DIALOG_PICASA_ERROR_ID = 0;
	public static final int DIALOG_GRID_SIZE_ID = 1;
	public static final int DIALOG_COMPLETED_ID = 2;
	private GameBoard board;
	private Bitmap bitmap; // temporary holder for puzzle picture
	private boolean numbersVisible = false; // Whether a title is displayed that
	 										// shows the correct location of the
											// tiles.

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.board);
		//TODO load saved game state
		selectImageFromGallery();
	}    

	/* (non-Javadoc)
	 * Will start an intent for external Gallery app.  
	 * Image returned via onActivityResult().
	 */
	private void selectImageFromGallery() {
		Intent galleryIntent = new Intent(Intent.ACTION_PICK, 
				MediaStore.Images.Media.INTERNAL_CONTENT_URI);
		startActivityForResult(galleryIntent, IMAGEREQUESTCODE);
	}

	/* (non-Javadoc)
	 * Run when Gallery app returns selected image.
	 */
	@Override
	protected final void onActivityResult(final int requestCode, 
			final int resultCode, 
			final Intent i) {
		super.onActivityResult(requestCode, resultCode, i);

		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case IMAGEREQUESTCODE:
				Uri imageUri = i.getData();
				try {
					bitmap = MediaStore.Images.Media.getBitmap(
							  	getContentResolver(), imageUri);
				} catch (FileNotFoundException e) {
					// You see, what had happened was...
					// When using the Gallery app for selecting an image, the 
					// Gallery will display the user's on-line Picasa web
					// albums.  If the user attempts to select one of the
					// pictures from their Picasa web albums, Gingerbread and 
					// earlier versions of the OS will throw this exception.
					// Honeycomb and later will automatically download the 
					// picture.  This catch will be called for Gingerbread
					// users and just tell them that we do not support using
					// Picasa web album pictures for their version of Android.
					// They will then be prompted to select another picture from
					// the Gallery.
					showDialog(DIALOG_PICASA_ERROR_ID);
				} catch (IOException e) {
					// This should never happen
					e.printStackTrace();
				}
				showDialog(DIALOG_GRID_SIZE_ID); // choose puzzle size
				break;
			} // end switch
		} // end if
	}

	/* (non-Javadoc)
	 * Basic wrapper method for creating the game board and setting the number
	 * visibility.
	 * @param gridSize row and column count (3 = 3x3; 4 = 4x4; 5 = 5x5; etc.)
	 */
	private final void createGameBoard(short gridSize) {
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
        
		TableLayout tableLayout;
		tableLayout = (TableLayout) findViewById(R.id.parentLayout);    
		tableLayout.removeAllViews();
		
		board = GameBoard.createGameBoard(this, 
				bitmap, 
				tableLayout,
				(int) (metrics.widthPixels * metrics.density),
				(int) (metrics.heightPixels * metrics.density),
				gridSize);
		board.setNumbersVisible(numbersVisible);
		bitmap = null; // free memory for this copy of the picture since the
					   // picture is stored by the GameBoard class
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.arranger_menu, menu);
		return true;
	}

	//TODO Replace this with ActionBar support for IceCream Sandwich (4.0)
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean returnVal = true;

		switch (item.getItemId()) {
		case R.id.new_picture :
			selectImageFromGallery();
			break;
		case R.id.reshuffle:
			board.shuffleTiles();
			break;
		case R.id.show_numbers:
			if (numbersVisible) {
				item.setTitle(R.string.show_numbers);
			} else {
				item.setTitle(R.string.hide_numbers);
			}
			toggleNumbersVisible();
			break;
		default:
			returnVal = super.onOptionsItemSelected(item);
		}

		return returnVal;
	}

	/* (non-Javadoc)
	 * Internal method for changing the visibility of the title numbers.  These
	 * numbers are the tile's correct location and consist of the row, a dash,
	 * and the column. ie. "1-2" for the tile that should be located in row 1
	 * and column 2.
	 */
	private void toggleNumbersVisible() {
		numbersVisible = !numbersVisible;
		board.setNumbersVisible(numbersVisible);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		switch(id) {
		case DIALOG_PICASA_ERROR_ID:
			builder.setMessage(R.string.picasa_error)
			.setCancelable(false)
			.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					// After message is displayed, have the user pick again.
					selectImageFromGallery();
				}
			});
			dialog = builder.create();
			break;
		case DIALOG_GRID_SIZE_ID:
			builder.setTitle(R.string.grid_size_dialog_title);
			final CharSequence[] gridSizeItems = {"3x3", "4x4", "5x5", "6x6"};
			builder.setItems(gridSizeItems, 
							 new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
					createGameBoard((short) (item + 3));
				}
			});
			dialog = builder.create();
			break;
		case DIALOG_COMPLETED_ID:
			builder.setMessage(R.string.congratulations)
			.setCancelable(false)
			.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					board.shuffleTiles();
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