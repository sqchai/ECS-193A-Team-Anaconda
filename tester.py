import cv2
import imutils
from imutils import contours
import math
import numpy as np 



inchperpixel = None
width = 5.5
dest = (100,100)

draw = False # true if the mouse is pressed. 
a,b = -1,-1  
mousePath = np.empty((0,2), int)
allPaths = []


def apply_move(move):
    temp=[]

def getDiff():
    temp=[]

def analyze_path():
    temp=[]

def get_next_move(frame, prevFrame, i):
    i = i+1
    #temp=[]

def car_start():
    '''while(targetPath.getNextTarget() != NULL) {
        if(camera.ready() && car.ready()) {
            Move nextMove = getNextMove(img, camera.capture(), targetPath.getNextTarget())
            car.applyMove(nextMove)
        }
    }'''
    cap = cv2.VideoCapture(0)
    prevFrame = cap.read()
    outBound = False
    i=0
    while i !=len(allPaths)+1:
        frame = cap.read()
        nextMove = get_next_move(frame, prevFrame, i)
        i = i+1
        #car.apply_move(nextMove)


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
car_start()
