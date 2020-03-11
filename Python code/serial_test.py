import serial
import time

def main():
	ser = serial.Serial('/dev/ttyACM0')
	t = 0
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
