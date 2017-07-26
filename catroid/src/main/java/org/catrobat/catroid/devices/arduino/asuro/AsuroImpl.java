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

import org.catrobat.catroid.bluetooth.base.BluetoothConnection;
import org.catrobat.catroid.bluetooth.base.BluetoothDevice;
import org.catrobat.catroid.devices.arduino.ArduinoImpl;
import org.catrobat.catroid.devices.arduino.ArduinoListener;
import org.catrobat.catroid.formulaeditor.Sensors;

import java.util.UUID;

public class AsuroImpl extends ArduinoImpl implements Asuro {

	private static final UUID ASURO_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
//	private static final String TAG = AsuroImpl.class.getSimpleName();

//	private static final int MIN_VALUE = 0;
//	private static final int MAX_VALUE = 255;

	/* Note: pins are numbered according to Arduino pin numbering */
	private static final int PIN_STATUS_LED_RED = 2; // ATmega328: 4
	private static final int PIN_STATUS_LED_GREEN = 8; // ATmega328: 14

	private static final int PIN_FRONT_LED = 6; // ATmega328: 12
	
	private static final int PIN_LEFT_MOTOR_SPEED = 9; // ATmega328: 15
	private static final int PIN_LEFT_MOTOR_FORWARD = 5; // ATmega328: 6
	private static final int PIN_LEFT_MOTOR_BACKWARD = 4; // ATmega328: 11

	private static final int PIN_RIGHT_MOTOR_SPEED = 10; // ATmega328: 16
	private static final int PIN_RIGHT_MOTOR_FORWARD = 13; // ATmega328: 19
	private static final int PIN_RIGHT_MOTOR_BACKWARD = 12; // ATmega328: 18

	private static final int PIN_BUMPERS_ENABLE = 3;
	private static final int PIN_WHEEL_ENCODER_LEDS = 7;

	private static final int PIN_SENSOR_BUMPERS = 4;
	private static final int PIN_SENSOR_BOTTOM_LEFT = 3;
	private static final int PIN_SENSOR_BOTTOM_RIGHT = 2;
	private static final int PIN_SENSOR_SIDE_LEFT = 1;
	private static final int PIN_SENSOR_SIDE_RIGHT = 0;

	private boolean isInitialized = false;

	private double correctionFactorLeftMotor, correctionFactorRightMotor; /* value between 0 and 1 */

	@Override
	public void moveLeftMotorForward(int speedInPercent) {
		setAnalogArduinoPin(PIN_LEFT_MOTOR_SPEED, (int) (speedInPercent * correctionFactorLeftMotor));
		setDigitalArduinoPin(PIN_LEFT_MOTOR_FORWARD, 1);
		setDigitalArduinoPin(PIN_LEFT_MOTOR_BACKWARD, 0);
		//setAnalogArduinoPin(PIN_LEFT_MOTOR_SPEED, percentToSpeed(speedInPercent)); conversion not necessary, at the moment done by Arduino code (might change in the future)
	}

	@Override
	public void moveLeftMotorBackward(int speedInPercent) {
		setAnalogArduinoPin(PIN_LEFT_MOTOR_SPEED, (int) (speedInPercent * correctionFactorLeftMotor));
		setDigitalArduinoPin(PIN_LEFT_MOTOR_FORWARD, 0);
		setDigitalArduinoPin(PIN_LEFT_MOTOR_BACKWARD, 1);
	}

	@Override
	public void moveRightMotorForward(int speedInPercent) {
		setAnalogArduinoPin(PIN_RIGHT_MOTOR_SPEED, (int) (speedInPercent * correctionFactorRightMotor));
		setDigitalArduinoPin(PIN_RIGHT_MOTOR_FORWARD, 1);
		setDigitalArduinoPin(PIN_RIGHT_MOTOR_BACKWARD, 0);
	}

	@Override
	public void moveRightMotorBackward(int speedInPercent) {
		setAnalogArduinoPin(PIN_RIGHT_MOTOR_SPEED, (int) (speedInPercent * correctionFactorRightMotor));
		setDigitalArduinoPin(PIN_RIGHT_MOTOR_FORWARD, 0);
		setDigitalArduinoPin(PIN_RIGHT_MOTOR_BACKWARD, 1);
	}

	@Override
	public void stopLeftMotor() {
		setAnalogArduinoPin(PIN_LEFT_MOTOR_SPEED, 0);
		setDigitalArduinoPin(PIN_LEFT_MOTOR_FORWARD, 0);
		setDigitalArduinoPin(PIN_LEFT_MOTOR_BACKWARD, 0);
	}

