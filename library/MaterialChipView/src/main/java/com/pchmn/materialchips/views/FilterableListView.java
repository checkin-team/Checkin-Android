package com.pchmn.materialchips.views;


import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.widget.Filter;
import android.widget.RelativeLayout;

import com.pchmn.materialchips.ChipsInput;
import com.pchmn.materialchips.R;
import com.pchmn.materialchips.adapter.FilterableAdapter;
import com.pchmn.materialchips.model.ChipInterface;
import com.pchmn.materialchips.util.ViewUtil;

import java.util.List;

public class FilterableListView extends RelativeLayout
{

	private static final String TAG = FilterableListView.class.toString();
	// list
	RecyclerView mRecyclerView;
	private Context           mContext;
	private FilterableAdapter mAdapter;
	private CharSequence mLastFilter = "";
	// others
	private ChipsInput      mChipsInput;
	private List<Character> mValidChipsSeparators;

	public FilterableListView(Context context)
	{
		super(context);
		mContext = context;
		init();
	}

	private void init()
	{
		// inflate layout
		View view = inflate(getContext(), R.layout.list_filterable_view, this);

		mRecyclerView = view.findViewById(R.id.recycler_view);

		// recycler
		mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));

		// hide on first
		setVisibility(GONE);
	}


	public void build(List<? extends ChipInterface> filterableList, ChipsInput chipsInput, ColorStateList backgroundColor, ColorStateList textColor, boolean useLetterTile,
	                  List<Character> validChipsSeparators)
	{
		mChipsInput = chipsInput;
		mValidChipsSeparators = validChipsSeparators;
		// adapter
		mAdapter = new FilterableAdapter(mContext, mRecyclerView, filterableList, chipsInput, backgroundColor, textColor, useLetterTile);
		mRecyclerView.setAdapter(mAdapter);
		if(backgroundColor != null)
		{
			//			mRecyclerView.getBackground().setColorFilter(backgroundColor.getDefaultColor(), PorterDuff.Mode.SRC_ATOP);
			mRecyclerView.setBackgroundColor(backgroundColor.getDefaultColor());
		}

		// listen to change in the tree
		mChipsInput.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
		{
			private int previousHeightDiff = -1;

			@Override
			public void onGlobalLayout()
			{
				ViewGroup rootView = (ViewGroup) mChipsInput.getRootView();
				if(getParent() == null)
				{
					// size
					RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewUtil.getWindowWidth(mContext), ViewGroup.LayoutParams.MATCH_PARENT);

					layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
					layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

					if(mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
					{
						//						layoutParams.bottomMargin = ViewUtil.getNavBarHeight(mContext);
					}
					else
					{
						layoutParams.leftMargin = ViewUtil.getNavBarHeight(mContext);
						layoutParams.rightMargin = ViewUtil.getNavBarHeight(mContext);
					}

					// add view
					rootView.addView(FilterableListView.this, layoutParams);
				}
				else
				{
					Handler handler = new Handler();
					handler.postDelayed(new Runnable()
					{
						@Override
						public void run()
						{
							int  y = (int) getY();
							if(y > 0)
							{
								Rect r = new Rect();
								//r will be populated with the coordinates of your view that area still visible.
								mChipsInput.getWindowVisibleDisplayFrame(r);
								int rootViewHeight = mChipsInput.getRootView().getHeight();
								int heightDiff     = rootViewHeight - (r.bottom - r.top);
								if(heightDiff != previousHeightDiff)
								{
									int[] absPosition = new int[2];
									mChipsInput.getLocationInWindow(absPosition);
									int chipsInputHeight = mChipsInput.getHeight();
									previousHeightDiff = heightDiff;
									ViewGroup.LayoutParams layoutParams = getLayoutParams();
									int                    newHeight    = rootViewHeight - heightDiff - y + r.top;
									layoutParams.height = newHeight;
									requestLayout();
								}
							}
						}
					}, 500);
				}

				// remove the listener:
				//				if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
				//				{
				//					mChipsInput.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				//				}
				//				else
				//				{
				//					mChipsInput.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				//				}
			}

		});

		mChipsInput.addOnLayoutChangeListener(new OnLayoutChangeListener()
		{
			@Override
			public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom)
			{
				if(getVisibility() == GONE)
				{
					return;
				}

				int[] coord = new int[2];
				mChipsInput.getLocationInWindow(coord);
				MarginLayoutParams layoutParams = (MarginLayoutParams) getLayoutParams();
				layoutParams.topMargin = coord[1] + mChipsInput.getHeight();
				setLayoutParams(layoutParams);
			}
		});
	}

	private int dp2pixels(@NonNull Context context, float dp)
	{
		Resources r  = context.getResources();
		float     px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
		return (int) px;
	}

	public void setFilterableList(List<? extends ChipInterface> filterableList)
	{
		if(mAdapter.setChipList(filterableList))
		{
			filterList(mLastFilter);
		}
	}

	public void filterList(CharSequence text)
	{
		mLastFilter = text;
		//		if(TextUtils.isEmpty(text) || mValidChipsSeparators.contains(text.toString().charAt(text.length() - 1)))
		//		{
		//			fadeOut();
		//			return;
		//		}
		mAdapter.getFilter().filter(text, new Filter.FilterListener()
		{
			@Override
			public void onFilterComplete(int count)
			{
				// show if there are results
//				if(mAdapter.getItemCount() > 0)
//				{
//					fadeIn();
//				}
				//				else
				//				{
				//					fadeOut();
				//				}
			}
		});
	}

	/**
	 * Fade in
	 */
	public void fadeIn()
	{
		if(getVisibility() == VISIBLE)
		{
			return;
		}

		int[] coord = new int[2];
		mChipsInput.getLocationInWindow(coord);
		MarginLayoutParams layoutParams = (MarginLayoutParams) getLayoutParams();
		layoutParams.topMargin = coord[1] + mChipsInput.getHeight();
		setLayoutParams(layoutParams);

		AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
		anim.setDuration(200);
		startAnimation(anim);
		setVisibility(VISIBLE);
	}

	/**
	 * Fade out
	 */
	public void fadeOut()
	{
		mLastFilter = "";
		if(getVisibility() == GONE)
		{
			return;
		}

		AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);
		anim.setDuration(200);
		startAnimation(anim);
		setVisibility(GONE);
	}

	public FilterableAdapter getFilterableAdapter()
	{
		return mAdapter;
	}
}
