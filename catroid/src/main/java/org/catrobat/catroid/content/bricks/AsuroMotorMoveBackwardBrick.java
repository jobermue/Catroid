/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.content.bricks;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;
import org.catrobat.catroid.ui.fragment.SingleSeekbar;

import java.util.List;

public class AsuroMotorMoveBackwardBrick extends FormulaBrick {
	private static final long serialVersionUID = 1L;

	private transient View prototypeView;
	private transient AdapterView<?> adapterView;
	private String motor;
	private transient Motor motorEnum;
	private transient TextView editSpeed;

	private transient SingleSeekbar speedSeekbar =
			new SingleSeekbar(this, BrickField.ASURO_SPEED, R.string.asuro_motor_speed);

	public enum Motor {
		MOTOR_LEFT, MOTOR_RIGHT, MOTOR_BOTH
	}

	public AsuroMotorMoveBackwardBrick() {
		addAllowedBrickField(BrickField.ASURO_SPEED);
	}

	public AsuroMotorMoveBackwardBrick(Motor motor, int speedValue) {
		this.motorEnum = motor;
		this.motor = motorEnum.name();
		initializeBrickFields(new Formula(speedValue));
	}

	public AsuroMotorMoveBackwardBrick(Motor motor, Formula speedFormula) {
		this.motorEnum = motor;
		this.motor = motorEnum.name();

		initializeBrickFields(speedFormula);
	}

	private void initializeBrickFields(Formula speed) {
		addAllowedBrickField(BrickField.ASURO_SPEED);
		setFormulaWithBrickField(BrickField.ASURO_SPEED, speed);
	}

	protected Object readResolve() {
		if (motor != null) {
			motorEnum = Motor.valueOf(motor);
		}
		return this;
	}

