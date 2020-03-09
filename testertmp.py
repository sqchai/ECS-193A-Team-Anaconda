import cv2
import imutils
from imutils import contours
import math
import numpy as np 
import random
import sys

'''#dir is in radian, which is absolute
class Movement:
	def __init__ (self, dir=0,input_x=0, input_y=0):
		self.dir = dir
		self.x = input_x
		self.y = input_y

#dir is also in radian, but relative to current dir. It serves as an adjustment to the current movement
class Command:
	def __init__ (self, dir=0,input_x=0, input_y=0):
		self.dir = dir
		self.x = input_x
		self.y = input_y'''


inchperpixel = None
width = 5.5
dest = (100,100)

draw = False # true if the mouse is pressed. 
a,b = -1,-1  
mousePath = np.empty((0,2), int)
allPaths = []
simPaths = []

def calc_angle(p1,p2):
	slope = (p1[1]-p2[1])/(p1[0]-p2[0])
	angle = math.atan(slope)
	if(p2[1]<p1[1]):
		angle+=math.pi
	return angle

'''def apply_move(move):
	temp=[]

#returns slope and starting pt of the newly produced stroke
def get_diff(prevFrame,frame):
	##place holder, some random slope& starting pt
	slope = 1
	x=0
	y=0
	move = Movement(math.atan(slope),x,y)
	return move

def analyze_path(prevFrame,frame):
	temp=[]

def get_next_move(stroke, path):
	outBound = False
	if(stroke.dir<math.pi/2):
		if((stroke.x>path[1][0]) or (stroke.y>path[1][1])):
			outBound = True
	elif(stroke.dir<math.pi):
		if(stroke.x < path[1][0] or stroke.y > path[1][1]):
			outBound = True
	elif(stroke.dir<(math.pi*2/3)):
		if(stroke.x < path[1][0] or stroke.y < path[1][1]):
			outBound = True
	else:
		if(stroke.x > path[1][0] or stroke.y < path[1][1]):
			outBound = True

	if (outBound):
		path=path[1:]

	ideal_angle = calc_angle(path[0],path[1])
	cmd = Command(stroke.dir-ideal_angle, stroke.x, stroke.y)
	print("currcmd"+str(cmd.dir)+" "+str(cmd.x)+" "+str(cmd.y))
	return cmd

	#temp=[]'''

'''def car_start(j):
	cap = cv2.VideoCapture(0)
	prevFrame = cap.read()
	outBound = False
	currpath = allPaths[j]
	i=0
	while len(currpath)>1 and i<len(simPaths[j])-1:
		frame = cap.read()

		#simulation_stroke = get_diff(prevFrame,frame)

		simulation_stroke = Movement(calc_angle(simPaths[j][i],simPaths[j][i+1]),simPaths[j][i][0],simPaths[j][i][1])
		nextcmd = get_next_move(simulation_stroke, currpath)
		i = i+1

		#car.apply_move(nextMove)'''

def ret_move(p1,p2):
	angle = calc_angle(p1,p2)
	print (angle)

def calc_dist(x1,y1,x2,y2):
	dist = math.sqrt((x1-x2)**2 + (y1-y2)**2)
	return dist

def car_start(i):
	threshold = 10
	cap = cv2.VideoCapture(0)
	Frame = cap.read()
	thresholdtmp = sys.maxsize
	target = [0,0]
	for k in range(0,len(simPaths[i])):
		for j in range(0,len(allPaths[i])):
			if(calc_dist(allPaths[i][j][0],allPaths[i][j][0],simPaths[i][k][0],simPaths[i][k][1])<thresholdtmp):
				thresholdtmp = calc_dist(allPaths[i][j][0],allPaths[i][j][0],simPaths[i][k][0],simPaths[i][k][1])
				target = [i,j]
		if(thresholdtmp<threshold):
			target = [target[0],target[1]+1]
		ret_move(allPaths[target[0]][target[1]],simPaths[i][k])






