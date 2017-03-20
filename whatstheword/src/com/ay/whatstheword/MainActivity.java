package com.ay.whatstheword;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.view.HapticFeedbackConstants;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.GridView;

//import android.widget.TextView;

public class MainActivity extends Activity {

	// Squishy Animation parameter
	private static final AccelerateInterpolator sAccelerator = new AccelerateInterpolator();
	private static final DecelerateInterpolator sDecelerator = new DecelerateInterpolator();
	static long SHORT_DURATION = 150;
	static long MEDIUM_DURATION = 300;
	static long REGULAR_DURATION = 450;
	static long LONG_DURATION = 500;
	private static float sDurationScale = 1f;

	protected static final String SELECT_CAT = null;
	private GridView categoryGrid;

	private OnTouchListener funButtonListener = new OnTouchListener() {

		private View touchGridItem;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			int index = categoryGrid
					.pointToPosition((int) event.getX(), (int) event.getY());
			View evaluateTouchGridItem = categoryGrid.getChildAt(index);
			if (evaluateTouchGridItem != null) {
				touchGridItem = evaluateTouchGridItem;
			}
			if (touchGridItem == null) {
				touchGridItem = categoryGrid.getChildAt(0);
			}
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				touchGridItem.animate().scaleX(.8f).scaleY(.8f)
						.setInterpolator(sDecelerator).setDuration(300);
				// mStarter.setTextColor(Color.CYAN);
				touchGridItem.removeCallbacks(mSquishRunnable);
				touchGridItem.setPressed(true);
				break;
			case MotionEvent.ACTION_MOVE:
				float x = event.getX();
				float y = event.getY();
				boolean isInside = (x > 0 && x < touchGridItem.getX() + touchGridItem.getWidth()
						&& y > 0 && y < touchGridItem.getY() + touchGridItem.getHeight());
				if (touchGridItem.isPressed() != isInside) {
					touchGridItem.setPressed(isInside);
				}
				break;
			case MotionEvent.ACTION_UP:
				if (touchGridItem.isPressed()) {
					if (index < allCat.size() && index > -1) {
						performCategoryClick(touchGridItem, index);
						touchGridItem.setPressed(false);
					} else {
						touchGridItem.animate().scaleX(1).scaleY(1)
						.setInterpolator(sAccelerator).setDuration(100);
					}
				} else {
					touchGridItem.animate().scaleX(1).scaleY(1)
							.setInterpolator(sAccelerator).setDuration(100);
				}
				// mStarter.setTextColor(Color.BLUE);
				break;
			}
			return true;
		}
	};

	private static ArrayList<Category> allCat;

	public static List<Category> getAllCategories() {
		return allCat;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initCat();

		// mStarter.setOnTouchListener(funButtonListener);
		// mStarter.animate().setDuration(100);

		categoryGrid = (GridView) findViewById(R.id.gridView1);
		CategoryGrid adapter = new CategoryGrid(MainActivity.this,
				R.layout.row_grid, allCat);
		categoryGrid.setAdapter(adapter);
		categoryGrid
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View v,
							int arg2, long arg3) {
						performCategoryClick(v, arg2);

					}

					
				});

		categoryGrid.setOnTouchListener(funButtonListener);

	}
	
	
	private void performCategoryClick(View v, final int index) {
		v.performHapticFeedback(HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
		MediaPlayer mp = MediaPlayer.create(MainActivity.this,
				R.raw.click);
		mp.start();

		// EditText editText = (EditText)
		// findViewById(R.id.edit_message);
		// String message = editText.getText().toString();
		v.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				Intent intent = new Intent(MainActivity.this,
						WordViewerActivity.class);
				intent.putExtra(SELECT_CAT, index);

				startActivity(intent);				
			}
		}, 100);
		v.animate().scaleX(1).scaleY(1)
		.setInterpolator(sAccelerator).setDuration(100);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		categoryGrid.setVisibility(View.VISIBLE);
		categoryGrid.getViewTreeObserver().addOnPreDrawListener(
				mOnPreDrawListener);
	}

	private ViewTreeObserver.OnPreDrawListener mOnPreDrawListener = new ViewTreeObserver.OnPreDrawListener() {

		@Override
		public boolean onPreDraw() {
			final View mContainer = categoryGrid;
			mContainer.getViewTreeObserver().removeOnPreDrawListener(this);
			mContainer.postDelayed(new Runnable() {
				public void run() {
					View mStarter = categoryGrid.getChildAt(0).findViewById(
							R.id.item_image);
					// Drop in the button from off the top of the screen
					mStarter.setVisibility(View.VISIBLE);
//					mStarter.setY(-mStarter.getHeight());
					squishyBounce(mStarter, 0, 0, 0, .9f, 1.05f);
				}

			}, 500);
			return true;
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void initCat() {
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder docBuilder = null;
		try {
			docBuilder = docBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Document doc = null;
		try {
			doc = docBuilder.parse(getAssets().open("word_data.xml"));
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		allCat = new ArrayList<Category>();

		// normalize text representation
		doc.getDocumentElement().normalize();

		NodeList catList = doc.getElementsByTagName("Category");

		for (int i = 0; i < catList.getLength(); i++) {

			Category currentCat = new Category();

			Node currentCatNode = catList.item(i);

			if (currentCatNode.getNodeType() == Node.ELEMENT_NODE) {

				Element currentCatElement = (Element) currentCatNode;

				currentCat.setName(currentCatElement.getAttribute("name"));
				currentCat.setId(Integer.parseInt(currentCatElement
						.getAttribute("id")));
				String imageName = currentCatElement.getAttribute("image");
				int resID = getResources().getIdentifier(imageName, "drawable",
						"com.ay.whatstheword");
				currentCat.setImage(resID);
				currentCat.setColor(currentCatElement.getAttribute("color"));
				// -------
				NodeList wordList = currentCatElement
						.getElementsByTagName("Word");
				List<Word> words = new ArrayList<Word>();
				for (int j = 0; j < wordList.getLength(); j++) {
					Word currentWord = new Word();
					Node currentWordNode = wordList.item(j);

					if (currentCatNode.getNodeType() == Node.ELEMENT_NODE) {

						Element currentWordElement = (Element) currentWordNode;
						currentWord.setTitle(currentWordElement
								.getAttribute("title"));
						currentWord.setColor(currentWordElement
								.getAttribute("color"));
						String wordImageName = currentWordElement
								.getAttribute("image");
						int wordResID = getResources().getIdentifier(
								wordImageName, "drawable",
								"com.ay.whatstheword");
						currentWord.setImageID(wordResID);
						currentWord.setSound(getResources().getIdentifier(
								currentWordElement.getAttribute("sound"),
								"raw", "com.ay.whatstheword"));
						currentWord.setSoundReal(getResources().getIdentifier(
								currentWordElement.getAttribute("sound")+"_real",
								"raw", "com.ay.whatstheword"));
						words.add(currentWord);
					}

				}

				currentCat.setWords(words);

				allCat.add(currentCat);
			}
		}
	}

	private void squishyBounce(final View view, final float startTY,
			final float bottomTY, final float endTY, final float squash,
			final float stretch) {
		view.setPivotX(view.getWidth() / 2);
		view.setPivotY(view.getHeight() / 2);
		PropertyValuesHolder pvhTY = PropertyValuesHolder.ofFloat(
				View.TRANSLATION_Y, startTY, bottomTY);
		PropertyValuesHolder pvhSX = PropertyValuesHolder.ofFloat(View.SCALE_X,
				1.4f);
		PropertyValuesHolder pvhSY = PropertyValuesHolder.ofFloat(View.SCALE_Y,
				0.9f);
		ObjectAnimator downAnim = ObjectAnimator.ofPropertyValuesHolder(view,
				pvhTY, pvhSX, pvhSY);
		downAnim.setInterpolator(sAccelerator);

		pvhTY = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, bottomTY,
				endTY);
		pvhSX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1);
		pvhSY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1);
		ObjectAnimator upAnim = ObjectAnimator.ofPropertyValuesHolder(view,
				pvhTY, pvhSX, pvhSY);
		upAnim.setInterpolator(sDecelerator);

		pvhSX = PropertyValuesHolder.ofFloat(View.SCALE_X, 0.8f);
		pvhSY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.05f);
		ObjectAnimator stretchAnim = ObjectAnimator.ofPropertyValuesHolder(
				view, pvhSX, pvhSY);
		stretchAnim.setRepeatCount(1);
		stretchAnim.setRepeatMode(ValueAnimator.REVERSE);
		stretchAnim.setInterpolator(sDecelerator);

		AnimatorSet set = new AnimatorSet();
		set.playSequentially(downAnim, stretchAnim, upAnim);
		set.setDuration(getDuration(SHORT_DURATION));
		set.start();
		set.addListener(new AnimatorListenerAdapter() {
			public void onAnimationEnd(Animator animation) {
				view.postDelayed(mSquishRunnable,
						(long) (2000 + Math.random() * 2000));
			}
		});
	}

	public static long getDuration(long baseDuration) {
		return (long) (baseDuration * sDurationScale);
	}

	private Runnable mSquishRunnable = new Runnable() {
		public void run() {
			View childToBounce = categoryGrid.getChildAt(
					(int) (Math.ceil(Math.random()
							* categoryGrid.getChildCount()) - 1)).findViewById(
					R.id.item_image);
			squishyBounce(childToBounce, 0, 0, 0, .9f, 1.05f);
		}
	};

}
