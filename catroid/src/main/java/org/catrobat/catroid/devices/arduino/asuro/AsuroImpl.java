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

import android.util.Log;

import org.catrobat.catroid.bluetooth.base.BluetoothConnection;
import org.catrobat.catroid.bluetooth.base.BluetoothDevice;
import org.catrobat.catroid.devices.arduino.ArduinoImpl;
import org.catrobat.catroid.devices.arduino.ArduinoListener;
import org.catrobat.catroid.formulaeditor.Sensors;

import java.io.IOException;
import java.util.UUID;

import name.antonsmirnov.firmata.Firmata;
import name.antonsmirnov.firmata.message.AnalogMessage;
import name.antonsmirnov.firmata.message.DigitalMessage;
import name.antonsmirnov.firmata.message.Message;
import name.antonsmirnov.firmata.message.ReportAnalogPinMessage;
import name.antonsmirnov.firmata.message.ReportDigitalPortMessage;
import name.antonsmirnov.firmata.message.ReportFirmwareVersionMessage;
import name.antonsmirnov.firmata.message.SetPinModeMessage;
import name.antonsmirnov.firmata.serial.ISerial;
import name.antonsmirnov.firmata.serial.SerialException;
import name.antonsmirnov.firmata.serial.StreamingSerialAdapter;

public class AsuroImpl extends ArduinoImpl implements Asuro {

	private static final String TAG = AsuroImpl.class.getSimpleName();

	private static final int MIN_VALUE = 0;
	private static final int MAX_VALUE = 255;

	/* Note: pins are numbered according to Arduino pin numbering */
	private static final int PIN_STATUS_LED_RED = 2; //4
	private static final int PIN_STATUS_LED_GREEN = 8; //14

	private static final int PIN_FRONT_LED = 6; //12

	/* TODO: find out pins of back LEDs */
//	private static final int PIN_BACK_LED_LEFT = 7;
//	private static final int PIN_BACK_LED_RIGHT = 8;
	
	private static final int PIN_LEFT_MOTOR_SPEED = 9; //15
	private static final int PIN_LEFT_MOTOR_FORWARD = 5; //6
	private static final int PIN_LEFT_MOTOR_BACKWARD = 4; //11

	private static final int PIN_RIGHT_MOTOR_SPEED = 10; //16
	private static final int PIN_RIGHT_MOTOR_FORWARD = 13; //18
	private static final int PIN_RIGHT_MOTOR_BACKWARD = 12; //19

	/* TODO: add pins for bumpers */
	private static final int PIN_SENSOR_BUMPERS = 4;
	private static final int PIN_SENSOR_BOTTOM_LEFT = 3;
	private static final int PIN_SENSOR_BOTTOM_RIGHT = 2;
	private static final int PIN_SENSOR_SIDE_LEFT = 1;
	private static final int PIN_SENSOR_SIDE_RIGHT = 0;

	@Override
	public void moveLeftMotorForward(int speedInPercent) {
		setDigitalArduinoPin(PIN_LEFT_MOTOR_FORWARD, 1);
		setDigitalArduinoPin(PIN_LEFT_MOTOR_BACKWARD, 0);
		//setAnalogArduinoPin(PIN_LEFT_MOTOR_SPEED, percentToSpeed(speedInPercent)); conversion not necessary, at the moment done by Arduino code (might change in the future)
		setAnalogArduinoPin(PIN_LEFT_MOTOR_SPEED, speedInPercent);
	}

	@Override
	public void moveLeftMotorBackward(int speedInPercent) {
		setDigitalArduinoPin(PIN_LEFT_MOTOR_FORWARD, 0);
		setDigitalArduinoPin(PIN_LEFT_MOTOR_BACKWARD, 1);
		setAnalogArduinoPin(PIN_LEFT_MOTOR_SPEED, speedInPercent);
	}

	@Override
	public void moveRightMotorForward(int speedInPercent) {
		setAnalogArduinoPin(PIN_RIGHT_MOTOR_SPEED, speedInPercent);
		setDigitalArduinoPin(PIN_RIGHT_MOTOR_FORWARD, 1);
		setDigitalArduinoPin(PIN_RIGHT_MOTOR_BACKWARD, 0);
	}

	@Override
	public void moveRightMotorBackward(int speedInPercent) {
		setAnalogArduinoPin(PIN_RIGHT_MOTOR_SPEED, speedInPercent);
		setDigitalArduinoPin(PIN_RIGHT_MOTOR_FORWARD, 0);
		setDigitalArduinoPin(PIN_RIGHT_MOTOR_BACKWARD, 1);
	}

	@Override
	public void stopLeftMotor() {
		moveLeftMotorForward(0);
	}

	@Override
	public void stopRightMotor() {
		moveRightMotorForward(0);
	}

	@Override
	public void stopAllMovements() {
		stopLeftMotor();
		stopRightMotor();
	}

	@Override
	public void setStatusLEDColor(int red, int green) { //color is binary (0 or 1)
		setDigitalArduinoPin(PIN_STATUS_LED_RED, red);
		setDigitalArduinoPin(PIN_STATUS_LED_GREEN, green);
	}

	@Override
	public void setFrontLED(boolean on) {
		if (on) {
			setDigitalArduinoPin(PIN_FRONT_LED, 1);
		} else {
			setDigitalArduinoPin(PIN_FRONT_LED, 0);
		}
	}

	@Override
	public void setLeftBackLED(boolean on) {
		//TODO
	}

	@Override
	public void setRightBackLED(boolean on) {
		//TODO
	}

	@Override
	public int getSensorValue(Sensors sensor) {
		switch (sensor) {
			case ASURO_BUMPERS:
				return (int) super.getAnalogArduinoPin(PIN_SENSOR_BUMPERS);
			case ASURO_BOTTOM_LEFT:
				return (int) super.getAnalogArduinoPin(PIN_SENSOR_BOTTOM_LEFT);
			case ASURO_BOTTOM_RIGHT:
				return (int) super.getAnalogArduinoPin(PIN_SENSOR_BOTTOM_RIGHT);
			case ASURO_SIDE_LEFT:
				return (int) super.getAnalogArduinoPin(PIN_SENSOR_SIDE_LEFT);
			case ASURO_SIDE_RIGHT:
				return (int) super.getAnalogArduinoPin(PIN_SENSOR_SIDE_RIGHT);
		}

		return 0;
	}

	private int percentToSpeed(int percent) {
		if (percent <= 0) {
			return MIN_VALUE;
		}
		if (percent >= 100) {
			return MAX_VALUE;
		}

		return (int) (percent * 2.55);
	}

	private int checkRGBValue(int rgbValue) {
		if (rgbValue > MAX_VALUE) {
			return MAX_VALUE;
		}

		if (rgbValue < MIN_VALUE) {
			return MIN_VALUE;
		}

		return rgbValue;
	}

	private void resetPins() {
		stopAllMovements();
		setStatusLEDColor(0, 0);
		setFrontLED(false);
		setLeftBackLED(false);
		setRightBackLED(false);
	}

	@Override
	public void pause() {
		stopAllMovements();
		super.reportSensorData(false);
		super.pause();
	}

	@Override
	public void destroy() {
		resetPins();
		super.destroy();
	}
}
