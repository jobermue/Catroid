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

public class AsuroOdometryCalibrationBrick extends FormulaBrick {

	private static final long serialVersionUID = 1L;

	private transient View prototypeView;

	private transient SingleSeekbar hysteresisSeekbar =
			new SingleSeekbar(this, BrickField.ASURO_HYSTERESIS, R.string.asuro_odometry_hysteresis);

	public AsuroOdometryCalibrationBrick() {
		addAllowedBrickField(BrickField.ASURO_TRIGGER_LEFT);
		addAllowedBrickField(BrickField.ASURO_TRIGGER_RIGHT);
		addAllowedBrickField(BrickField.ASURO_HYSTERESIS);
	}

	public AsuroOdometryCalibrationBrick(int trigger_left, int trigger_right, int hysteresis) {
		initializeBrickFields(new Formula(trigger_left), new Formula(trigger_right), new Formula(hysteresis));
	}

	public AsuroOdometryCalibrationBrick(Formula trigger_left, Formula trigger_right, Formula hysteresis) {
		initializeBrickFields(trigger_left, trigger_right, hysteresis);
	}

	private void initializeBrickFields(Formula trigger_left, Formula trigger_right, Formula hysteresis) {
		addAllowedBrickField(BrickField.ASURO_TRIGGER_LEFT);
		addAllowedBrickField(BrickField.ASURO_TRIGGER_RIGHT);
		addAllowedBrickField(BrickField.ASURO_HYSTERESIS);
		setFormulaWithBrickField(BrickField.ASURO_TRIGGER_LEFT, trigger_left);
		setFormulaWithBrickField(BrickField.ASURO_TRIGGER_RIGHT, trigger_right);
		setFormulaWithBrickField(BrickField.ASURO_HYSTERESIS, hysteresis);
	}

	protected Object readResolve() {
		return this;
	}

	@Override
	public int getRequiredResources() {
		return BLUETOOTH_ASURO | getFormulaWithBrickField(BrickField.ASURO_TRIGGER_LEFT).getRequiredResources() |
				getFormulaWithBrickField(BrickField.ASURO_TRIGGER_RIGHT).getRequiredResources() |
				getFormulaWithBrickField(BrickField.ASURO_HYSTERESIS).getRequiredResources();
	}

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_asuro_odometry_calibration, null);

		TextView textLeft = (TextView) prototypeView.findViewById(R.id.brick_asuro_odometry_calibration_left_edit_text);
		textLeft.setText(String.valueOf(BrickValues.ASURO_TRIGGER_INITIAL_LEFT));

		TextView textRight = (TextView) prototypeView.findViewById(R.id.brick_asuro_odometry_calibration_right_edit_text);
		textRight.setText(String.valueOf(BrickValues.ASURO_TRIGGER_INITIAL_RIGHT));

		TextView textSpeed = (TextView) prototypeView.findViewById(R.id
				.brick_asuro_odometry_calibration_hysteresis_edit_text);
		textSpeed.setText(String.valueOf(BrickValues.ASURO_HYSTERESIS_INITIAL));

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
		view = View.inflate(context, R.layout.brick_asuro_odometry_calibration, null);
		view = BrickViewProvider.setAlphaOnView(view, alphaValue);

		setCheckboxView(R.id.brick_asuro_odometry_calibration_checkbox);

		TextView editLeftSteps = (TextView) view.findViewById(R.id.brick_asuro_odometry_calibration_left_edit_text);
		getFormulaWithBrickField(BrickField.ASURO_TRIGGER_LEFT).setTextFieldId(R.id
				.brick_asuro_odometry_calibration_left_edit_text);
		getFormulaWithBrickField(BrickField.ASURO_TRIGGER_LEFT).refreshTextField(view);
		editLeftSteps.setOnClickListener(this);

		TextView editRightSteps = (TextView) view.findViewById(R.id.brick_asuro_odometry_calibration_right_edit_text);
		getFormulaWithBrickField(BrickField.ASURO_TRIGGER_RIGHT).setTextFieldId(R.id
				.brick_asuro_odometry_calibration_right_edit_text);
		getFormulaWithBrickField(BrickField.ASURO_TRIGGER_RIGHT).refreshTextField(view);
		editRightSteps.setOnClickListener(this);

		TextView editSpeed = (TextView) view.findViewById(R.id.brick_asuro_odometry_calibration_hysteresis_edit_text);
		getFormulaWithBrickField(BrickField.ASURO_HYSTERESIS).setTextFieldId(R.id
				.brick_asuro_odometry_calibration_hysteresis_edit_text);
		getFormulaWithBrickField(BrickField.ASURO_HYSTERESIS).refreshTextField(view);
		editSpeed.setOnClickListener(this);

		return view;
	}

	@Override
	public View getCustomView(Context context, int brickId, BaseAdapter baseAdapter) {
		return hysteresisSeekbar.getView(context);
	}

	@Override
	public Brick clone() {
		return new AsuroOdometryCalibrationBrick(
				getFormulaWithBrickField(BrickField.ASURO_TRIGGER_LEFT).clone(),
				getFormulaWithBrickField(BrickField.ASURO_TRIGGER_RIGHT).clone(),
				getFormulaWithBrickField(BrickField.ASURO_HYSTERESIS).clone());
	}

	@Override
	public void showFormulaEditorToEditFormula(View view) {
		switch (view.getId()) {
			case R.id.brick_asuro_odometry_calibration_left_edit_text:
				FormulaEditorFragment.showFragment(view, this, BrickField.ASURO_TRIGGER_LEFT);
				break;
			case R.id.brick_asuro_odometry_calibration_right_edit_text:
				FormulaEditorFragment.showFragment(view, this, BrickField.ASURO_TRIGGER_RIGHT);
				break;
			case R.id.brick_asuro_odometry_calibration_hysteresis_edit_text:
			default:
				if (isOnlyANumber(BrickField.ASURO_HYSTERESIS)) {
					FormulaEditorFragment.showCustomFragment(view, this, BrickField.ASURO_HYSTERESIS);
				} else {
					FormulaEditorFragment.showFragment(view, this, BrickField.ASURO_HYSTERESIS);
				}
				break;
		}
	}

	private boolean isOnlyANumber(BrickField field) {
		return getFormulaWithBrickField(field).getRoot().getElementType()
				== FormulaElement.ElementType.NUMBER;
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createAsuroOdometryCalibrationAction(sprite,
				getFormulaWithBrickField(BrickField.ASURO_TRIGGER_LEFT),
				getFormulaWithBrickField(BrickField.ASURO_TRIGGER_RIGHT),
				getFormulaWithBrickField(BrickField.ASURO_HYSTERESIS)));
		return null;
	}
}
