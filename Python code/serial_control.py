import serial
import time

port = '/dev/ttyACM0'

def main():
	ser = serial.Serial(port, 9600, timeout = 5)
	time.sleep(3)

	while(t < 10):
		ser.write(1) #forward
		time.sleep(0.3)
		ser.write(0) #stop
		time.sleep(0.3)
		ser.write(2) #back
		time.sleep(0.3)
		ser.write(0) #stop
		time.sleep(0.3)
		t = t + 1

	ser.close()

if __name__ == "__main__":
	main()
