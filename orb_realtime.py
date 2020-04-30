import cv2, numpy as np

img1 = None
win_name = 'Camera Matching'
MIN_MATCH = 10

detector = cv2.ORB_create(1000)

matcher = cv2.BFMatcher(cv2.NORM_HAMMING, crossCheck=True)

cap = cv2.VideoCapture(0)              
cap.set(cv2.CAP_PROP_FRAME_WIDTH, 640)
cap.set(cv2.CAP_PROP_FRAME_HEIGHT, 480)

while cap.isOpened():       
    ret, frame = cap.read() 
    if img1 is None:  
        res = frame
    else:             
        img2 = frame
        gray1 = cv2.cvtColor(img1, cv2.COLOR_BGR2GRAY)
        gray2 = cv2.cvtColor(img2, cv2.COLOR_BGR2GRAY)
        
        kp1, desc1 = detector.detectAndCompute(gray1, None)
        kp2, desc2 = detector.detectAndCompute(gray2, None)
        
        matches = matcher.match(desc1,desc2)

        matches = sorted(matches, key = lambda x:x.distance)
        
        res = cv2.drawMatches(img1,kp1,img2,kp2,matches[:10], None, flags=2)
        
    
    cv2.imshow(win_name, res)
    key = cv2.waitKey(1)
    if key == 27:    
            break          
    elif key == ord(' '): 
        x,y,w,h = cv2.selectROI(win_name, frame, False)
        if w and h:
            img1 = frame[y:y+h, x:x+w]
else:
    print("can't open camera.")
cap.release()                          
cv2.destroyAllWindows()
