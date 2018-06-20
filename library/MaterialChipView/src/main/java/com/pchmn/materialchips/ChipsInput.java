package com.pchmn.materialchips;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;
import com.pchmn.materialchips.adapter.ChipsAdapter;
import com.pchmn.materialchips.model.Chip;
import com.pchmn.materialchips.model.ChipInterface;
import com.pchmn.materialchips.util.ActivityUtil;
import com.pchmn.materialchips.util.MyWindowCallback;
import com.pchmn.materialchips.util.ViewUtil;
import com.pchmn.materialchips.views.ChipsInputEditText;
import com.pchmn.materialchips.views.DetailedChipView;
import com.pchmn.materialchips.views.FilterableListView;
import com.pchmn.materialchips.views.ScrollViewMaxHeight;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChipsInput extends ScrollViewMaxHeight
{

	private static final String TAG  = ChipsInput.class.toString();
	// attributes
	private static final int    NONE = -1;
	// xml element
	RecyclerView mRecyclerView;
	// context
	private Context        mContext;
	// adapter
	private ChipsAdapter   mChipsAdapter;
	private String         mHint;
	private ColorStateList mHintColor;
	private ColorStateList mTextColor;
	private int mMaxRows = 2;
	private ColorStateList mChipLabelColor;
	private boolean mChipHasAvatarIcon = true;
	private boolean mChipDeletable     = false;
	private Drawable       mChipDeleteIcon;
	private ColorStateList mChipDeleteIconColor;
	private ColorStateList mChipBackgroundColor;
	private boolean mChipWidthMatchParent = false;
	private boolean mShowChipDetailed     = true;
	private boolean mAllowNewChips        = false;
	private boolean mChipClickNotify      = false;
	private int     mMaxViewsInRow        = -1;
	private ColorStateList mChipDetailedTextColor;
	private ColorStateList mChipDetailedDeleteIconColor;
	private ColorStateList mChipDetailedBackgroundColor;
	private ColorStateList mFilterableListBackgroundColor;
	private ColorStateList mFilterableListTextColor;
	private boolean        mFilterableUseLetterTile;
	private boolean        mFilterableListAlwaysShow;
	ChipsLayoutManager mChipsLayoutManager;
	// chips listener
	private List<ChipsListener> mChipsListenerList = new ArrayList<>();
	private ChipsListener                 mChipsListener;
	// chip list
	private List<? extends ChipInterface> mChipList;
	private FilterableListView            mFilterableListView;
	// chip validator
	private ChipValidator                 mChipValidator;
	private List<Character> mValidChipSeparators = new ArrayList<>(Arrays.asList(' ', ',', ';'));

	public ChipsInput(Context context)
	{
		super(context);
		mContext = context;
		init(null);
	}

	public ChipsInput(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		mContext = context;
		init(attrs);
	}

	/**
	 * Inflate the view according to attributes
	 *
	 * @param attrs the attributes
	 */
	@SuppressLint("ClickableViewAccessibility")
	private void init(AttributeSet attrs)
	{
		// inflate layout
		View rootView = inflate(getContext(), R.layout.chips_input, this);

		mRecyclerView = rootView.findViewById(R.id.chips_recycler);
		// attributes
		if(attrs != null)
		{
			TypedArray a = mContext.getTheme().obtainStyledAttributes(attrs, R.styleable.ChipsInput, 0, 0);

			try
			{
				// hint
				mHint = a.getString(R.styleable.ChipsInput_hint);
				mHintColor = a.getColorStateList(R.styleable.ChipsInput_hintColor);
				mTextColor = a.getColorStateList(R.styleable.ChipsInput_textColor);
				mMaxRows = a.getInteger(R.styleable.ChipsInput_maxRows, 2);
				setMaxHeight(ViewUtil.dpToPx((40 * mMaxRows) + 8));
				//setVerticalScrollBarEnabled(true);
				// chip label color
				mChipLabelColor = a.getColorStateList(R.styleable.ChipsInput_chip_labelColor);
				// chip avatar icon
				mChipHasAvatarIcon = a.getBoolean(R.styleable.ChipsInput_chip_hasAvatarIcon, true);
				// chip delete icon
				mChipDeletable = a.getBoolean(R.styleable.ChipsInput_chip_deletable, false);
				mChipDeleteIconColor = a.getColorStateList(R.styleable.ChipsInput_chip_deleteIconColor);
				int deleteIconId = a.getResourceId(R.styleable.ChipsInput_chip_deleteIcon, NONE);
				if(deleteIconId != NONE)
				{
					mChipDeleteIcon = ContextCompat.getDrawable(mContext, deleteIconId);
				}
				// chip background color
				mChipBackgroundColor = a.getColorStateList(R.styleable.ChipsInput_chip_backgroundColor);
				mChipWidthMatchParent = a.getBoolean(R.styleable.ChipsInput_chip_width_match_parent, false);

				// show chip detailed
				mShowChipDetailed = a.getBoolean(R.styleable.ChipsInput_showChipDetailed, true);
				// chip detailed text color
				mChipDetailedTextColor = a.getColorStateList(R.styleable.ChipsInput_chip_detailed_textColor);
				mChipDetailedBackgroundColor = a.getColorStateList(R.styleable.ChipsInput_chip_detailed_backgroundColor);
				mChipDetailedDeleteIconColor = a.getColorStateList(R.styleable.ChipsInput_chip_detailed_deleteIconColor);
				// filterable list
				mFilterableListBackgroundColor = a.getColorStateList(R.styleable.ChipsInput_filterable_list_backgroundColor);
				mFilterableListTextColor = a.getColorStateList(R.styleable.ChipsInput_filterable_list_textColor);
				mFilterableUseLetterTile = a.getBoolean(R.styleable.ChipsInput_filterable_list_useLetterTile, false);
				mFilterableListAlwaysShow = a.getBoolean(R.styleable.ChipsInput_filterable_list_alwaysVisible, false);

				mAllowNewChips = a.getBoolean(R.styleable.ChipsInput_allowNewChips, false);
				mChipClickNotify = a.getBoolean(R.styleable.ChipsInput_chip_clickNotify, false);
				mMaxViewsInRow = a.getInt(R.styleable.ChipsInput_maxViewsInRow, -1);
			}
			finally
			{
				a.recycle();
			}
		}

		// adapter
		mChipsAdapter = new ChipsAdapter(mContext, this, mRecyclerView);
		mChipsAdapter.getEditText().setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, final MotionEvent event)
			{
				Handler handler = new Handler();
				handler.postDelayed(new Runnable()
				{
					@Override
					public void run()
					{
						if(event.getActionMasked() == MotionEvent.ACTION_UP)
						{
							if(mFilterableListAlwaysShow)
							{
								mFilterableListView.fadeIn();
							}
						}
						else if(event.getActionMasked() == MotionEvent.ACTION_CANCEL)
						{
							mFilterableListView.fadeOut();
						}
					}
				}, 500);
				return false;
			}
		});

		mChipsLayoutManager = ChipsLayoutManager.newBuilder(mContext).setOrientation(ChipsLayoutManager.HORIZONTAL).build();
		if(mMaxViewsInRow > 0)
		{
			mChipsLayoutManager.setMaxViewsInRow(mMaxViewsInRow);
		}
		mRecyclerView.setLayoutManager(mChipsLayoutManager);
		mRecyclerView.setNestedScrollingEnabled(false);
		mRecyclerView.setAdapter(mChipsAdapter);

		// set window callback
		// will hide DetailedOpenView and hide keyboard on touch outside
		if(!this.isInEditMode())
		{
			Activity activity = ActivityUtil.scanForActivity(mContext);
			if(activity == null)
			{
				throw new ClassCastException("android.view.Context cannot be cast to android.app.Activity");
			}

			android.view.Window.Callback mCallBack = (activity).getWindow().getCallback();
			activity.getWindow().setCallback(new MyWindowCallback(mCallBack, activity));
		}
	}

	public void addChip(final ChipInterface chip)
	{
		mChipsAdapter.addChip(chip);
		if(mFilterableListView != null && !mFilterableListAlwaysShow)
		{
			mFilterableListView.fadeOut();
		}
	}

	public void addChips(List<ChipInterface> chips)
	{
		for(ChipInterface chip : chips)
		{
			addChip(chip);
		}
	}

	public void addChip(Object id, Drawable icon, String label, String info)
	{
		Chip chip = new Chip(id, icon, label, info);
		mChipsAdapter.addChip(chip);
	}

	public void addChip(Drawable icon, String label, String info)
	{
		Chip chip = new Chip(icon, label, info);
		mChipsAdapter.addChip(chip);
	}

	public void addChip(Object id, Uri iconUri, String label, String info)
	{
		Chip chip = new Chip(id, iconUri, label, info);
		mChipsAdapter.addChip(chip);
	}

	public void addChip(Uri iconUri, String label, String info)
	{
		Chip chip = new Chip(iconUri, label, info);
		mChipsAdapter.addChip(chip);
	}

	public void addChip(String label, String info)
	{
		ChipInterface chip = new Chip(label, info);
		mChipsAdapter.addChip(chip);
	}

	public void removeChip(ChipInterface chip)
	{
		mChipsAdapter.removeChip(chip);
	}

	public void removeChipById(Object id)
	{
		mChipsAdapter.removeChipById(id);
	}

	public void removeChipByLabel(String label)
	{
		mChipsAdapter.removeChipByLabel(label);
	}

	public void removeChipByInfo(String info)
	{
		mChipsAdapter.removeChipByInfo(info);
	}

	public void removeAllChips()
	{
		mChipsAdapter.removeAllChips();
	}

	public ChipView getChipView()
	{
		int padding = ViewUtil.dpToPx(4);
		ChipView chipView = new ChipView.Builder(mContext).labelColor(mChipLabelColor).hasAvatarIcon(mChipHasAvatarIcon).deletable(mChipDeletable).deleteIcon(mChipDeleteIcon).deleteIconColor(
				mChipDeleteIconColor).backgroundColor(mChipBackgroundColor).widthMatchParent(mChipWidthMatchParent).build();

		chipView.setPadding(padding, padding, padding, padding);
		return chipView;
	}

	public List<ChipInterface> getChips()
	{
		return mChipsAdapter.getChipList();
	}

	public ChipsInputEditText generateEditText()
	{
		ChipsInputEditText editText = new ChipsInputEditText(mContext);
		if(mHintColor != null)
		{
			editText.setHintTextColor(mHintColor);
		}
		if(mTextColor != null)
		{
			editText.setTextColor(mTextColor);
		}
		return editText;
	}

	public ChipsInputEditText getEditText()
	{
		return mChipsAdapter.getEditText();
	}

	public void setText(CharSequence text)
	{
		mChipsAdapter.setText(text);
	}

	public String getText()
	{
		return mChipsAdapter.getText();
	}

	public DetailedChipView getDetailedChipView(ChipInterface chip)
	{
		return new DetailedChipView.Builder(mContext).chip(chip).textColor(mChipDetailedTextColor).backgroundColor(mChipDetailedBackgroundColor).deleteIconColor(mChipDetailedDeleteIconColor).build();
	}

	public void addChipsListener(ChipsListener chipsListener)
	{
		//Don't add a listener more than once
		for(int i = 0; i < mChipsListenerList.size(); i++)
		{
			if(mChipsListenerList.get(i) == chipsListener)
			{
				return;
			}
		}
		mChipsListenerList.add(chipsListener);
		mChipsListener = chipsListener;
	}

	public void onChipAdded(ChipInterface chip, int size)
	{
		for(int i = 0; i < mChipsListenerList.size(); i++)
		{
			mChipsListenerList.get(i).onChipAdded(chip, size);
		}
	}

	public void onChipRemoved(ChipInterface chip, int size)
	{
		for(int i = 0; i < mChipsListenerList.size(); i++)
		{
			mChipsListenerList.get(i).onChipRemoved(chip, size);
		}
	}

	public void onTextChanged(CharSequence text)
	{
		if(mChipsListener != null)
		{
			for(int i = 0; i < mChipsListenerList.size(); i++)
			{
				mChipsListenerList.get(i).onTextChanged(text);
			}
			// show filterable list
			if(mFilterableListView != null)
			{
				mFilterableListView.filterList(text);
				if(!mFilterableListAlwaysShow && TextUtils.isEmpty(text))
				{
					mFilterableListView.fadeOut();
				}
				else
				{
					mFilterableListView.fadeIn();
				}
			}
		}
	}

	public void onAddNewChip(CharSequence text)
	{
		if(mChipsListener != null)
		{
			for(int i = 0; i < mChipsListenerList.size(); i++)
			{
				mChipsListenerList.get(i).onNewChip(text);
			}
		}
	}

	public void onChipClicked(ChipInterface chip)
	{
		if(mChipsListener != null)
		{
			for(int i = 0; i < mChipsListenerList.size(); i++)
			{
				mChipsListenerList.get(i).onChipClicked(chip);
			}
		}
	}

	@Override
	public void clearFocus()
	{
		super.clearFocus();
		if(mFilterableListView != null)
		{
			mFilterableListView.fadeOut();
		}
		mChipsAdapter.getEditText().clearFocus();
	}

	@Override
	public boolean requestFocus(int direction, Rect previouslyFocusedRect)
	{
		return mChipsAdapter.getEditText().requestFocus(direction, previouslyFocusedRect);
	}

	public List<? extends ChipInterface> getSelectedChipList()
	{
		return mChipsAdapter.getChipList();
	}

	public String getHint()
	{
		return mHint;
	}

	public void setHint(String mHint)
	{
		this.mHint = mHint;
	}

	public void setHintColor(ColorStateList mHintColor)
	{
		this.mHintColor = mHintColor;
	}

	public void setTextColor(ColorStateList mTextColor)
	{
		this.mTextColor = mTextColor;
	}

	public ChipsInput setMaxRows(int mMaxRows)
	{
		this.mMaxRows = mMaxRows;
		return this;
	}

	public void setChipLabelColor(ColorStateList mLabelColor)
	{
		this.mChipLabelColor = mLabelColor;
	}

	public void setChipHasAvatarIcon(boolean mHasAvatarIcon)
	{
		this.mChipHasAvatarIcon = mHasAvatarIcon;
	}

	public boolean chipHasAvatarIcon()
	{
		return mChipHasAvatarIcon;
	}

	public void setChipDeletable(boolean mDeletable)
	{
		this.mChipDeletable = mDeletable;
	}

	public void setChipAllowNew(boolean allowNew)
	{
		mAllowNewChips = allowNew;
	}

	public boolean getChipAllowNew()
	{
		return mAllowNewChips;
	}

	public void setChipDeleteIcon(Drawable mDeleteIcon)
	{
		this.mChipDeleteIcon = mDeleteIcon;
	}

	public void setChipDeleteIconColor(ColorStateList mDeleteIconColor)
	{
		this.mChipDeleteIconColor = mDeleteIconColor;
	}

	public void setChipBackgroundColor(ColorStateList mBackgroundColor)
	{
		this.mChipBackgroundColor = mBackgroundColor;
	}

	public boolean isShowChipDetailed()
	{
		return mShowChipDetailed;
	}

	public ChipsInput setShowChipDetailed(boolean mShowChipDetailed)
	{
		this.mShowChipDetailed = mShowChipDetailed;
		return this;
	}

	public boolean shouldChipClickNotify()
	{
		return mChipClickNotify;
	}

	public ChipsInput setChipClickNotify(boolean chipClickNotify)
	{
		mChipClickNotify = chipClickNotify;
		return this;
	}

	public int getmMaxViewsInRow()
	{
		return mMaxViewsInRow;
	}

	public void setMaxViewsInRow(int maxViewsInRow)
	{
		mMaxViewsInRow = maxViewsInRow;
		if(mMaxViewsInRow > 0)
		{
			mChipsLayoutManager.setMaxViewsInRow(mMaxViewsInRow);
		}
	}

	public void setChipDetailedTextColor(ColorStateList mChipDetailedTextColor)
	{
		this.mChipDetailedTextColor = mChipDetailedTextColor;
	}

	public void setChipDetailedDeleteIconColor(ColorStateList mChipDetailedDeleteIconColor)
	{
		this.mChipDetailedDeleteIconColor = mChipDetailedDeleteIconColor;
	}

	public void setChipDetailedBackgroundColor(ColorStateList mChipDetailedBackgroundColor)
	{
		this.mChipDetailedBackgroundColor = mChipDetailedBackgroundColor;
	}

	public List<? extends ChipInterface> getFilterableList()
	{
		return mChipList;
	}

	public void setFilterableList(List<? extends ChipInterface> list)
	{
		mChipList = list;
		if(mFilterableListView == null)
		{
			mFilterableListView = new FilterableListView(mContext);
			mFilterableListView.build(mChipList, this, mFilterableListBackgroundColor, mFilterableListTextColor, mFilterableUseLetterTile, getValidChipSeparators());
			mChipsAdapter.setFilterableListView(mFilterableListView);
		}
		else
		{
			mFilterableListView.setFilterableList(list);
		}
	}

	public ChipValidator getChipValidator()
	{
		return mChipValidator;
	}

	public void setChipValidator(ChipValidator mChipValidator)
	{
		this.mChipValidator = mChipValidator;
	}

	public List<Character> getValidChipSeparators()
	{
		return mValidChipSeparators;
	}

	public void setValidChipSeparators(List<Character> validChipSeparators)
	{
		mValidChipSeparators = validChipSeparators;
	}

	public FilterableListView getFilterableListView()
	{
		return mFilterableListView;
	}

	public RecyclerView getRecyclerView()
	{
		return mRecyclerView;
	}

	public interface ChipsListener
	{
		void onChipAdded(ChipInterface chip, int newSize);

		void onChipRemoved(ChipInterface chip, int newSize);

		void onTextChanged(CharSequence text);

		void onNewChip(CharSequence text);

		void onChipClicked(ChipInterface chip);
	}

	public interface ChipValidator
	{
		boolean areEquals(ChipInterface chip1, ChipInterface chip2);
	}
}
