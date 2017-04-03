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
package org.catrobat.catroid.devices.arduino.asuro;

import org.catrobat.catroid.devices.arduino.ArduinoListener;

import name.antonsmirnov.firmata.IFirmata;


class AsuroListener extends ArduinoListener implements IFirmata.Listener {

	private static final String TAG = AsuroListener.class.getSimpleName();

	public int getBumperSensor() { return analogPinValue[AsuroImpl.PIN_SENSOR_BUMPERS]; }

	public int getSideLeftSensor() {
		return analogPinValue[AsuroImpl.PIN_SENSOR_BOTTOM_LEFT];
	}

	public int getSideRightSensor() {
		return analogPinValue[AsuroImpl.PIN_SENSOR_BOTTOM_RIGHT];
	}

	public int getBottomLeftSensor() {
		return analogPinValue[AsuroImpl.PIN_SENSOR_SIDE_LEFT];
	}

	public int getBottomRightSensor() {
		return analogPinValue[AsuroImpl.PIN_SENSOR_SIDE_RIGHT];
	}

}
