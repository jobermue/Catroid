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
import org.catrobat.catroid.formulaeditor.Sensors;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
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

public class AsuroImpl implements Asuro {

	private static final UUID ASURO_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
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

	private static final int MIN_PWM_PIN = 2;
	private static final int MAX_PWM_PIN = 13;

	private static final int MIN_PWM_PIN_GROUP_1 = 3;
	private static final int MAX_PWM_PIN_GROUP_1 = 3;
	private static final int MIN_PWM_PIN_GROUP_2 = 5;
	private static final int MAX_PWM_PIN_GROUP_2 = 6;
	private static final int MIN_PWM_PIN_GROUP_3 = 9;
	private static final int MAX_PWM_PIN_GROUP_3 = 11;

	public static final int PIN_SENSOR_BUMPERS = 4;
	public static final int PIN_SENSOR_BOTTOM_LEFT = 3;
	public static final int PIN_SENSOR_BOTTOM_RIGHT = 2;
	public static final int PIN_SENSOR_SIDE_LEFT = 1;
	public static final int PIN_SENSOR_SIDE_RIGHT = 0;

	private static final int MIN_SENSOR_PIN = 0;
	private static final int MAX_SENSOR_PIN = 5;

	private BluetoothConnection btConnection;
	private Firmata firmata;
	private boolean isInitialized = false;
	private boolean isReportingSensorData = false;

	private AsuroListener asuroListener;

	@Override
	public void moveLeftMotorForward(int speedInPercent) {
		sendAnalogFirmataMessage(PIN_LEFT_MOTOR_SPEED, percentToSpeed(speedInPercent));
		setDigitalArduinoPin(PIN_LEFT_MOTOR_FORWARD, 1);
		setDigitalArduinoPin(PIN_LEFT_MOTOR_BACKWARD, 0);
	}

	@Override
	public void moveLeftMotorBackward(int speedInPercent) {
		sendAnalogFirmataMessage(PIN_LEFT_MOTOR_SPEED, percentToSpeed(speedInPercent));
		setDigitalArduinoPin(PIN_LEFT_MOTOR_FORWARD, 0);
		setDigitalArduinoPin(PIN_LEFT_MOTOR_BACKWARD, 1);
	}

	@Override
	public void moveRightMotorForward(int speedInPercent) {
		sendAnalogFirmataMessage(PIN_RIGHT_MOTOR_SPEED, percentToSpeed(speedInPercent));
		setDigitalArduinoPin(PIN_RIGHT_MOTOR_FORWARD, 1);
		setDigitalArduinoPin(PIN_RIGHT_MOTOR_BACKWARD, 0);
	}

