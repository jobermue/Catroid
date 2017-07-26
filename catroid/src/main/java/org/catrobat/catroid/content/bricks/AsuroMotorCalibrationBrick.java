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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
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

public class AsuroMotorCalibrationBrick extends FormulaBrick {

	private static final long serialVersionUID = 1L;

	private transient View prototypeView;

	public AsuroMotorCalibrationBrick() {
		addAllowedBrickField(BrickField.ASURO_BIAS);
		addAllowedBrickField(BrickField.ASURO_SPEED_FACTOR);
	}

	public AsuroMotorCalibrationBrick(double biasValue, double speedFactorValue) {
		initializeBrickFields(new Formula(biasValue), new Formula(speedFactorValue));
	}

	public AsuroMotorCalibrationBrick(Formula biasValue, Formula speedFactorValue) {
		initializeBrickFields(biasValue, speedFactorValue);
	}

	private void initializeBrickFields(Formula bias, Formula speedFactor) {
		addAllowedBrickField(BrickField.ASURO_BIAS);
		addAllowedBrickField(BrickField.ASURO_SPEED_FACTOR);
		setFormulaWithBrickField(BrickField.ASURO_BIAS, bias);
		setFormulaWithBrickField(BrickField.ASURO_SPEED_FACTOR, speedFactor);
	}

	protected Object readResolve() {
		return this;
	}

	@Override
	public int getRequiredResources() {
		return BLUETOOTH_ASURO | getFormulaWithBrickField(BrickField.ASURO_BIAS).getRequiredResources() |
				getFormulaWithBrickField(BrickField.ASURO_SPEED_FACTOR).getRequiredResources();
	}

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_asuro_motor_calibration, null);

		TextView textBias = (TextView) prototypeView.findViewById(R.id.brick_asuro_motor_calibration_bias_edit_text);
		textBias.setText(String.valueOf(BrickValues.ASURO_BIAS));

		TextView textSpeedFactor = (TextView) prototypeView.findViewById(R.id
				.brick_asuro_motor_calibration_speed_edit_text);
		textSpeedFactor.setText(String.valueOf(BrickValues.ASURO_SPEED_FACTOR));

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
		view = View.inflate(context, R.layout.brick_asuro_motor_calibration, null);
		view = BrickViewProvider.setAlphaOnView(view, alphaValue);

		setCheckboxView(R.id.brick_asuro_motor_calibration_checkbox);

		TextView editSpeedFactor = (TextView) view.findViewById(R.id.brick_asuro_motor_calibration_speed_edit_text);
		getFormulaWithBrickField(BrickField.ASURO_SPEED_FACTOR).setTextFieldId(R.id
				.brick_asuro_motor_calibration_speed_edit_text);
		getFormulaWithBrickField(BrickField.ASURO_SPEED_FACTOR).refreshTextField(view);

		editSpeedFactor.setOnClickListener(this);

		TextView editBias = (TextView) view.findViewById(R.id.brick_asuro_motor_calibration_bias_edit_text);
		getFormulaWithBrickField(BrickField.ASURO_BIAS).setTextFieldId(R.id
				.brick_asuro_motor_calibration_bias_edit_text);
		getFormulaWithBrickField(BrickField.ASURO_BIAS).refreshTextField(view);

		editBias.setOnClickListener(this);

		return view;
	}

	@Override
	public Brick clone() {
		return new AsuroMotorCalibrationBrick(getFormulaWithBrickField(BrickField.ASURO_BIAS).clone(),
				getFormulaWithBrickField(BrickField.ASURO_SPEED_FACTOR).clone());
	}

	@Override
	public void showFormulaEditorToEditFormula(View view) {
		switch (view.getId()) {
			case R.id.brick_asuro_motor_calibration_bias_edit_text:
				FormulaEditorFragment.showFragment(view, this, BrickField.ASURO_BIAS);
				break;
			case R.id.brick_asuro_motor_calibration_speed_edit_text:
			default:
				FormulaEditorFragment.showFragment(view, this, BrickField.ASURO_SPEED_FACTOR);
				break;
		}
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createAsuroMotorCalibrationAction(sprite,
				getFormulaWithBrickField(BrickField.ASURO_BIAS),
				getFormulaWithBrickField(BrickField.ASURO_SPEED_FACTOR)));
		return null;
	}
}