# mouse callback function  
def draw_circle(event,x,y,flags,param):  
	global a,b,draw,mousePath 
	if(event == cv2.EVENT_LBUTTONDOWN): 
		if not draw:
			mousePath = np.empty((0,2), int)
			allPaths.append(mousePath)
			allPaths[len(allPaths)-1] = np.append(allPaths[len(allPaths)-1], np.array([[x,y]]), axis=0) 
		draw = True  
		a,b = x,y
		#cv2.circle(canvas,(x,y),5,(0,0,255),-1)
	elif (event == cv2.EVENT_MOUSEMOVE):  
		if draw == True:  
			#cv2.circle(canvas,(x,y),5,(0,0,255),-1)  
			allPaths[len(allPaths)-1] = np.append(allPaths[len(allPaths)-1], np.array([[x,y]]), axis=0) 
	elif(event == cv2.EVENT_LBUTTONUP):  
		draw = False
		#cv2.circle(canvas,(x,y),5,(0,0,255),-1)  
		allPaths[len(allPaths)-1] = np.append(allPaths[len(allPaths)-1], np.array([[x,y]]), axis=0)

def get_path():
	faceCascade = cv2.CascadeClassifier(cv2.data.haarcascades+'haarcascade_frontalcatface.xml')
	cap = cv2.VideoCapture(0)
	while(True):
		ret, frame = cap.read()
		faces = faceCascade.detectMultiScale(frame, 1.3, 5)
		img = frame
		cv2.rectangle(img, (dest[0], dest[1]), (dest[0]+10, dest[1]+10), (0, 0, 255), 2)
		for (x,y,w,h) in faces:
			inchperpixel = width/w
			cv2.rectangle(img, (x, y), (x+w, y+h), (0, 0, 255), 2)
			cv2.putText(img,'Cat'+str(w)+"ipp"+str(inchperpixel),(x,y-7), 3, 1.2, (0, 255, 0), 2, cv2.LINE_AA)
			todest = math.sqrt(pow(((x+w/2)-dest[0]),2)+pow(((y+h/2)-dest[1]),2))*inchperpixel
			cv2.putText(img,'actual distance is'+str(todest)+"inches",(x,y-100), 3, 1.2, (0, 255, 0), 2, cv2.LINE_AA)
		'''gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
		gray = cv2.GaussianBlur(gray, (7, 7), 0)
	
		edged = cv2.Canny(gray, 50, 100)
		edged = cv2.dilate(edged, None, iterations=1)
		edged = cv2.erode(edged, None, iterations=1)
	
		cnts = cv2.findContours(edged.copy(), cv2.RETR_EXTERNAL,cv2.CHAIN_APPROX_SIMPLE)
		cnts = imutils.grab_contours(cnts)
		for c in cnts:
		   if cv2.contourArea(c) < 1500: 
			   continue
		   (x, y, w, h) = cv2.boundingRect(c) 
		   cv2.rectangle(img, (x, y), (x+w, y+h), (0, 255, 0), 2)
		   if inchperpixel!=None:
			   cv2.putText(img,"actualwidth is "+str(inchperpixel*w),(x,y-7), 3, 1.2, (0, 255, 0), 2, cv2.LINE_AA)'''
		for path in allPaths:
			for k in range(1,len(path)):
				cv2.line(img,(path[k-1][0],path[k-1][1]),(path[k][0],path[k][1]),(0,0,255),2)
	
		cv2.imshow('frame',img)
	
		if cv2.waitKey(5) & 0xFF == ord('q'):
			print(allPaths);
			break
	
	cap.release()
	cv2.destroyAllWindows()


cv2.namedWindow('frame') 
cv2.setMouseCallback('frame',draw_circle) 
get_path()
simPaths = allPaths
for i in range(0,len(simPaths)):
	for j in range(0,len(simPaths[i])):
		simPaths[i][j] = simPaths[i][j]+random.randint(0,50)


for j in range(0,len(allPaths)):
	car_start(j)
