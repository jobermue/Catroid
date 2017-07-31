/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;
import org.catrobat.catroid.ui.fragment.SingleSeekbar;

import java.util.List;

public class AsuroMotorsMoveStepsBrick extends FormulaBrick {

	private static final long serialVersionUID = 1L;

	private transient View prototypeView;

	private transient SingleSeekbar speedSeekbar =
			new SingleSeekbar(this, BrickField.ASURO_SPEED, R.string.asuro_motor_speed);

	public AsuroMotorsMoveStepsBrick() {
		addAllowedBrickField(BrickField.ASURO_STEPS_LEFT);
		addAllowedBrickField(BrickField.ASURO_STEPS_RIGHT);
		addAllowedBrickField(BrickField.ASURO_SPEED);
	}

	public AsuroMotorsMoveStepsBrick(int steps_left, int steps_right, int speed) {
		initializeBrickFields(new Formula(steps_left), new Formula(steps_right), new Formula(speed));
	}

	public AsuroMotorsMoveStepsBrick(Formula steps_left, Formula steps_right, Formula speed) {
		initializeBrickFields(steps_left, steps_right, speed);
	}

	private void initializeBrickFields(Formula steps_left, Formula steps_right, Formula speed) {
		addAllowedBrickField(BrickField.ASURO_STEPS_LEFT);
		addAllowedBrickField(BrickField.ASURO_STEPS_RIGHT);
		addAllowedBrickField(BrickField.ASURO_SPEED);
		setFormulaWithBrickField(BrickField.ASURO_STEPS_LEFT, steps_left);
		setFormulaWithBrickField(BrickField.ASURO_STEPS_RIGHT, steps_right);
		setFormulaWithBrickField(BrickField.ASURO_SPEED, speed);
	}

	protected Object readResolve() {
		return this;
	}

	@Override
	public int getRequiredResources() {
		return BLUETOOTH_ASURO | getFormulaWithBrickField(BrickField.ASURO_STEPS_LEFT).getRequiredResources() |
				getFormulaWithBrickField(BrickField.ASURO_STEPS_RIGHT).getRequiredResources() |
				getFormulaWithBrickField(BrickField.ASURO_SPEED).getRequiredResources();
	}

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_asuro_motors_steps, null);

		TextView textLeft = (TextView) prototypeView.findViewById(R.id.brick_asuro_motors_steps_left_edit_text);
		textLeft.setText(String.valueOf(BrickValues.ASURO_STEPS_INITIAL_LEFT));

		TextView textRight = (TextView) prototypeView.findViewById(R.id.brick_asuro_motors_steps_right_edit_text);
		textRight.setText(String.valueOf(BrickValues.ASURO_STEPS_INITIAL_RIGHT));

		TextView textSpeed = (TextView) prototypeView.findViewById(R.id.brick_asuro_motors_steps_speed_edit_text);
		textSpeed.setText(String.valueOf(BrickValues.ASURO_SPEED));

		return prototypeView;
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}
		if (view == null) {
			alphaValue = 255;
		}
		view = View.inflate(context, R.layout.brick_asuro_motors_steps, null);
		view = BrickViewProvider.setAlphaOnView(view, alphaValue);

		setCheckboxView(R.id.brick_asuro_motors_steps_checkbox);

		TextView editLeftSteps = (TextView) view.findViewById(R.id.brick_asuro_motors_steps_left_edit_text);
		getFormulaWithBrickField(BrickField.ASURO_STEPS_LEFT).setTextFieldId(R.id
				.brick_asuro_motors_steps_left_edit_text);
		getFormulaWithBrickField(BrickField.ASURO_STEPS_LEFT).refreshTextField(view);
		editLeftSteps.setOnClickListener(this);

		TextView editRightSteps = (TextView) view.findViewById(R.id.brick_asuro_motors_steps_right_edit_text);
		getFormulaWithBrickField(BrickField.ASURO_STEPS_RIGHT).setTextFieldId(R.id
				.brick_asuro_motors_steps_right_edit_text);
		getFormulaWithBrickField(BrickField.ASURO_STEPS_RIGHT).refreshTextField(view);
		editRightSteps.setOnClickListener(this);

		TextView editSpeed = (TextView) view.findViewById(R.id.brick_asuro_motors_steps_speed_edit_text);
		getFormulaWithBrickField(BrickField.ASURO_SPEED).setTextFieldId(R.id
				.brick_asuro_motors_steps_speed_edit_text);
		getFormulaWithBrickField(BrickField.ASURO_SPEED).refreshTextField(view);
		editSpeed.setOnClickListener(this);

		return view;
	}

	@Override
	public View getCustomView(Context context, int brickId, BaseAdapter baseAdapter) {
		return speedSeekbar.getView(context);
	}

	@Override
	public Brick clone() {
		return new AsuroMotorsMoveStepsBrick(
				getFormulaWithBrickField(BrickField.ASURO_STEPS_LEFT).clone(),
				getFormulaWithBrickField(BrickField.ASURO_STEPS_RIGHT).clone(),
				getFormulaWithBrickField(BrickField.ASURO_SPEED).clone());
	}

	@Override
	public void showFormulaEditorToEditFormula(View view) {
		switch (view.getId()) {
			case R.id.brick_asuro_motors_steps_left_edit_text:
				FormulaEditorFragment.showFragment(view, this, BrickField.ASURO_STEPS_LEFT);
				break;
			case R.id.brick_asuro_motors_steps_right_edit_text:
				FormulaEditorFragment.showFragment(view, this, BrickField.ASURO_STEPS_RIGHT);
				break;
			case R.id.brick_asuro_motors_steps_speed_edit_text:
			default:
				if (isSpeedOnlyANumber()) {
					FormulaEditorFragment.showCustomFragment(view, this, BrickField.ASURO_SPEED);
				} else {
					FormulaEditorFragment.showFragment(view, this, BrickField.ASURO_SPEED);
				}
				break;
		}
	}

	private boolean isSpeedOnlyANumber() {
		return getFormulaWithBrickField(BrickField.ASURO_SPEED).getRoot().getElementType()
				== FormulaElement.ElementType.NUMBER;
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createAsuroMotorsMoveStepsAction(sprite,
				getFormulaWithBrickField(BrickField.ASURO_STEPS_LEFT),
				getFormulaWithBrickField(BrickField.ASURO_STEPS_RIGHT),
				getFormulaWithBrickField(BrickField.ASURO_SPEED)));
		return null;
	}
}
