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
import org.catrobat.catroid.content.bricks.AsuroMotorMoveForwardBrick.Motor;
import org.catrobat.catroid.devices.arduino.asuro.Asuro;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;

public class AsuroMotorCalibrationAction extends TemporalAction {
	private static final double MIN_BIAS = -1;
	private static final double MAX_BIAS = 1;
	private static final double MIN_FACTOR = 0;
	private static final double MAX_FACTOR = 1;

	private Formula bias, factor;
	private Sprite sprite;

	private BluetoothDeviceService btService = ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE);

	@Override
	protected void update(float percent) {
		double biasValue, factorValue;
		try {
			biasValue = bias.interpretDouble(sprite);
		} catch (InterpretationException interpretationException) {
			biasValue = 0;
			Log.d(getClass().getSimpleName(), "Formula interpretation for left/right bias failed.",
					interpretationException);
		}
		if (biasValue < MIN_BIAS) {
			biasValue = MIN_BIAS;
		} else if (biasValue > MAX_BIAS) {
			biasValue = MAX_BIAS;
		}

		try {
			factorValue = factor.interpretDouble(sprite);
		} catch (InterpretationException interpretationException) {
			factorValue = 0;
			Log.d(getClass().getSimpleName(), "Formula interpretation for speed correction failed.",
					interpretationException);
		}
		if (factorValue < MIN_FACTOR) {
			factorValue = MIN_FACTOR;
		} else if (factorValue > MAX_FACTOR) {
			factorValue = MAX_FACTOR;
		}

		Asuro asuro = btService.getDevice(BluetoothDevice.ASURO);
		if (asuro == null) {
			return;
		}

		asuro.setMotorCalibration(biasValue, factorValue);
	}

	public void setBias(Formula bias) {
		this.bias = bias;
	}

	public void setFactor(Formula factor) {
		this.factor = factor;
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}
}
