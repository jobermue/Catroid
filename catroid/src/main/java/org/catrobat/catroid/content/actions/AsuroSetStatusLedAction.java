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

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.bluetooth.base.BluetoothDevice;
import org.catrobat.catroid.bluetooth.base.BluetoothDeviceService;
import org.catrobat.catroid.common.CatroidService;
import org.catrobat.catroid.common.ServiceProvider;
import org.catrobat.catroid.content.bricks.AsuroStatusLedBrick.LedStatus;
import org.catrobat.catroid.devices.arduino.asuro.Asuro;

public class AsuroSetStatusLedAction extends TemporalAction {

	private LedStatus ledStatusEnum;
	private BluetoothDeviceService btService = ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE);

	@Override
	protected void update(float percent) {

		Asuro asuro = btService.getDevice(BluetoothDevice.ASURO);
		if (asuro == null) {
			return;
		}

//		int ledStatus = 0x00;

		switch (ledStatusEnum) {
			case LED_OFF:
//				ledStatus = 0x00;
				asuro.setStatusLEDColor(0, 0);
				break;
			case LED_GREEN:
//				ledStatus = 0x01;
				asuro.setStatusLEDColor(0, 1);
				break;
			case LED_RED:
//				ledStatus = 0x02;
				asuro.setStatusLEDColor(1, 0);
				break;
			case LED_ORANGE:
//				ledStatus = 0x03;
				asuro.setStatusLEDColor(1, 1);
				break;
//			case LED_GREEN_FLASHING:
//				ledStatus = 0x04;
//				break;
//			case LED_RED_FLASHING:
//				ledStatus = 0x05;
//				break;
//			case LED_ORANGE_FLASHING:
//				ledStatus = 0x06;
//				break;
//			case LED_GREEN_PULSE:
//				ledStatus = 0x07;
//				break;
//			case LED_RED_PULSE:
//				ledStatus = 0x08;
//				break;
//			case LED_ORANGE_PULSE:
//				ledStatus = 0x09;
//				break;
		}

//		asuro.setLed(ledStatus);
	}

	public void setLedStatusEnum(LedStatus ledStatusEnum) {
		this.ledStatusEnum = ledStatusEnum;
	}
}
