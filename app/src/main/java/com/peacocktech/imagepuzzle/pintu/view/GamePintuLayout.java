package com.peacocktech.imagepuzzle.pintu.view;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.peacocktech.imagepuzzle.R;
import com.peacocktech.imagepuzzle.utils.ImagePiece;
import com.peacocktech.imagepuzzle.utils.ImageSplitterUtil;


/**
 * �Զ����RelateLayout
 * 
 * @author luo
 * 
 * 
 *         ʹ��TranslateAnimation ���ж���ͼƬ�Ľ��� ��ʵ�ֶ���Ч�� ���Ǳ�����ͼƬ�ǽ����˽�������ʵ����ԭ����λ�û���ԭ����ͼƬ
 *         ֻ�ǻ�����ʾλ�ö��� �����û������Ĵ�� ʵ������û�иı�� ���Դ˴�Ҫ�ڽ���֮�󽫽������ͼƬɾ����ʾʵ�ʽ�����ͼƬ��
 *         �������źͽ�����ͼƬһ����ͼƬ Ȼ���ø��Ƶ�ͼƬ���ж�����ʾ ��ʾ֮��ʵ�ʵ�ͼƬ���н�����ʾ
 * 
 *         ����֮��ͨ���ӿڽ��лص�����ҳ������
 */
@SuppressLint("HandlerLeak") public class GamePintuLayout extends RelativeLayout implements OnClickListener {
	
	/**
	 * ƴͼͼƬ��Դ
	 */
	private int[] imageIds = {
			R.drawable.image1, R.drawable.image2, R.drawable.image3,
			R.drawable.image4, R.drawable.image5, R.drawable.image6,
			R.drawable.image6, R.drawable.image5, R.drawable.image4,
			R.drawable.image3, R.drawable.image2, R.drawable.image1};
	
	public int mColumn = 3;	// �ʼĬ��Ϊ��3x3
	/**
	 * �������ڱ߾�
	 */
	private int mPadding;
	/**
	 * ÿ��Сͼ֮��ľ��� dp
	 * 
	 * @param context
	 */
	private int mMargin = 3;

	private ImageView[] mGamePintuItems;

	/**
	 * ��Ϸ���Ŀ��
	 */
	private int mWidth;

	private int mItemWidth;

	/**
	 * ��Ϸ��ͼƬ
	 */
	private Bitmap mBitmap;

	private List<ImagePiece> mItemsBitmaps;

	private boolean once;

	private boolean isGameSuccess;

	private boolean isGameOver;

	private boolean isPause;
	
	// ���Լ������
	private boolean canContinuePoint = true;

	/**
	 *  �ص��Ľӿ�
	 * @author luo
	 *
	 */
	public interface GamePintuListener {
		void nextLevel(int nextLevel);

		void timeChanged(int currentTime);

		void gameOver();
	}

	// ��Ҫ��Activity���н������һ����϶���UI�Ĳ�������ʹ��handler

	public GamePintuListener mListener;

	/**
	 * ���ýӿڻص�
	 */
	public void setOnGamePintuListener(GamePintuListener mListener) {
		this.mListener = mListener;
	}

	public int level = 1;
	private static final int TIME_CHANGED = 1223;
	private static final int NEXT_LEVEL = 0127;

	private Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case TIME_CHANGED:
				// �����Ϸ���ػ�����ʱ�䵽��δ���ػ�������Ϸ��ͣ ��ʱ��ʱ�䲻�����ı�
				if (isGameSuccess || isGameOver || isPause)
					return;
				if (mListener != null) {
					mListener.timeChanged(mTime);
					if (mTime == 0) {
						isGameOver = true;
						mListener.gameOver();
						canContinuePoint = false;
						return;
					}
				}
				mTime--;
				mHandler.sendEmptyMessageDelayed(TIME_CHANGED, 1000); // ����ʹ��handler���͹㲥�ﵽʱ��ݼ���Ч��
				break;
			case NEXT_LEVEL:
				level = level + 1;
				if (mListener != null) {
					// ����MainActivity��ʵ�ֵ�OnGameListener�������еķ���
					mListener.nextLevel(level);
				} else {
					nextLevel();
				}
				canContinuePoint = false;
				break;