	@Override
	public void stopRightMotor() {
		setAnalogArduinoPin(PIN_RIGHT_MOTOR_SPEED, 0);
		setDigitalArduinoPin(PIN_RIGHT_MOTOR_FORWARD, 0);
		setDigitalArduinoPin(PIN_RIGHT_MOTOR_BACKWARD, 0);
	}

	@Override
	public void moveForward(int speedInPercent) {
		setDigitalArduinoPin(PIN_LEFT_MOTOR_BACKWARD, 0);
		setDigitalArduinoPin(PIN_RIGHT_MOTOR_BACKWARD, 0);
		setDigitalArduinoPin(PIN_LEFT_MOTOR_FORWARD, 1);
		setDigitalArduinoPin(PIN_RIGHT_MOTOR_FORWARD, 1);
		setAnalogArduinoPin(PIN_LEFT_MOTOR_SPEED, (int) (speedInPercent * correctionFactorLeftMotor));
		setAnalogArduinoPin(PIN_RIGHT_MOTOR_SPEED, (int) (speedInPercent * correctionFactorRightMotor));
	}

	@Override
	public void moveBackward(int speedInPercent) {
		setDigitalArduinoPin(PIN_LEFT_MOTOR_FORWARD, 0);
		setDigitalArduinoPin(PIN_RIGHT_MOTOR_FORWARD, 0);
		setDigitalArduinoPin(PIN_LEFT_MOTOR_BACKWARD, 1);
		setDigitalArduinoPin(PIN_RIGHT_MOTOR_BACKWARD, 1);
		setAnalogArduinoPin(PIN_LEFT_MOTOR_SPEED, (int) (speedInPercent * correctionFactorLeftMotor));
		setAnalogArduinoPin(PIN_RIGHT_MOTOR_SPEED, (int) (speedInPercent * correctionFactorRightMotor));
	}

	@Override
	public void stopAllMovements() {
		setDigitalArduinoPin(PIN_LEFT_MOTOR_BACKWARD, 0);
		setDigitalArduinoPin(PIN_RIGHT_MOTOR_BACKWARD, 0);
		setDigitalArduinoPin(PIN_LEFT_MOTOR_FORWARD, 0);
		setDigitalArduinoPin(PIN_RIGHT_MOTOR_FORWARD, 0);
		setAnalogArduinoPin(PIN_LEFT_MOTOR_SPEED, 0);
		setAnalogArduinoPin(PIN_RIGHT_MOTOR_SPEED, 0);
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
	public void setMotorCalibration(double leftRightBias, double speed_correction) {
		if (leftRightBias > 0) { /* left motor faster */
			correctionFactorLeftMotor = (1.0 - leftRightBias) * speed_correction;
			correctionFactorRightMotor = speed_correction;
		} else { /* right motor faster */
			correctionFactorLeftMotor = speed_correction;
			correctionFactorRightMotor = (1.0 + leftRightBias) * speed_correction;
		}
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

	/* begin BluetoothDevice */
	@Override
	public String getName() {
		return "ASURO";
	}

	@Override
	public Class<? extends BluetoothDevice> getDeviceType() {
		return BluetoothDevice.ASURO;
	}

//	void setConnection(BluetoothConnection connection);

	@Override
	public void disconnect() {
		super.disconnect();
		isInitialized = false;
	}

//	boolean isAlive();

	@Override
	public UUID getBluetoothDeviceUUID() { return ASURO_UUID; }
	/* end BluetoothDevice */

	/* begin StageResourceInterface */
	@Override
	public void initialise() {
		super.initialise();

		if (isInitialized) {
			return;
		}
		isInitialized = true;

		/* enable bumpers */
		setDigitalArduinoPin(PIN_BUMPERS_ENABLE, 1);

		/* enable wheel encoder LEDs (odometry) */
		setDigitalArduinoPin(PIN_WHEEL_ENCODER_LEDS, 1);

		setMotorCalibration(0, 1);
	}

	@Override
	public void start() {
		super.start();
		if (!isInitialized) {
			initialise();
		}
	}

	@Override
	public void pause() {
		stopAllMovements();
//		super.reportSensorData(false);
		super.pause();
	}

	@Override
	public void destroy() {
		resetPins();
		super.destroy();
	}
	/* end StageResourceInterface */

	private void resetPins() {
		stopAllMovements();
		setStatusLEDColor(0, 0);
		setFrontLED(false);
//		setLeftBackLED(false);
//		setRightBackLED(false);
	}
}
