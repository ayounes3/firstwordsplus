package com.ay.whatstheword;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class WordViewerActivity extends Activity {

	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	private ViewFlipper mViewFlipper;
	private AnimationListener mAnimationListener;
	private Context mContext;


	private GestureDetector detector; 
	private Category currentCat;
	private ImageView[] rightBtns;
	private ImageView[] leftBtns;
	private MediaPlayer mpReal = null;
	private boolean realSound = true;
	private View playBtn;

	private void setFlipperImage(int wordIndex) {
		final Word word = currentCat.getWords().get(wordIndex);
		final LayoutInflater inflater = getLayoutInflater();
		mViewFlipper = (ViewFlipper) this.findViewById(R.id.view_flipper);

		final View wordView = inflater.inflate(R.layout.word_layout, mViewFlipper, false);
		mViewFlipper.addView(wordView);

		ImageView image1 = (ImageView) wordView.findViewById(R.id.imageView1);
		GradientDrawable bgShape = (GradientDrawable) image1.getBackground();
		// Shape circle = (Shape) wordView.findViewById(R.id.circle_bg);
		int parseColor = Color.parseColor(word.getColor());
		bgShape.setColor(parseColor);

		ImageView image = (ImageView) wordView.findViewById(R.id.word_image);
		image.setImageResource(word.getImageId());

		TextView txt = (TextView) wordView.findViewById(R.id.word_text);
		txt.setText(word.getTitle());
		txt.setTextColor(parseColor);

		rightBtns[wordIndex] = (ImageView) wordView
				.findViewById(R.id.right_btn);
		GradientDrawable rightBtnBg = (GradientDrawable) rightBtns[wordIndex]
				.getBackground();
		rightBtnBg.setColor(parseColor);
		leftBtns[wordIndex] = (ImageView) wordView.findViewById(R.id.left_btn);
		GradientDrawable leftBtnBg = (GradientDrawable) leftBtns[wordIndex]
				.getBackground();
		leftBtnBg.setColor(parseColor);

		addNavListeners();

	}

	private void addNavListeners() {
		for (final ImageView leftBtn : leftBtns) {
			if (leftBtn != null) {
				leftBtn.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						stopAutoPlay();
						v.startAnimation(AnimationUtils.loadAnimation(
								WordViewerActivity.this, R.anim.image_click));
						v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
						MediaPlayer mp = MediaPlayer.create(
								WordViewerActivity.this, R.raw.click);
						mp.start();
						mp.setOnCompletionListener(new OnCompletionListener() {

							@Override
							public void onCompletion(MediaPlayer mp) {
								mp.stop();
								mp.release();

							}
						});
						animateAndShowNext();

					}

				});
			}
		}

		for (final ImageView rightBtn : rightBtns) {
			if (rightBtn != null) {
				rightBtn.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						stopAutoPlay();
						v.startAnimation(AnimationUtils.loadAnimation(
								WordViewerActivity.this, R.anim.image_click));
						v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
						MediaPlayer mp = MediaPlayer.create(
								WordViewerActivity.this, R.raw.click);
						mp.start();
						mp.setOnCompletionListener(new OnCompletionListener() {

							@Override
							public void onCompletion(MediaPlayer mp) {
								mp.stop();
								mp.release();

							}
						});
						animateAndShowPrevious();

					}
				});
			}
		}

		mViewFlipper.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(final View view, final MotionEvent event) {
				
			    switch (event.getAction()) {
			    case MotionEvent.ACTION_DOWN:
					stopAutoPlay();
					detector.onTouchEvent(event);
			        break;
			    case MotionEvent.ACTION_UP:
			        view.performClick();
			        break;
			    default:
			        break;
			    }
			    return true;
			}

		});
	}

	private void removeNavListeners() {
		for (ImageView leftBtn : leftBtns) {
			leftBtn.setOnClickListener(null);
		}
		for (ImageView rightBtn : rightBtns) {
			rightBtn.setOnClickListener(null);
		}
		mViewFlipper.setOnTouchListener(null);
	}

	OnClickListener playButtonListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			// sets auto flipping
			mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext,
					R.anim.left_in));
			mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(mContext,
					R.anim.left_out));
			mViewFlipper.getInAnimation().setAnimationListener(
					mAnimationListener);
			playBtn.setOnClickListener(null);
			mViewFlipper
					.setDisplayedChild(mViewFlipper.getDisplayedChild() + 1);

			//Button click effects
			playBtn.startAnimation(AnimationUtils.loadAnimation(
					WordViewerActivity.this, R.anim.image_click));
			playBtn.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
			MediaPlayer mp = MediaPlayer.create(WordViewerActivity.this,
					R.raw.click);
			mp.start();
			mp.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					mp.stop();
					mp.release();

				}
			});

			mViewFlipper.setAutoStart(true);
			mViewFlipper.setFlipInterval(4000);
			mViewFlipper.startFlipping();
			GradientDrawable playBtnBg = (GradientDrawable) playBtn
					.getBackground();
			playBtnBg.setColor(Color.GRAY);

		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_word_viewer);
		mContext = this;
		mViewFlipper = (ViewFlipper) this.findViewById(R.id.view_flipper);
		Intent intent = getIntent();
		currentCat = MainActivity.getAllCategories().get(
				intent.getIntExtra(MainActivity.SELECT_CAT, 0));
		

		setTitle(currentCat.getName());
		List<Word> allWords = currentCat.getWords();
		detector = new GestureDetector(this, new SwipeGestureDetector());
		rightBtns = new ImageView[allWords.size()];
		leftBtns = new ImageView[allWords.size()];
		for (int i = 0; i < allWords.size(); i++) {
			setFlipperImage(i);
		}
		// Play sound of first view
		removeNavListeners();
		playSounds(allWords.get(0));

		playBtn = findViewById(R.id.play);

		playBtn.setOnClickListener(playButtonListener);

		final View stopBtn = findViewById(R.id.stop);
		stopBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				// stop auto flipping
				stopAutoPlay();
				
				//Button click effects
				stopBtn.startAnimation(AnimationUtils.loadAnimation(
						WordViewerActivity.this, R.anim.image_click));
				stopBtn.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
				MediaPlayer mp = MediaPlayer.create(WordViewerActivity.this,
						R.raw.click);
				mp.start();
				mp.setOnCompletionListener(new OnCompletionListener() {

					@Override
					public void onCompletion(MediaPlayer mp) {
						mp.stop();
						mp.release();

					}
				});

			}

		});

		GradientDrawable playBtnBg = (GradientDrawable) playBtn.getBackground();
		playBtnBg.setColor(Color.WHITE);
		GradientDrawable stopBtnBg = (GradientDrawable) stopBtn.getBackground();
		stopBtnBg.setColor(Color.WHITE);
		// animation listener
		mAnimationListener = new Animation.AnimationListener() {

			public void onAnimationStart(Animation animation) {
				// animation started event
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationEnd(Animation animation) {
				Word word = currentCat.getWords().get(
						mViewFlipper.getDisplayedChild());
				playSounds(word);
			}

		};

	}

	class SwipeGestureDetector extends SimpleOnGestureListener {

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			animateAndShowNext();
			return true;
		}

		@Override
		public boolean onDown(MotionEvent e) {
			return false;
		}

		@Override
		public void onShowPress(MotionEvent e) {
			animateAndShowNext();
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			try {
				// right to left swipe
				if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {

					animateAndShowNext();
					return true;
				} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {

					animateAndShowPrevious();
					return true;
				} else {

					animateAndShowNext();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			return false;
		}

	}

	private void animateAndShowNext() {
		removeNavListeners();
		mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext,
				R.anim.left_in));
		mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(mContext,
				R.anim.left_out));
		// controlling animation
		mViewFlipper.getInAnimation().setAnimationListener(mAnimationListener);
		mViewFlipper.showNext();
	}

	private void animateAndShowPrevious() {
		removeNavListeners();
		mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext,
				R.anim.right_in));
		mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(mContext,
				R.anim.right_out));
		// controlling animation
		mViewFlipper.getInAnimation().setAnimationListener(mAnimationListener);
		mViewFlipper.showPrevious();
	}

	private void playSounds(Word word) {
		MediaPlayer mp = MediaPlayer.create(WordViewerActivity.this,
				word.getSoundId());
		try {
			mp.prepare();
		} catch (IllegalStateException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		mp.start();
		mpReal = null;
		realSound = true;
		try {
			mpReal = MediaPlayer.create(WordViewerActivity.this,
					word.getSoundReal());
			mpReal.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					mp.stop();
					mp.release();
					addNavListeners();
				}
			});
		} catch (NotFoundException e) {
			realSound = false;
		}

		mp.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				mp.stop();
				mp.release();
				if (realSound) {
					try {
						mpReal.prepare();
					} catch (IllegalStateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					mpReal.start();
				} else {
					addNavListeners();
				}

			}
		});
	}

	private void stopAutoPlay() {
		mViewFlipper.stopFlipping();
		GradientDrawable playBtnBg = (GradientDrawable) playBtn.getBackground();
		playBtnBg.setColor(Color.WHITE);
		playBtn.setOnClickListener(playButtonListener);
	}
}
