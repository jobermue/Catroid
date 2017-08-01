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

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;

import java.util.List;

public class AsuroOdometryDebugBrick extends BrickBaseType implements OnItemSelectedListener {
	private static final long serialVersionUID = 1L;
	private transient OdometryDebugStatus statusEnum;
	private String odoStatus;

	public enum OdometryDebugStatus {
		LED_OFF, LED_ON
	}

	public AsuroOdometryDebugBrick(OdometryDebugStatus odometryDebugStatus) {
		this.statusEnum = odometryDebugStatus;
		this.odoStatus = statusEnum.name();
	}

	protected Object readResolve() {
		if (odoStatus != null) {
			statusEnum = OdometryDebugStatus.valueOf(odoStatus);
		}
		return this;
	}

	@Override
	public int getRequiredResources() {
		return BLUETOOTH_ASURO;
	}

	@Override
	public View getPrototypeView(Context context) {
		View prototypeView = View.inflate(context, R.layout.brick_asuro_set_odometry_debug, null);

		Spinner dbgStatusSpinner = (Spinner) prototypeView.findViewById(R.id.brick_asuro_odometry_debug_spinner);
		dbgStatusSpinner.setFocusableInTouchMode(false);
		dbgStatusSpinner.setFocusable(false);

		ArrayAdapter<CharSequence> dbgStatusAdapter = ArrayAdapter.createFromResource(context,
				R.array.asuro_odometry_debug_chooser, android.R.layout.simple_spinner_item);
		dbgStatusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		dbgStatusSpinner.setAdapter(dbgStatusAdapter);
		dbgStatusSpinner.setSelection(statusEnum.ordinal());
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
		view = View.inflate(context, R.layout.brick_asuro_set_odometry_debug, null);
		view = BrickViewProvider.setAlphaOnView(view, alphaValue);

		setCheckboxView(R.id.brick_asuro_odometry_debug_checkbox);

		ArrayAdapter<CharSequence> dbgStatusAdapter = ArrayAdapter.createFromResource(context,
				R.array.asuro_odometry_debug_chooser, android.R.layout.simple_spinner_item);
		dbgStatusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		Spinner dbgStatusSpinner = (Spinner) view.findViewById(R.id.brick_asuro_odometry_debug_spinner);
		dbgStatusSpinner.setOnItemSelectedListener(this);

		if (!(checkbox.getVisibility() == View.VISIBLE)) {
			dbgStatusSpinner.setClickable(true);
			dbgStatusSpinner.setEnabled(true);
		} else {
			dbgStatusSpinner.setClickable(false);
			dbgStatusSpinner.setEnabled(false);
		}

		dbgStatusSpinner.setAdapter(dbgStatusAdapter);
		if (statusEnum == null) {
			readResolve();
		}
		dbgStatusSpinner.setSelection(statusEnum.ordinal());
		return view;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		statusEnum = OdometryDebugStatus.values()[position];
		odoStatus = statusEnum.name();
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createAsuroSetOdometryDebugAction(statusEnum));
		return null;
	}
}
