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

public class AsuroOdometryCalibrationAction extends TemporalAction {
	private int left_trigger, right_trigger, hysteresis;


	private Formula formula_left_trigger, formula_right_trigger, formula_hysteresis;
	private Sprite sprite;

	private BluetoothDeviceService btService = ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE);

	@Override
	protected void begin () {
	}

	@Override
	protected void update(float percent) {
		try {
			left_trigger = this.formula_left_trigger.interpretInteger(sprite);
		} catch (InterpretationException interpretationException) {
			left_trigger = 0;
			Log.d(getClass().getSimpleName(), "Formula interpretation for left trigger failed.", interpretationException);
		}
		try {
			right_trigger = this.formula_right_trigger.interpretInteger(sprite);
		} catch (InterpretationException interpretationException) {
			right_trigger = 0;
			Log.d(getClass().getSimpleName(), "Formula interpretation for right trigger failed.", interpretationException);
		}
		try {
			hysteresis = this.formula_hysteresis.interpretInteger(sprite);
		} catch (InterpretationException interpretationException) {
			hysteresis = 0;
			Log.d(getClass().getSimpleName(), "Formula interpretation for hysteresis failed.", interpretationException);
		}

		if (left_trigger < 0) {
			left_trigger = 0;
		} else if (left_trigger > 1024) {
			left_trigger = 1024;
		}
		if (right_trigger < 0) {
			right_trigger = 0;
		} else if (right_trigger > 1024) {
			right_trigger = 1024;
		}

		Asuro asuro = btService.getDevice(BluetoothDevice.ASURO);
		if (asuro == null) {
			return;
		}
		asuro.configureOdometry(left_trigger, right_trigger, hysteresis);
	}

	public void setLeftTrigger(Formula trigger) {
		this.formula_left_trigger = trigger;
	}
	public void setRightTrigger(Formula trigger) { this.formula_right_trigger = trigger; }
	public void setHysteresis(Formula hysteresis) {this.formula_hysteresis = hysteresis; }

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}
}
