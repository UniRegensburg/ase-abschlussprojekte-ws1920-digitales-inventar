package com.example.digitalesinventar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import static androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_SWIPE;
import static androidx.recyclerview.widget.ItemTouchHelper.LEFT;
import static androidx.recyclerview.widget.ItemTouchHelper.RIGHT;

enum ButtonsState {
	GONE,
	LEFT_VISIBLE,
	RIGHT_VISIBLE
}

class SwipeController extends ItemTouchHelper.Callback {

	private boolean swipeBack;
	private ButtonsState buttonShowedState = ButtonsState.GONE;
	private RectF buttonInstance = null;
	private RecyclerView.ViewHolder currentItemViewHolder = null;
	private SwipeControllerActions buttonsActions = null;
	private static final float buttonWidth = 200;
	private Context context;

	public SwipeController(Context context, SwipeControllerActions buttonsActions) {
		this.buttonsActions = buttonsActions;
		this.context = context;
	}

	@Override
	public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
		return makeMovementFlags(0, LEFT | RIGHT);
	}

	@Override
	public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
		return false;
	}

	@Override
	public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

	}

	@Override
	public int convertToAbsoluteDirection(int flags, int layoutDirection) {
		if (swipeBack) {
			swipeBack = buttonShowedState != ButtonsState.GONE;
			return 0;
		}
		return super.convertToAbsoluteDirection(flags, layoutDirection);
	}

	@Override
	public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
		if (actionState == ACTION_STATE_SWIPE) {
			if (buttonShowedState != ButtonsState.GONE) {
				if (buttonShowedState == ButtonsState.LEFT_VISIBLE) dX = Math.max(dX, buttonWidth);
				if (buttonShowedState == ButtonsState.RIGHT_VISIBLE) dX = Math.min(dX, -buttonWidth);
				super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
			}
			else {
				setTouchListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
			}
		}

		if (buttonShowedState == ButtonsState.GONE) {
			super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
		}
		currentItemViewHolder = viewHolder;
	}

	@SuppressLint("ClickableViewAccessibility")
	private void setTouchListener(final Canvas c, final RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder, final float dX, final float dY, final int actionState, final boolean isCurrentlyActive) {
		recyclerView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				swipeBack = event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP;
				if (swipeBack) {
					if (dX < -buttonWidth) buttonShowedState = ButtonsState.RIGHT_VISIBLE;
					else if (dX > buttonWidth) buttonShowedState  = ButtonsState.LEFT_VISIBLE;

					if (buttonShowedState != ButtonsState.GONE) {
						setTouchDownListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
						setItemsClickable(recyclerView, false);
					}
				}
				return false;
			}
		});
	}

	@SuppressLint("ClickableViewAccessibility")
	private void setTouchDownListener(final Canvas c, final RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder, final float dX, final float dY, final int actionState, final boolean isCurrentlyActive) {
		recyclerView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					setTouchUpListener(c, recyclerView, viewHolder, dX, dY, actionState, false);
				}
				return false;
			}
		});
	}

	@SuppressLint("ClickableViewAccessibility")
	private void setTouchUpListener(final Canvas c, final RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder, final float dX, final float dY, final int actionState, final boolean isCurrentlyActive) {
		recyclerView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					SwipeController.super.onChildDraw(c, recyclerView, viewHolder, 0F, dY, actionState, isCurrentlyActive);
					recyclerView.setOnTouchListener(new View.OnTouchListener() {
						@Override
						public boolean onTouch(View v, MotionEvent event) {
							return false;
						}

					});
					setItemsClickable(recyclerView, true);
					swipeBack = false;

					if (buttonsActions != null && buttonInstance != null && buttonInstance.contains(event.getX(), event.getY())) {
						if (buttonShowedState == ButtonsState.LEFT_VISIBLE) {
							buttonsActions.onLeftClicked(viewHolder.getAdapterPosition());
						}
						else if (buttonShowedState == ButtonsState.RIGHT_VISIBLE) {
							buttonsActions.onRightClicked(viewHolder.getAdapterPosition());
						}
					}
					buttonShowedState = ButtonsState.GONE;
					currentItemViewHolder = null;
				}
				return false;
			}
		});
	}

	private void setItemsClickable(RecyclerView recyclerView, boolean isClickable) {
		for (int i = 0; i < recyclerView.getChildCount(); ++i) {
			recyclerView.getChildAt(i).setClickable(isClickable);
			recyclerView.getChildAt(i).setLongClickable(isClickable);
		}
	}

	private void drawButtons(Canvas c, RecyclerView.ViewHolder viewHolder) {
		float buttonWidthWithoutPadding = buttonWidth - 20;

		View itemView = viewHolder.itemView;
		Paint p = new Paint();

		//left button
		RectF leftButton = new RectF(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + buttonWidthWithoutPadding, itemView.getBottom());
		p.setColor(0xf03700B3);
		c.drawRect(leftButton, p);
		drawIcon(c, leftButton, p, R.drawable.edit_white30px);

		//right button
		RectF rightButton = new RectF(itemView.getRight() - buttonWidthWithoutPadding, itemView.getTop(), itemView.getRight(), itemView.getBottom());
		p.setColor(0xf0B00020);
		c.drawRect(rightButton, p);
		drawIcon(c, rightButton, p, R.drawable.delete_white30px);

		buttonInstance = null;
		if (buttonShowedState == ButtonsState.LEFT_VISIBLE) {
			buttonInstance = leftButton;
		}
		else if (buttonShowedState == ButtonsState.RIGHT_VISIBLE) {
			buttonInstance = rightButton;
		}
	}

	public void onDraw(Canvas c) {
		try {
				TextView name = currentItemViewHolder.itemView.findViewById(R.id.item_name1);
				if (currentItemViewHolder != null && !name.getText().equals("Hobby") && !name.getText().equals("Einrichtung") && !name.getText().equals("Kleidung")) {
					drawButtons(c, currentItemViewHolder);
				}
			} catch (NullPointerException e) {
		}
	}

	private void drawIcon (Canvas c, RectF button, Paint p, int iconDrawable){
		Bitmap icon = BitmapFactory.decodeResource(context.getResources(), iconDrawable);
		c.drawBitmap(icon, button.left + (button.width()/2) - (icon.getWidth()/2), button.top + (button.height()/2) - (icon.getHeight()/2), p);
	}
}