	@Override
	public void moveRightMotorBackward(int speedInPercent) {
		sendAnalogFirmataMessage(PIN_RIGHT_MOTOR_SPEED, percentToSpeed(speedInPercent));
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
	public void setStatusLEDColor(int red, int green) {
		red = checkRGBValue(red);
		green = checkRGBValue(green);

		sendFirmataMessage(new AnalogMessage(PIN_STATUS_LED_RED, red));
		sendFirmataMessage(new AnalogMessage(PIN_STATUS_LED_GREEN, green));
	}

	@Override
	public void setFrontLED(boolean on) {
		if (on) {
			sendFirmataMessage(new AnalogMessage(PIN_FRONT_LED, MAX_VALUE));
		} else {
			sendFirmataMessage(new AnalogMessage(PIN_FRONT_LED, MIN_VALUE));
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

	@Override
	public String getName() {
		return "Asuro";
	}

	@Override
	public Class<? extends BluetoothDevice> getDeviceType() {
		return ASURO;
	}

	@Override
	public void setConnection(BluetoothConnection connection) {
		this.btConnection = connection;
	}

	@Override
	public void disconnect() {

		if (firmata == null) {
			return;
		}

		try {
			resetPins();
			this.reportSensorData(false);
			firmata.clearListeners();
			firmata.getSerial().stop();
			isInitialized = false;
			firmata = null;
		} catch (SerialException e) {
			Log.d(TAG, "Error stop Asuro serial");
		}
	}

	@Override
	public boolean isAlive() {
		if (firmata == null) {
			return false;
		}

		try {
			firmata.send(new ReportFirmwareVersionMessage());
			return true;
		} catch (SerialException e) {
			return false;
		}
	}

	public void reportFirmwareVersion() {
		if (firmata == null) {
			return;
		}

		try {
			firmata.send(new ReportFirmwareVersionMessage());
		} catch (SerialException e) {
			Log.d(TAG, "Firmata Serial error, cannot send message.");
		}
	}

	@Override
	public int getSensorValue(Sensors sensor) {
		switch (sensor) {
			case ASURO_BUMPERS:
				return asuroListener.getBumperSensor();
			case ASURO_BOTTOM_LEFT:
				return asuroListener.getBottomLeftSensor();
			case ASURO_BOTTOM_RIGHT:
				return asuroListener.getBottomRightSensor();
			case ASURO_SIDE_LEFT:
				return asuroListener.getSideLeftSensor();
			case ASURO_SIDE_RIGHT:
				return asuroListener.getSideRightSensor();
		}

		return 0;
	}

	@Override
	public UUID getBluetoothDeviceUUID() {
		return ASURO_UUID;
	}

	@Override
	public void initialise() {
		if (isInitialized) {
			return;
		}

		try {
			tryInitialize();
			isInitialized = true;
		} catch (SerialException e) {
			Log.d(TAG, "Error starting firmata serials");
		} catch (IOException e) {
			Log.d(TAG, "Error opening streams");
		}
	}

	private void tryInitialize() throws IOException, SerialException {
		ISerial serial = new StreamingSerialAdapter(btConnection.getInputStream(), btConnection.getOutputStream());

		firmata = new Firmata(serial);

		asuroListener = new AsuroListener();
		firmata.addListener(asuroListener);

		firmata.getSerial().start();

		for (int pin = MIN_PWM_PIN_GROUP_1; pin <= MAX_PWM_PIN_GROUP_1; ++pin) {
			sendFirmataMessage(new SetPinModeMessage(pin, SetPinModeMessage.PIN_MODE.PWM.getMode()));
		}
		for (int pin = MIN_PWM_PIN_GROUP_2; pin <= MAX_PWM_PIN_GROUP_2; ++pin) {
			sendFirmataMessage(new SetPinModeMessage(pin, SetPinModeMessage.PIN_MODE.PWM.getMode()));
		}
		for (int pin = MIN_PWM_PIN_GROUP_3; pin <= MAX_PWM_PIN_GROUP_3; ++pin) {
			sendFirmataMessage(new SetPinModeMessage(pin, SetPinModeMessage.PIN_MODE.PWM.getMode()));
		}

		reportSensorData(true);

		// get status of digital ports
		sendFirmataMessage(new ReportDigitalPortMessage(0, true));
		sendFirmataMessage(new ReportDigitalPortMessage(1, true));
	}

	private void reportSensorData(boolean report) {
		if (isReportingSensorData == report) {
			return;
		}

		isReportingSensorData = report;

		for (int pin = MIN_SENSOR_PIN; pin <= MAX_SENSOR_PIN; ++pin) {
			// sendFirmataMessage(new SetPinModeMessage(? maybe 54 ?, SetPinModeMessage.PIN_MODE.ANALOG.getMode())); // --> not needed
			sendFirmataMessage(new ReportAnalogPinMessage(pin, report));
		}
	}

	private void resetPins() {
		stopAllMovements();
		setStatusLEDColor(0, 0);
		setFrontLED(false);
		setLeftBackLED(false);
		setRightBackLED(false);
	}

	@Override
	public void start() {
		if (!isInitialized) {
			initialise();
		}

		reportSensorData(true);
	}

	@Override
	public void pause() {
		stopAllMovements();
		reportSensorData(false);
	}

	@Override
	public void destroy() {
		resetPins();
	}

	public void setDigitalArduinoPin(int digitalPinNumber, int pinValue) {
		int digitalPort = 0;
		int PinNumberOfPort;
		int PortValue;

		if (digitalPinNumber < 8) {
			digitalPort = 0;
			PinNumberOfPort = digitalPinNumber;
		} else {
			digitalPort = 1;
			PinNumberOfPort = digitalPinNumber - 8;
		}

		PortValue = asuroListener.getuCPortValue(digitalPort);
		if (pinValue > 0) { // set pin
			PortValue = PortValue | (1 << PinNumberOfPort);
			asuroListener.setPortValue(digitalPinNumber, 1);
		} else { // clear pin
			PortValue = PortValue & ~(1 << PinNumberOfPort);
			asuroListener.setPortValue(digitalPinNumber, 0);
		}
		sendDigitalFirmataMessage(digitalPort, digitalPinNumber, PortValue);
		asuroListener.setuCPortValue(digitalPort, PortValue);
	}

	private void sendAnalogFirmataMessage(int pin, int value) {
		sendFirmataMessage(new AnalogMessage(pin, value));
	}

	private void sendDigitalFirmataMessage(int port, int pin, int value) {
		sendFirmataMessage(new SetPinModeMessage(pin, SetPinModeMessage.PIN_MODE.OUTPUT.getMode()));
		sendFirmataMessage(new DigitalMessage(port, value));
	}

	private void sendFirmataMessage(Message message) {
		if (firmata == null) {
			return;
		}

		try {
			firmata.send(message);
		} catch (SerialException e) {
			Log.d(TAG, "Firmata Serial error, cannot send message.");
		}
	}
}
