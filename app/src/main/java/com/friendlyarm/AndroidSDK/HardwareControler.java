package com.friendlyarm.AndroidSDK;
import android.util.Log;

public class HardwareControler
{
	/* File */
	static public native int open(String devName, int flags);
	static public native int write(int fd, byte[] data);
	static public native int read(int fd, byte[] buf, int len);
	static public native int select(int fd, int sec, int usec);
	static public native void close(int fd);
	static public native int ioctlWithIntValue(int fd, int cmd, int value);

	/* Serial Port */
	static public native int openSerialPort(String devName, long baud, int dataBits, int stopBits );
	static public native int openSerialPortEx( String devName
			, long baud
			, int dataBits
			, int stopBits
			, String parityBit
			, String flowCtrl
	);

	/* LED */
	static public native int setLedState( int ledID, int ledState );

	/* PWM */
	static public native int PWMPlay(int frequency);
	static public native int PWMStop();
	static public native int PWMPlayEx(int gpioPin, int frequency);
	static public native int PWMStopEx(int gpioPin);

	/* ADC */
	static public native int readADC();
	static public native int readADCWithChannel(int channel);
	static public native int[] readADCWithChannels(int[] channels);

	/* I2C */
	static public native int setI2CSlave(int fd, int slave);
	static public native int setI2CTimeout(int fd, int timeout);
	static public native int setI2CRetries(int fd, int retries);
	static public native int I2CReadByteData(int fd, int pos, int wait_us);
	static public native int I2CReadByte(int fd, int wait_us);
	static public native int I2CWriteByteData(int fd, int pos, byte byteData, int wait_us);
	static public native int I2CWriteByte(int fd, byte byteData, int wait_us);

	/* Discard */ static public native int readByteFromI2C(int fd, int pos, int wait_ms);
	/* Discard */ static public native int writeByteToI2C(int fd, int pos, byte byteData, int wait_ms);
	/* Discard */ static public native int writeByteToI2C2(int fd, byte byteData, int wait_ms);

	/* SPI */
	static public native int setSPIWriteBitsPerWord( int spi_fd, int bits );
	static public native int setSPIReadBitsPerWord( int spi_fd, int bits );
	static public native int setSPIBitOrder( int spi_fd, int order);
	static public native int setSPIClockDivider( int spi_fd, int divider);
	static public native int setSPIMaxSpeed( int spi_fd, int spi_speed);
	static public native int setSPIDataMode( int spi_fd, int mode);
	static public native int SPItransferOneByte( int spi_fd, byte byteData, int spi_delay, int spi_speed, int spi_bits);
	static public native int SPItransferBytes(int spi_fd, byte[] writeData, byte[] readBuff, int spi_delay, int spi_speed, int spi_bits);
	static public native int writeBytesToSPI(int spi_fd, byte[] writeData, int spi_delay, int spi_speed, int spi_bits);
	static public native int readBytesFromSPI(int spi_fd, byte[] readBuff, int spi_delay, int spi_speed, int spi_bits);

	/* GPIO */
	static public native int exportGPIOPin(int pin);
	static public native int unexportGPIOPin(int pin);
	//GPIOEnum.LOW or GPIOEnum.HIGH
	static public native int setGPIOValue(int pin, int value);
	static public native int getGPIOValue(int pin);
	//GPIOEnum.IN or GPIOEnum.OUT
	static public native int setGPIODirection(int pin, int direction);
	static public native int getGPIODirection(int pin);

	/* OldInterface: for EEPROM */
	static public native int openI2CDevice();
	static public native int writeByteDataToI2C(int fd, int pos, byte byteData);
	static public native int readByteDataFromI2C(int fd, int pos);

	static public native int getBoardType();
	/* getBoardType return value: */
	public static final int S3C6410_COMMON = 6410;
	public static final int S5PV210_COMMON = 210;
	public static final int S5P4412_COMMON = 4412;
	public static final int S5P4418_BASE = 4418;
	public static final int NanoPi2 = S5P4418_BASE+0;
	public static final int NanoPC_T2 = S5P4418_BASE+1;
	public static final int NanoPi_S2 = S5P4418_BASE+2;
	public static final int Smart4418 = S5P4418_BASE+3;
	public static final int NanoPi2_Fire = S5P4418_BASE+4;
	public static final int NanoPi_M2 = S5P4418_BASE+5;
	public static final int NanoPi_M2A = S5P4418_BASE+7;
	public static final int Smart4418SDK = S5P4418_BASE+0x103;
	public static final int S5P4418_MAX = Smart4418SDK;
	public static final int S5P6818_BASE = 6818;
	public static final int NanoPC_T3 = S5P6818_BASE+1;
	public static final int NanoPi_S3 = S5P6818_BASE+2;
	public static final int Smart6818 = S5P6818_BASE+3;
	public static final int NanoPi_M3 = S5P6818_BASE+7;
	public static final int S5P6818_MAX = NanoPi_M3;

	static public boolean isS5P4418Board() {
		int boardtype = getBoardType();
		return boardtype>=S5P4418_BASE && boardtype<=S5P4418_MAX;
	}

	static public boolean isS5P6818Board() {
		int boardtype = getBoardType();
		return boardtype>=S5P6818_BASE && boardtype<=S5P6818_MAX;
	}
	static {
		try {
			System.loadLibrary("friendlyarm-hardware");
		} catch (UnsatisfiedLinkError e) {
			Log.d("HardwareControler", "libfriendlyarm-hardware library not found!");
		}
	}
}
