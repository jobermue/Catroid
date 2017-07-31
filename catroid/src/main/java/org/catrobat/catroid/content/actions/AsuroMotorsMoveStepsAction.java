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
package org.catrobat.catroid.content.actions;

import android.util.Log;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.bluetooth.base.BluetoothDevice;
import org.catrobat.catroid.bluetooth.base.BluetoothDeviceService;
import org.catrobat.catroid.common.CatroidService;
import org.catrobat.catroid.common.ServiceProvider;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.devices.arduino.asuro.Asuro;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;

public class AsuroMotorsMoveStepsAction extends TemporalAction {
	private static final int MAX_SPEED = 100;
	private static final int POWER_DOWN_RAMP_DEGREES = 20;
	private int left_steps, right_steps, speed;


	private Formula formula_left_steps, formula_right_steps, formula_speed;
	private Sprite sprite;

	private BluetoothDeviceService btService = ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE);

	@Override
	protected void begin () {
		/*try {
			left_steps = this.formula_left_steps.interpretInteger(sprite);
		} catch (InterpretationException interpretationException) {
			left_steps = 0;
			Log.d(getClass().getSimpleName(), "Formula interpretation for left steps failed.", interpretationException);
		}
		try {
			right_steps = this.formula_right_steps.interpretInteger(sprite);
		} catch (InterpretationException interpretationException) {
			right_steps = 0;
			Log.d(getClass().getSimpleName(), "Formula interpretation for right steps failed.", interpretationException);
		}
		try {
			speed = this.formula_speed.interpretInteger(sprite);
		} catch (InterpretationException interpretationException) {
			speed = 0;
			Log.d(getClass().getSimpleName(), "Formula interpretation for speed failed.", interpretationException);
		}

		int left_dir = 1;
		if (left_steps < 0) {
			left_dir = -1;
			left_steps = -left_steps;
		}
		int right_dir = 1;
		if (right_steps < 0) {
			right_dir = -1;
			right_steps = -right_steps;
		}

		Asuro asuro = btService.getDevice(BluetoothDevice.ASURO);
		if (asuro == null) {
			return;
		}
		asuro.moveMotorsStepsSpeed(left_steps, right_steps, left_dir, right_dir, speed);*/
	}

	@Override
	protected void update(float percent) {
		try {
			left_steps = this.formula_left_steps.interpretInteger(sprite);
		} catch (InterpretationException interpretationException) {
			left_steps = 0;
			Log.d(getClass().getSimpleName(), "Formula interpretation for left steps failed.", interpretationException);
		}
		try {
			right_steps = this.formula_right_steps.interpretInteger(sprite);
		} catch (InterpretationException interpretationException) {
			right_steps = 0;
			Log.d(getClass().getSimpleName(), "Formula interpretation for right steps failed.", interpretationException);
		}
		try {
			speed = this.formula_speed.interpretInteger(sprite);
		} catch (InterpretationException interpretationException) {
			speed = 0;
			Log.d(getClass().getSimpleName(), "Formula interpretation for speed failed.", interpretationException);
		}

		int left_dir = 1;
		if (left_steps < 0) {
			left_dir = -1;
			left_steps = -left_steps;
		}
		int right_dir = 1;
		if (right_steps < 0) {
			right_dir = -1;
			right_steps = -right_steps;
		}

		Asuro asuro = btService.getDevice(BluetoothDevice.ASURO);
		if (asuro == null) {
			return;
		}
		asuro.moveMotorsStepsSpeed(left_steps, right_steps, left_dir, right_dir, speed);
	}

	public void setLeftSteps(Formula steps) {
		this.formula_left_steps = steps;
	}
	public void setRightSteps(Formula steps) { this.formula_right_steps = steps; }
	public void setSpeed(Formula speed) {this.formula_speed = speed; }

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}
}
