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

public class AsuroTurnAngleBrick extends FormulaBrick {

	private static final long serialVersionUID = 1L;

	private transient View prototypeView;
	private transient TextView editAngle;

	public AsuroTurnAngleBrick() {
		addAllowedBrickField(BrickField.ASURO_ANGLE);
	}

	public AsuroTurnAngleBrick(int angleValue) {
		initializeBrickFields(new Formula(angleValue));
	}

	public AsuroTurnAngleBrick(Formula angleFormula) {
		initializeBrickFields(angleFormula);
	}

	private void initializeBrickFields(Formula angle) {
		addAllowedBrickField(BrickField.ASURO_ANGLE);
		setFormulaWithBrickField(BrickField.ASURO_ANGLE, angle);
	}

	protected Object readResolve() {
		return this;
	}

	@Override
	public int getRequiredResources() {
		return BLUETOOTH_ASURO | getFormulaWithBrickField(BrickField.ASURO_ANGLE).getRequiredResources();
	}

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_asuro_turn_angle, null);
		TextView textSpeed = (TextView) prototypeView.findViewById(R.id.brick_asuro_turn_angle_action_angle_edit_text);
		textSpeed.setText(String.valueOf(BrickValues.ASURO_ANGLE_INITIAL));

		return prototypeView;
	}

	@Override
	public Brick clone() {
		return new AsuroTurnAngleBrick(getFormulaWithBrickField(BrickField.ASURO_ANGLE).clone());
	}

	@Override
	public void showFormulaEditorToEditFormula(View view) {
		FormulaEditorFragment.showFragment(view, this, BrickField.ASURO_ANGLE);
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}

		view = View.inflate(context, R.layout.brick_asuro_turn_angle, null);
		view = BrickViewProvider.setAlphaOnView(view, alphaValue);
		setCheckboxView(R.id.brick_asuro_turn_angle_action_checkbox);

		editAngle = (TextView) view.findViewById(R.id.brick_asuro_turn_angle_action_angle_edit_text);
		getFormulaWithBrickField(BrickField.ASURO_ANGLE).setTextFieldId(R.id
				.brick_asuro_turn_angle_action_angle_edit_text);
		getFormulaWithBrickField(BrickField.ASURO_ANGLE).refreshTextField(view);

		editAngle.setOnClickListener(this);

		return view;
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createAsuroTurnAngleAction(sprite,
				getFormulaWithBrickField(BrickField.ASURO_ANGLE)));
		return null;
	}

	@Override
	public void updateReferenceAfterMerge(Scene into, Scene from) {
	}
}