	@Override
	public int getRequiredResources() {
		return BLUETOOTH_PHIRO | getFormulaWithBrickField(BrickField.ASURO_SPEED).getRequiredResources();
	}

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_asuro_motor_backward, null);
		TextView textSpeed = (TextView) prototypeView.findViewById(R.id.brick_asuro_motor_backward_action_speed_text_view);
		textSpeed.setText(String.valueOf(BrickValues.ASURO_SPEED));

		Spinner asuroProMotorSpinner = (Spinner) prototypeView.findViewById(R.id.brick_asuro_motor_backward_action_spinner);
		asuroProMotorSpinner.setFocusableInTouchMode(false);
		asuroProMotorSpinner.setFocusable(false);
		asuroProMotorSpinner.setEnabled(false);

		ArrayAdapter<CharSequence> motorAdapter = ArrayAdapter.createFromResource(context, R.array.brick_asuro_select_motor_spinner,
				android.R.layout.simple_spinner_item);
		motorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		asuroProMotorSpinner.setAdapter(motorAdapter);
		asuroProMotorSpinner.setSelection(motorEnum.ordinal());

		return prototypeView;
	}

	@Override
	public Brick clone() {
		return new AsuroMotorMoveBackwardBrick(motorEnum,
				getFormulaWithBrickField(BrickField.ASURO_SPEED).clone());
	}

	@Override
	public View getCustomView(Context context, int brickId, BaseAdapter baseAdapter) {
		return speedSeekbar.getView(context);
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}
		if (view == null) {
			alphaValue = 255;
		}

		view = View.inflate(context, R.layout.brick_asuro_motor_backward, null);
		view = getViewWithAlpha(alphaValue);
		setCheckboxView(R.id.brick_asuro_motor_backward_action_checkbox);

		final Brick brickInstance = this;
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(brickInstance, isChecked);
			}
		});

		TextView textSpeed = (TextView) view.findViewById(R.id.brick_asuro_motor_backward_action_speed_text_view);
		editSpeed = (TextView) view.findViewById(R.id.brick_asuro_motor_backward_action_speed_edit_text);
		getFormulaWithBrickField(BrickField.ASURO_SPEED).setTextFieldId(R.id.brick_asuro_motor_backward_action_speed_edit_text);
		getFormulaWithBrickField(BrickField.ASURO_SPEED).refreshTextField(view);

		textSpeed.setVisibility(View.GONE);
		editSpeed.setVisibility(View.VISIBLE);

		editSpeed.setOnClickListener(this);

		ArrayAdapter<CharSequence> motorAdapter = ArrayAdapter.createFromResource(context, R.array.brick_asuro_select_motor_spinner,
				android.R.layout.simple_spinner_item);
		motorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner motorSpinner = (Spinner) view.findViewById(R.id.brick_asuro_motor_backward_action_spinner);

		if (!(checkbox.getVisibility() == View.VISIBLE)) {
			motorSpinner.setClickable(true);
			motorSpinner.setEnabled(true);
		} else {
			motorSpinner.setClickable(false);
			motorSpinner.setEnabled(false);
		}

		motorSpinner.setAdapter(motorAdapter);
		motorSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
				motorEnum = Motor.values()[position];
				motor = motorEnum.name();
				adapterView = arg0;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		motorSpinner.setSelection(motorEnum.ordinal());

		return view;
	}

	@Override
	public void showFormulaEditorToEditFormula(View view) {
		if (isSpeedOnlyANumber()) {
			FormulaEditorFragment.showCustomFragment(view, this, BrickField.ASURO_SPEED);
		} else {
			FormulaEditorFragment.showFragment(view, this, BrickField.ASURO_SPEED);
		}
	}

	private boolean isSpeedOnlyANumber() {
		return getFormulaWithBrickField(BrickField.ASURO_SPEED).getRoot().getElementType()
				== FormulaElement.ElementType.NUMBER;
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {

		if (view != null) {

			View layout = view.findViewById(R.id.brick_asuro_motor_backward_action_layout);
			Drawable background = layout.getBackground();
			background.setAlpha(alphaValue);

			TextView textAsuroProMotorActionLabel = (TextView) view.findViewById(R.id.brick_asuro_motor_backward_action_label);
			TextView textAsuroProMotorActionSpeed = (TextView) view.findViewById(R.id.brick_asuro_motor_backward_action_speed);
			TextView textAsuroProMotorActionPercent = (TextView) view.findViewById(R.id.brick_asuro_motor_backward_action_percent);
			TextView textAsuroProMotorActionLabelSpeedView = (TextView) view
					.findViewById(R.id.brick_asuro_motor_backward_action_speed_text_view);
			TextView editSpeed = (TextView) view.findViewById(R.id.brick_asuro_motor_backward_action_speed_edit_text);

			textAsuroProMotorActionLabel.setTextColor(textAsuroProMotorActionLabel.getTextColors().withAlpha(alphaValue));
			textAsuroProMotorActionSpeed.setTextColor(textAsuroProMotorActionSpeed.getTextColors().withAlpha(alphaValue));
			textAsuroProMotorActionPercent.setTextColor(textAsuroProMotorActionPercent.getTextColors().withAlpha(alphaValue));
			textAsuroProMotorActionLabelSpeedView.setTextColor(textAsuroProMotorActionLabelSpeedView.getTextColors().withAlpha(
					alphaValue));
			Spinner motorSpinner = (Spinner) view.findViewById(R.id.brick_asuro_motor_backward_action_spinner);
			ColorStateList color = textAsuroProMotorActionLabelSpeedView.getTextColors().withAlpha(alphaValue);
			motorSpinner.getBackground().setAlpha(alphaValue);
			if (adapterView != null) {
				((TextView) adapterView.getChildAt(0)).setTextColor(color);
			}
			editSpeed.setTextColor(editSpeed.getTextColors().withAlpha(alphaValue));
			editSpeed.getBackground().setAlpha(alphaValue);

			this.alphaValue = alphaValue;
		}

		return view;
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createAsuroMotorMoveBackwardActionAction(sprite, motorEnum, getFormulaWithBrickField(BrickField.ASURO_SPEED)));
		return null;
	}

	@Override
	public void updateReferenceAfterMerge(Scene into, Scene from) {
	}
}