			default:
				break;
			}
		};
	};

	private boolean isTimeEabled; // �Ƿ���ʱ��
	private int mTime; // ��ʱ����¼ʱ��

	/**
	 * �����Ƿ���ʱ��
	 * 
	 * @param isTimeEabled
	 */
	public void setTimeEabled(boolean isTimeEabled) {
		this.isTimeEabled = isTimeEabled;
	}

	public GamePintuLayout(Context context) {
		this(context, null);
	}

	public GamePintuLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public GamePintuLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// ��ʼ���ķ���
		init();
	}

	private void init() {
		// ��dp װ����px
		mMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				3, getResources().getDisplayMetrics());
		// ��ȡ��Ϸ����ĸ����߽��е���С�߽�ΪͼƬ���ڱ߽�
		mPadding = min(getPaddingLeft(), getPaddingRight(), getPaddingTop(),
				getPaddingBottom());
	}

	/**
	 * ��дonMeasure���� ȷ����ǰ���ֵĴ�С �˴�Ҫ���ó�Ϊһ��������
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		// ȡ��͸ߵ���Сֵ��Ϊ�����Ŀ�߶�
		mWidth = min(getMeasuredWidth(), getMeasuredHeight());

		if (!once) {
			// ������ͼ, �Լ�����
			initBitmap();

			// ����ImageView(Item)�Ŀ�ߵ�����
			initItem();

			// �ж��Ƿ���ʱ��
			checkTimeEnable();

			once = true;
		}
		setMeasuredDimension(mWidth, mWidth);
	}

	private void checkTimeEnable() {
		if (isTimeEabled) {
			// ���ݵ�ǰ�Ƶȼ�����ʱ��
			countTimeBaseLevel();
			// ͨ��handler����֪ͨ
			mHandler.sendEmptyMessage(TIME_CHANGED);
		}
	}

	/**
	 * ���ݵ�ǰ�ȼ����ù���ʱ��
	 */
	private void countTimeBaseLevel() {
		mTime = (int) (Math.pow(2, level) * 25);
	}

	/**
	 * ������ͼ, �Լ�����
	 */
	private void initBitmap() {

		// ��������ͼƬ
		if (mBitmap == null) {
			mBitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.image6);
		}else{
			mBitmap = BitmapFactory.decodeResource(getResources(), imageIds[(int) (Math.random() * 12)]);
		}

		// ����ָ��ͼƬ������������ͼ
		mItemsBitmaps = ImageSplitterUtil.splitImage(mBitmap, mColumn);

		// ʹ�� sort ʹͼƬ����Ĺ���
		Collections.sort(mItemsBitmaps, new Comparator<ImagePiece>() {

			@Override
			public int compare(ImagePiece a, ImagePiece b) {
				return Math.random() > 0.5 ? 1 : -1;
			}
		});
	}

	/**
	 * ����ImageView(Item)�Ŀ�ߵ����� �Լ�ÿ��Item������
	 */
	private void initItem() {
		mItemWidth = (mWidth - mPadding * 2 - mMargin * (mColumn - 1))
				/ mColumn;

		mGamePintuItems = new ImageView[mColumn * mColumn];

		// �������ǵ�Item�� ����Rule
		for (int i = 0; i < mGamePintuItems.length; i++) {
			ImageView item = new ImageView(getContext());
			// Ϊÿ��Item ��Ӽ���
			item.setOnClickListener(this);
			item.setImageBitmap(mItemsBitmaps.get(i).getBitmap());

			mGamePintuItems[i] = item;

			// Ϊÿһ��Item����������ID��������ÿ��Itemʹ��Id�����Ű�
			item.setId(i + 1);

			// ��Item��tag�д洢��index index����������ǹ���ʱͼƬӦ�е�index
			item.setTag(i + "_" + mItemsBitmaps.get(i).getIndex());

			LayoutParams lp1 = new LayoutParams(
					mItemWidth, mItemWidth);
			// ����Item������϶��ͨ��rightMargin

			// �������һ��
			if ((i + 1) % mColumn != 0) {
				lp1.rightMargin = mMargin;
			}

			// ���ǵ�һ��
			if (i % mColumn != 0) {
				lp1.addRule(RelativeLayout.RIGHT_OF,
						mGamePintuItems[i - 1].getId());
			}

			// ������ǵ�һ��
			if ((i + 1) > mColumn) {
				lp1.topMargin = mMargin;
				lp1.addRule(RelativeLayout.BELOW,
						mGamePintuItems[i - mColumn].getId());
			}

			addView(item, lp1);
		}
	}

	/**
	 * ��ȡ�����������Сֵ
	 * 
	 * @param paddingLeft
	 * @param paddingRight
	 * @param paddingTop
	 * @param paddingBottom
	 * @return
	 */
	private int min(int... params) {
		int min = params[0];
		for (int param : params) {
			if (param < min) {
				min = param;
			}
		}

		return min;
	}

	/**
	 * ��¼���������ͼƬ
	 */
	private ImageView mFirst;
	private ImageView mSecond;

	@Override
	public void onClick(View v) {

		if (isAniming || !canContinuePoint) {
			return;
		}

		// ���ε��ͬһ��Item ȡ������
		if (mFirst == v) {
			mFirst.setColorFilter(null);
			mFirst = null;
			return;
		}

		if (mFirst == null) { // ���ǵ�һ�ε��
			mFirst = (ImageView) v;
			// ����ѡ��״̬ ǰ����������ֱ�ʾ͸���ȣ�FF:��ȫ��͸�� 00����ȫ͸��
			mFirst.setColorFilter(Color.parseColor("#55FF5500"));
		} else { // ���ǵڶ��ε��
			mSecond = (ImageView) v;
			// �������ǵ�Item
			exchangeView();
		}
	}

	/**
	 * ������
	 */
	private RelativeLayout mAnimLayout;
	private boolean isAniming;

	/**
	 * �������ǵ�Item
	 */
	private void exchangeView() {

		mFirst.setColorFilter(null); // ȡ������

		// �������ǵĶ�����
		setUpAnimLayout();

		String firstTag = (String) mFirst.getTag();
		ImageView firstImage = new ImageView(getContext());
		final Bitmap firstBitmap = mItemsBitmaps.get(getImageIdByTag(firstTag))
				.getBitmap();
		firstImage.setImageBitmap(firstBitmap);
		LayoutParams lp1 = new LayoutParams(mItemWidth, mItemWidth);
		lp1.leftMargin = mFirst.getLeft() - mPadding;
		lp1.topMargin = mFirst.getTop() - mPadding;
		firstImage.setLayoutParams(lp1);
		mAnimLayout.addView(firstImage);

		String secondTag = (String) mSecond.getTag();
		ImageView secondImage = new ImageView(getContext());
		final Bitmap secondBitmap = mItemsBitmaps.get(
				getImageIdByTag(secondTag)).getBitmap();
		secondImage.setImageBitmap(secondBitmap);
		LayoutParams lp2 = new LayoutParams(mItemWidth, mItemWidth);
		lp2.leftMargin = mSecond.getLeft() - mPadding;
		lp2.topMargin = mSecond.getTop() - mPadding;
		secondImage.setLayoutParams(lp2);
		mAnimLayout.addView(secondImage);

		// ���ö���
		TranslateAnimation animFirst = new TranslateAnimation(0,
				mSecond.getLeft() - mFirst.getLeft(), 0, mSecond.getTop()
						- mFirst.getTop());
		animFirst.setDuration(300);
		animFirst.setFillAfter(true);
		firstImage.startAnimation(animFirst); // Ϊ��һ�������ͼƬ���ö���

		TranslateAnimation animSecond = new TranslateAnimation(0,
				-mSecond.getLeft() + mFirst.getLeft(), 0, -mSecond.getTop()
						+ mFirst.getTop());
		animSecond.setDuration(300);
		animSecond.setFillAfter(true);
		secondImage.startAnimation(animSecond); // Ϊ�ڶ��������ͼƬ���ö���

		// ��������
		animFirst.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation arg0) {
				mFirst.setVisibility(View.INVISIBLE);
				mSecond.setVisibility(View.INVISIBLE);
				isAniming = true;
			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onAnimationEnd(Animation arg0) {
				String firstTag = (String) mFirst.getTag();
				String secondTag = (String) mSecond.getTag();

				mFirst.setImageBitmap(secondBitmap);
				mSecond.setImageBitmap(firstBitmap);

				/**
				 * ����Ľ���ͼƬ�Ĵ���ֻ�ǽ�ָ���������ͼƬ�����˸ı� ���Ǹ��������Ӧ����Ϣ (���磺tag��index��û�н���
				 * ����˵��������ʾ��ͼƬ����Ϣʼ��û�иı�ֻ�ǽ�ͼƬ���� ������Ϊ������ж��Ƿ����
				 * �Լ�������ͼƬ�ڴ˻�ԭʱ��ȡ��index�Ǻ�ԭ��һ�������Իύ������ �������������ѵ� �����ڴ�Ҫ�����е���Ϣ���н���)
				 */
				mFirst.setTag(secondTag);
				mSecond.setTag(firstTag);

				mFirst.setVisibility(View.VISIBLE);
				mSecond.setVisibility(View.VISIBLE);

				// �����Ǹ�ֵΪ�� �����´ε����ʱ����
				mFirst = mSecond = null;

				// ��������ɾ��view �����´ν������¶���Ҫ�����Ķ���ͼƬ
				mAnimLayout.removeAllViews();

				// �ж��û���Ϸ�Ƿ�ɹ�
				checkSuccess();

				isAniming = false;
			}
		});
	}

	/**
	 * �ж��û���Ϸ�Ƿ�ɹ�
	 */
	private void checkSuccess() {
		boolean isSuccess = true;
		for (int i = 0; i < mGamePintuItems.length; i++) {
			ImageView imageView = mGamePintuItems[i];
			if (getImageIndex((String) imageView.getTag()) != i) {
				isSuccess = false;
				break;
			}
		}

		if (isSuccess) {
			isGameSuccess = true;
			// ɾ����һ�ؿ���handler��Ϣ ��ֹ������һ�ص�ʱ����յ���Ϣ��Ƶ�ʱ�� ʱ����
			mHandler.removeMessages(NEXT_LEVEL);
			/*
			 * Toast.makeText(getContext(), "��ϲ��,�ɹ����� ������", Toast.LENGTH_LONG)
			 * .show();
			 */
			// handler������Ϣ Ȼ��ص� ʹ��ҳ������
			mHandler.sendEmptyMessage(NEXT_LEVEL);
		}
	}

	/**
	 * �������ǵĶ�����
	 */
	private void setUpAnimLayout() {
		if (mAnimLayout == null) {
			mAnimLayout = new RelativeLayout(getContext());
			addView(mAnimLayout);
		}
	}

	/**
	 * ͨ��Tag��ø������ϵ�ͼƬ
	 */
	public int getImageIdByTag(String tag) {
		String[] parms = tag.split("_");
		return Integer.parseInt(parms[0]);
	}

	public int getImageIndex(String tag) {
		String[] parms = tag.split("_");
		return Integer.parseInt(parms[1]);
	}

	/**
	 * ʱ�䵽�����浱ǰ�ؿ�
	 */
	public void reStart() {
		isGameOver = false;
		mColumn--;
		nextLevel();
	}

	/**
	 * ��Ϸ��ͣ ��ʱ����ͣ
	 */
	public void pause() {
		isPause = true;
		mHandler.removeMessages(TIME_CHANGED);
	}

	/**
	 * ��Ϸ�ָ� ��ʱ����������
	 */
	public void reSume() {
		if (isPause) {
			isPause = false;
			mHandler.sendEmptyMessage(TIME_CHANGED);
		}
	}

	/**
	 * ���ص��� ����ҳ��
	 */
	public void nextLevel() {
		System.out.println("GamePintuLayout nextLevel");
		this.removeAllViews();
		mAnimLayout = null;
		mColumn++;
		isGameSuccess = false;
		canContinuePoint = true;

		// ���¸���ʱ��
		checkTimeEnable();
		// ����ҳ��
		initBitmap();
		initItem();
	}
}
